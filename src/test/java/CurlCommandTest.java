import com.github.vlmap.spring.loadbalancer.core.attach.cli.CommandLineTokenizer;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GrayAttachCommandLineParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CurlCommandTest {
    @Test
    public void test() {
        String commandLine = "-V debug --header 'aaa:1' -H=b:2 --path /**  -M POST --json-path $.data[0]:a --cookie  cookie1:2 --param p1:1 --param p2:2  ";
        CommandLineTokenizer tokenizer = new CommandLineTokenizer(commandLine);
        List<String> args=new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            args.add( tokenizer.nextToken());
        }
        GrayAttachCommandLineParser parser=new GrayAttachCommandLineParser();
        parser.parser(commandLine);


        System.out.println("");
    }
}
