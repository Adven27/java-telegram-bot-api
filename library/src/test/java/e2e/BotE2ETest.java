package e2e;

import com.pengrad.telegrambot.request.SendMessage;
import org.junit.Test;

import static e2e.BotTester.given;

public class BotE2ETest {


    @Test
    public void notCommand() throws Exception {
        given().text("hello").then().shouldHave(new SendMessage(1, "???"));
    }

    @Test
    public void command() throws Exception {
        given().text("/hello").then().shouldHave(new SendMessage(1, "answer for command"));
    }
}
