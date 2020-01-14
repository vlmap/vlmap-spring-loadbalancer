import com.github.vlmap.spring.loadbalancer.core.cli.CommandLineTokenizer;
import com.github.vlmap.spring.loadbalancer.core.cli.GrayAttachCommandLineParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CurlCommandTest {
    @Test
    public void test() {
        String commandLine = "-A debug --header 'aaa:1' -H=b:2 -U /**  -M POST --json-path $.data[0]:a --cookie  cookie1:2 --param p1:1 --param p2:2 -S ";
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
