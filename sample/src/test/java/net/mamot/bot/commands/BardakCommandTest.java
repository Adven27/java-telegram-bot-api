package net.mamot.bot.commands;

import net.mamot.bot.services.bardak.BardakMenu;
import org.junit.Test;

import static com.pengrad.telegrambot.tester.BotTester.given;
import static com.pengrad.telegrambot.tester.BotTester.message;
import static net.mamot.bot.commands.BardakCommand.COMMAND;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BardakCommandTest {

    private final BardakMenu menu = mock(BardakMenu.class);
    private BardakCommand sut = new BardakCommand(menu);

    @Test
    public void shouldReturnMenuForToday() throws Exception {
        when(menu.today()).thenReturn("джинжер виски бесплатно!");

        given(sut).
            got(COMMAND).
        then().
            shouldAnswer(message("джинжер виски бесплатно!"));
    }

}