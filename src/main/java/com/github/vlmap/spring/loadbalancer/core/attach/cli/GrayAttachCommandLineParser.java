package com.github.vlmap.spring.loadbalancer.core.attach.cli;

import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class GrayAttachCommandLineParser {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String PATH = "path";

    public static final String METHOD = "method";

    public static final String HEADER = "header";
    public static final String COOKIE = "cookie";
    public static final String PARAM = "param";
    public static final String JSON_PATH = "json-path";

    public static final String HEADER_REGEX = "header-regex";
    public static final String COOKIE_REGEX = "cookie-regex";
    public static final String PARAM_REGEX = "param-regex";
    public static final String JSON_PATH_REGEX = "json-path-regex";


    public static final String VALUE = "value";

    Options options = new Options();

    public GrayAttachCommandLineParser() {

        initOptions();
    }


    public void initOptions() {
        Option.Builder builder = Option.builder().longOpt(VALUE).required().hasArg(true).desc("条件匹配时返回的灰度值");
        options.addOption(builder.build());

        builder = Option.builder().longOpt(PATH).hasArg(true).desc("PATH匹配，支持ANT格式的PATH. 示例：--" + PATH + "=/test/** ");
        options.addOption(builder.build());


        builder = Option.builder().longOpt(METHOD).hasArg(true).desc("Method匹配. 示例：--" + METHOD + "=POST --method  GET ");
        options.addOption(builder.build());


        builder = Option.builder().longOpt(HEADER).argName("name1=value1&name2=value2").hasArg(true).desc("HEADER匹配。示例：--" + HEADER + "=referer=https://www.github.com");
        options.addOption(builder.build());

        builder = Option.builder().longOpt(HEADER_REGEX).argName("name1=value1&name2=value2").hasArg(true).desc("HEADE正则匹配。示例：--" + HEADER_REGEX + "=referer=https://www.*");
        options.addOption(builder.build());

        builder = Option.builder().longOpt(COOKIE).argName("name1=value1&name2=value2").hasArg(true).desc("COOKIE匹配. 示例：--" + COOKIE + "=name1=value1&name2=value2");
        options.addOption(builder.build());

        builder = Option.builder().longOpt(COOKIE_REGEX).hasArg(true).desc("COOKIE正则匹配. 示例：--" + COOKIE_REGEX + "=name1=value1&name2=value2");
        options.addOption(builder.build());

        builder = Option.builder().longOpt(PARAM).argName("name1=value1&name2=value2").hasArg(true).desc("参数正则匹配. 示例：--" + PARAM + "=name1=value1&name2=value2");
        options.addOption(builder.build());

        builder = Option.builder().longOpt(PARAM_REGEX).argName("name1=value1&name2=value2").hasArg(true).desc("参数正则匹配. 示例：--" + PARAM_REGEX + "=name1=value1&name2=value2");
        options.addOption(builder.build());

        builder = Option.builder().longOpt(JSON_PATH).argName("name1=value1&name2=value2").hasArg(true).desc("JsonPath匹配. 示例：--" + JSON_PATH_REGEX + "=$.data.el[0]=abc&$.data.el[0]:abc");
        options.addOption(builder.build());


        builder = Option.builder().longOpt(JSON_PATH_REGEX).argName("name1=value1&name2=value2").hasArg(true).desc("JsonPath正则匹配. 示例：--" + JSON_PATH_REGEX + "=$.data.el[0]=abc&$.data.el[0]:abc");
        options.addOption(builder.build());


    }

    public GaryAttachParamater parser(String command) {


        CommandLineTokenizer tokenizer = new CommandLineTokenizer(command);
        List<String> args = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            args.add(tokenizer.nextToken());
        }
        CommandLine commandLine = null;
        try {
            commandLine = new DefaultParser().parse(options, args.toArray(new String[0]));

        } catch (Exception e) {
            logger.error("parse commandline error:" + e.getMessage());
            logger.info(getHelpString());
        }

        GaryAttachParamater result = new GaryAttachParamater();
        result.setPath(commandLine.getOptionValue(PATH));
        result.setMethod(commandLine.getOptionValue(METHOD));
        result.setValue(commandLine.getOptionValue(VALUE));

        String[] values = null;
        Charset charset = Charset.forName("utf-8");

        values = commandLine.getOptionValues(HEADER);
        addAllMultiValue(result.getHeaders(), values, charset);

        values = commandLine.getOptionValues(HEADER_REGEX);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        addAllMultiValue(map, values, charset);
        result.getHeadersRegex().addAll(pattern(map));


        values = commandLine.getOptionValues(COOKIE);
        addAllMultiValue(result.getCookies(), values, charset);

        values = commandLine.getOptionValues(COOKIE_REGEX);
        map = new LinkedMultiValueMap<>();
        addAllMultiValue(map, values, charset);
        result.getCookiesRegex().addAll(pattern(map));


        values = commandLine.getOptionValues(PARAM);
        addAllMultiValue(result.getParams(), values, charset);

        values = commandLine.getOptionValues(PARAM_REGEX);
        map = new LinkedMultiValueMap<>();
        addAllMultiValue(map, values, charset);
        result.getParamsRegex().addAll(pattern(map));


        values = commandLine.getOptionValues(JSON_PATH);
        addAll(result.getJsonpath(), values, charset);

        values = commandLine.getOptionValues(JSON_PATH_REGEX);
        map = new LinkedMultiValueMap<>();
        addAllMultiValue(map, values, charset);
        result.getJsonpathRegex().putAll(pattern(map).toSingleValueMap());


        return result;

    }

    protected MultiValueMap<String, Pattern> pattern(MultiValueMap<String, String> input) {
        MultiValueMap<String, Pattern> result = new LinkedMultiValueMap<>();
        for (Map.Entry<String, List<String>> entry : input.entrySet()) {
            for (String value : entry.getValue()) {
                result.add(entry.getKey(), Pattern.compile(value));

            }
        }
        return result;

    }

    /**
     * get string of help usage
     *
     * @return help string
     */
    public String getHelpString() {

        HelpFormatter helpFormatter = new HelpFormatter();

        String help = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
        try {
            helpFormatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "格式说明,key=value 中的value需要进行URLEncoder编码,多个值可以用&连接", null,
                    options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
            printWriter.flush();
            help = new String(byteArrayOutputStream.toByteArray());
        } finally {

            IOUtils.closeQuietly(printWriter);
            IOUtils.closeQuietly(byteArrayOutputStream);

        }
        return help;


    }

    protected void addAllMultiValue(MultiValueMap<String, String> map, String[] values, Charset charset) {

        if (ArrayUtils.isNotEmpty(values)) {

            for (String value : values) {
                MultiValueMap<String, String> paramater = parseFormData(charset, value);
                map.addAll(paramater);

            }
        }
    }

    protected void addAll(Map<String, String> map, String[] values, Charset charset) {

        if (ArrayUtils.isNotEmpty(values)) {

            for (String value : values) {
                MultiValueMap<String, String> paramater = parseFormData(charset, value);
                map.putAll(paramater.toSingleValueMap());

            }
        }
    }

    private MultiValueMap<String, String> parseFormData(Charset charset, String body) {
        String[] pairs = org.springframework.util.StringUtils.tokenizeToStringArray(body, "&");
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
        try {
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx == -1) {
                    result.add(URLDecoder.decode(pair, charset.name()), null);
                } else {
                    String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
                    result.add(name, value);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
        return result;
    }
}
