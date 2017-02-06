package e2e;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.Test;

import static tester.BotTester.got;

public class BotE2ETest {

    @Test
    public void notCommand() throws Exception {
        got("hello").then().
            answer(new SendMessage(1, "???"));
    }

    @Test
    public void command() throws Exception {
        got("/hello").then().
            answer(new SendMessage(1, "answer for command"));
    }
    
    @Test
    public void twoCommands() throws Exception {
        got("/first and /second").then().
            answer(new SendMessage(1, "answer for command"));
    }
}
