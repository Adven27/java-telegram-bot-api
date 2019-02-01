package net.mamot.bot.commands;

import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.printer.PublicationPrinter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.pengrad.telegrambot.tester.BotTester.*;
import static net.mamot.bot.commands.UncleBobCommand.COMMAND;
import static net.mamot.bot.commands.UncleBobCommand.COMMAND_FORMAT_ERROR_MESSAGE;
import static net.mamot.bot.services.Stickers.HELP;
import static net.mamot.bot.services.Stickers.THINK;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UncleBobCommandTest {

    private static final String MESSAGE = "entry";

    private UncleBobCommand sut;

    @BeforeMethod
    public void setUp() {
        PublicationPrinter printer = mock(PublicationPrinter.class);
        when(printer.print(any(Entry.class))).thenReturn(MESSAGE + "1", MESSAGE + "2", MESSAGE + "3");

        sut = new UncleBobCommand(printer);
    }

    @Test
    public void shouldPostLatestArticle() {
        given(sut)
            .got(COMMAND)
        .then()
            .shouldAnswer(
                    sticker(THINK.id()),
                    message("entry1"));
    }

    @Test
    public void shouldPostRequestedNumberOfLatestArticles() {
        given(sut)
            .got(COMMAND + " 3")
        .then()
            .shouldAnswer(
                    sticker(THINK.id()),
                    message("entry1"),
                    message("entry2"),
                    message("entry3")
            );
    }


    @Test(dataProvider = "nonPositive")
    public void shouldAskForPositiveNumber_IfRequestedNonPositive(String number) {
        given(sut)
            .got(COMMAND + " " + number)
        .then()
            .shouldAnswer(
                    sticker(HELP.id()),
                    message(COMMAND_FORMAT_ERROR_MESSAGE));
    }

    @DataProvider(name = "nonPositive")
    public Object[][] getNonPositive() {
        return new String[][] {{null}, {"-1"}, {"0"}, {"дайте два"} };
    }

}