import com.github.vlmap.spring.loadbalancer.core.attach.MatcherProcess;
import com.github.vlmap.spring.loadbalancer.core.attach.SimpleRequestData;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GrayAttachCommandLineParser;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttachMatchTest {
    @Test
    public void test() throws UnsupportedEncodingException {
        List<GaryAttachParamater> paramaters=new ArrayList<>();
        GrayAttachCommandLineParser parser = new GrayAttachCommandLineParser();

        StringBuilder builder = new StringBuilder();
         builder.append(" ").append("--value debug");
        builder.append(" ").append("--method POST");
        builder.append(" ").append("--path /**");

        builder.append(" ").append("--header=h1=1&h1=2&h2=2");
        builder.append(" ").append("--param p1=1&p1=2&p2=2");
        builder.append(" ").append("--json-path =$.a=1&$.b=abcde");


         paramaters.add(parser.parser(builder.toString()));

        builder = new StringBuilder();
        builder.append(" ").append("--value debug1");
        builder.append(" ").append("--method POST");
        builder.append(" ").append("--path /hello/{a}/{b}/{c}/**");

        builder.append(" ").append("--header h1=1&h1=2&h2=2");
        builder.append(" ").append("--param p1=1&p1=2&p2=2");
        builder.append(" ").append("--json-path  $.a=1&$.b=abcde");
        builder.append(" ").append("--json-path-regex  $.a=").append(URLEncoder.encode("\\d+","utf-8")).append("&$.b=\\w%2B");


        paramaters.add(parser.parser(builder.toString()));

        MatcherProcess matcherProcess = new MatcherProcess();
        SimpleRequestData data = new SimpleRequestData();
        data.setPath("/hello/test/a/b");
        data.setMethod("POST");
        data.setContentType("application/json;charset=utf-8");
        data.setBody("{\"a\":1,\"b\":\"abcde\"}");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("content-type", "application/json;charset=utf-8");
        map.add("h1", "1");
        map.add("h1", "2");
        map.add("h2", "2");

        map.add("content-type", "application/json;charset=utf-8");

        data.setHeaders(map);

        map = new LinkedMultiValueMap<>();
        map.add("p1", "1");
        map.add("p1", "2");
        map.add("p2", "2");
        data.setParams(map);

        map = new LinkedMultiValueMap<>();
        map.add("c1", "1");
        map.add("c1", "2");
        map.add("c2", "2");
        data.setCookies(map);


        GaryAttachParamater paramater = matcherProcess.match(data, paramaters);
        if(paramater!=null){
            Assert.assertEquals(paramater.getValue(),"debug1");

        }
     }
}
