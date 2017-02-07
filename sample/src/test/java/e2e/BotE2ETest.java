package e2e;

import org.junit.Test;

import static com.pengrad.telegrambot.tester.BotTester.*;

public class BotE2ETest {

    @Test
    public void notCommandShouldReturnStubMessage() throws Exception {
        given().
            got("hello").
        then().
            shouldAnswer(message("answer to not command"));
    }

    @Test
    public void unknownCommandShouldInvokeDefaultAction() throws Exception {
        given().defaultAction((bot, message) -> bot.execute(message("help"))).
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