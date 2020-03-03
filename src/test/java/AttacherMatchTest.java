import com.fasterxml.jackson.databind.ObjectMapper;
 import com.github.vlmap.spring.loadbalancer.core.platform.MatcherProcess;
import com.github.vlmap.spring.loadbalancer.core.platform.RequestMatchParamater;
import com.github.vlmap.spring.loadbalancer.core.platform.SimpleRequest;
import com.github.vlmap.spring.loadbalancer.util.RequestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AttacherMatchTest {
    @Test
    public void test() throws IOException {
        List<RequestMatchParamater> paramaters = new ArrayList<>();
         ObjectMapper mapper = new ObjectMapper();
        RequestMatchParamater p = mapper.readValue("{\"headersRegex\":{\"a\":[\"1\",\"2\"]}}", RequestMatchParamater.class);



        MatcherProcess matcherProcess = new MatcherProcess();
        SimpleRequest data = new SimpleRequest();
        data.setPath("/hello/test/a/b");
        data.setMethod("POST");
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
        Object jsonDocument = RequestUtils.getJsonDocument(data);


        RequestMatchParamater paramater = matcherProcess.match(data, jsonDocument, paramaters);
        if (paramater != null) {
            Assert.assertEquals(paramater.getValue(), "debug1");

        }
    }
}
