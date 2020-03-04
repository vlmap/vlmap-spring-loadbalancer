package com.github.vlmap.spring.loadbalancer.core.platform;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.regex.Pattern;

public class MatcherProcess {
    protected AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 匹配标签后的值返回到 result
     *
     * @param data
     * @param paramaters
     * @return
     */
    public RequestMatchParamater match(SimpleRequest data, Object jsonDocument, Collection<RequestMatchParamater> paramaters) {
        List<RequestMatchParamater> list = new ArrayList<>();
        String path = data.getPath();

        for (RequestMatchParamater paramater : paramaters) {
            if (this.match(data, jsonDocument, paramater)) {
                list.add(paramater);
            }


        }
        if (list.size() > 1) {
            List<RequestMatchParamater> temp = new ArrayList<>();

            for (RequestMatchParamater paramater : list) {
                if (StringUtils.isNotBlank(paramater.getPath())) {
                    temp.add(paramater);
                }
            }
            if (CollectionUtils.isNotEmpty(temp)) {
                list = temp;
            }
            this.sort(list, path);

        }

        return list.isEmpty() ? null : list.get(0);


    }

    protected void sort(List<RequestMatchParamater> list, String path) {
        Comparator<String> comparator = pathMatcher.getPatternComparator(path);

        list.sort(new RequestMatchParamaterComparator(comparator));

    }

    protected boolean match(SimpleRequest data, Object jsonDocument, RequestMatchParamater paramater) {


        boolean state = false;

        state = matchPath(paramater.getPath(), data.getPath());
        if (!state) {
            return false;
        }
        state = matchMethod(paramater.getMethod(), data.getMethod());
        if (!state) {
            return false;
        }
        state = container(data.getCookies(), paramater.getCookies());
        if (!state) {
            return false;
        }
        state = matchRegex(data.getCookies(), paramater.getCookiesRegex());
        if (!state) {
            return false;
        }
        state = container(data.getHeaders(), paramater.getHeaders());
        if (!state) {
            return false;
        }

        state = matchRegex(data.getHeaders(), paramater.getHeadersRegex());
        if (!state) {
            return false;
        }
        state = container(data.getParams(), paramater.getParams());

        if (!state) {
            return false;
        }


        state = matchRegex(data.getParams(), paramater.getParamsRegex());
        if (!state) {
            return false;
        }
        state = matchJson(paramater.getJsonpath(), jsonDocument);

        if (!state) {
            return false;
        }

        state = matchJsonRegex(paramater.getJsonpathRegex(), jsonDocument);
        if (!state) {
            return false;
        }

        state = matchBody(paramater.getBody(), data.getBody());
        if (!state) {
            return false;
        }
        state = matchBodyRegex(paramater.getBodyRegex(), data.getBody());

        return state;
    }

    protected boolean matchJsonRegex(Map<String, Pattern> jsonpathRegex, Object document) {
        if (MapUtils.isNotEmpty(jsonpathRegex)) {
            if (document != null) {
                for (Map.Entry<String, Pattern> entry : jsonpathRegex.entrySet()) {
                    String path = entry.getKey();
                    try {
                        Object object = JsonPath.read(document, path);
                        Pattern pattern = entry.getValue();
                        if (!pattern.matcher(ObjectUtils.toString(object)).matches()) {
                            return false;
                        }

                    } catch (Exception e) {
                        return false;
                    }

                }


            } else {
                return false;
            }
        }
        return true;

    }

    protected boolean matchJson(Map<String, String> jsonpaths, Object document) {
        if (MapUtils.isNotEmpty(jsonpaths)) {
            if (document != null) {
                for (Map.Entry<String, String> entry : jsonpaths.entrySet()) {
                    String path = entry.getKey();
                    try {
                        Object object = JsonPath.read(document, path);
                        if (!StringUtils.equals(ObjectUtils.toString(object), ObjectUtils.toString(entry.getValue()))) {
                            return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }

                }


            } else {
                return false;
            }
        }
        return true;

    }

    protected boolean matchPath(String pattern, String path) {
        if (StringUtils.isNotBlank(pattern)) {

            if (pathMatcher.match(pattern, path)) {
                return true;
            }

            return false;

        }
        return true;
    }

    protected boolean matchMethod(String method, String input) {
        if (StringUtils.isBlank(method)) return true;
        return StringUtils.equals(method, input);

    }

    protected boolean matchRegex(MultiValueMap<String, String> parent, MultiValueMap<String, Pattern> pattern) {
        if (MapUtils.isNotEmpty(pattern)) {
            if (MapUtils.isNotEmpty(parent)) {
                for (Map.Entry<String, List<Pattern>> entry : pattern.entrySet()) {
                    List<Pattern> values = entry.getValue();

                    List<String> list = parent.get(entry.getKey());
                    for (Pattern regex : values) {
                        boolean state = false;
                        for (String value : list) {
                            state = regex.matcher(value).matches();
                            if (state) {
                                break;
                            }
                        }
                        if (!state) return false;
                    }


                }
                return true;
            }
            return false;


        }
        return true;
    }

    protected boolean container(MultiValueMap<String, String> parent, MultiValueMap<String, String> child) {
        if (MapUtils.isNotEmpty(child)) {
            if (MapUtils.isNotEmpty(parent)) {
                for (Map.Entry<String, List<String>> entry : child.entrySet()) {
                    List<String> values = entry.getValue();
                    if (CollectionUtils.isNotEmpty(values)) {
                        List<String> list = parent.get(entry.getKey());
                        if (CollectionUtils.isNotEmpty(list)) {
                            boolean isSub = CollectionUtils.isSubCollection(values, list);
                            if (CollectionUtils.isNotEmpty(list) && isSub) {
                                return true;
                            }
                        }

                    }


                }
            }
            return false;


        }
        return true;
    }

    protected boolean matchBody(String body, String input) {
        if (StringUtils.isBlank(body)) return true;
        return StringUtils.equals(body, input);

    }

    protected boolean matchBodyRegex(List<Pattern> bodyRegex, String input) {
        if (CollectionUtils.isNotEmpty(bodyRegex)) {
            if (input != null) {
                for (Pattern pattern : bodyRegex) {
                    try {

                        if (!pattern.matcher(input).matches()) {
                            return false;
                        }

                    } catch (Exception e) {
                        return false;
                    }

                }


            } else {
                return false;
            }
        }
        return true;

    }
}
