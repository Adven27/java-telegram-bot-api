package net.mamot.bot.commands;

import org.junit.Ignore;
import org.junit.Test;

import static com.pengrad.telegrambot.tester.BotTester.given;
import static com.pengrad.telegrambot.tester.BotTester.message;
import static net.mamot.bot.commands.TicTacToeCommand.COMMAND;

public class TicTacToeCommandTest {
    private TicTacToeCommand sut = new TicTacToeCommand();

    @Test @Ignore //TODO write tests!
    public void shouldReturnMessageFromAdviceResource() throws Exception {
        given(sut).
            got(COMMAND).
        then().
            shouldAnswer(message("JUST WRITE THEM! YESTERDAY YOU SAID TOMORROW!"));
    }

}
