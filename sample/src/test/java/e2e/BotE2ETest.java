package e2e;

import org.junit.Test;

import static com.pengrad.telegrambot.tester.BotTester.*;

public class BotE2ETest {

    @Test
    public void notCommandShouldReturnNothing() throws Exception {
        given().
            got("hello").
        then().
            noAnswer();
    }

    @Test
    public void unknownCommandShouldInvokeDefaultAction() throws Exception {
        given().defaultAction((bot, u) -> bot.execute(message("help")) != null).
            got("/someUnknownCommand").
        then().
            shouldAnswer(message("help"));
    }

    @Test
    public void oneCommandShouldBeProcessedOnce() throws Exception {
        given(command("/hello", bot -> bot.execute(message("Hi")))).
            got("/hello").
        then().
            shouldAnswer(message("Hi"));
    }

    @Test
    public void severalCommandsShouldBeProcessedSeveralTimes() throws Exception {
        given(command("/hello", bot -> bot.execute(message("Hi")))).
            got("/hello and /hello").
        then().
            shouldAnswer(message("Hi"), message("Hi"));
    }
}