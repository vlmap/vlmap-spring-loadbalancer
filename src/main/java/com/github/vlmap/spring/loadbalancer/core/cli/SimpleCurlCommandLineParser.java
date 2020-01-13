package com.github.vlmap.spring.loadbalancer.core.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SimpleCurlCommandLineParser {
    public static final String HEADER = "header";
    public static final String PATH = "path";
    public static final String COOKIE = "cookie";
    public static final String PARAM = "param";
    public static final String JSON_PATH = "jsonpath";
    public static final String METHOD = "method";
    public static final String MODEL = "model";


    public static final String MODEL_ANY = "any";
    public static final String MODEL_ALL = "all";


    private MultiValueMap<String, String> headers = new LinkedMultiValueMap();

    private MultiValueMap<String, String> paths = new LinkedMultiValueMap();

    private MultiValueMap<String, String> cookies = new LinkedMultiValueMap();

    private MultiValueMap<String, String> params = new LinkedMultiValueMap();

    private MultiValueMap<String, String> jsonpath = new LinkedMultiValueMap();
    private MultiValueMap<String, String> methods = new LinkedMultiValueMap();

    private String cli;
    private String model = MODEL_ANY;


    public SimpleCurlCommandLineParser(String cli) {
        this.cli = cli;
    }

    Pattern pattern = Pattern.compile("--[a-zA-Z]+[=].[ ].[--]{0}");

    public void p(){
        Options options = new Options();
        options.addOption("H",HEADER,true,"");
System.in
    }

    public void parser() {
        StringBuilder builder = new StringBuilder(cli);
        while (true) {
            builder.indexOf("--");
            break;
        }
        List<String> args = new ArrayList<>();
        DefaultApplicationArguments arguments = new DefaultApplicationArguments(args.toArray(new String[0]));
        List<String> values = null;
        values = arguments.getOptionValues(HEADER);
        addAll(headers, HEADER, values);

        values = arguments.getOptionValues(PATH);
        addAll(paths, PATH, values);

        values = arguments.getOptionValues(COOKIE);
        addAll(cookies, COOKIE, values);

        values = arguments.getOptionValues(PARAM);
        addAll(params, PARAM, values);

        values = arguments.getOptionValues(JSON_PATH);
        addAll(jsonpath, JSON_PATH, values);

        values = arguments.getOptionValues(PATH);
        addAll(paths, PATH, values);

        values = arguments.getOptionValues(METHOD);

        addAll(methods, METHOD, values);

        values = arguments.getOptionValues(MODEL_ALL);
        if (!CollectionUtils.isEmpty(values)) {
            String model = values.get(0);
            if (StringUtils.isNotBlank(model)) {
                this.model = model;
            }
        }

    }

    protected void addAll(MultiValueMap<String, String> map, String key, List<String> values) {
        if (!CollectionUtils.isEmpty(values)) {
            for (String value : values) {
                map.add(key, value);
            }
        }
    }
}
