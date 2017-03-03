package net.mamot.bot.commands;

import com.google.common.collect.ImmutableMap;
import net.mamot.bot.services.impl.MessageFromURL;
import net.mamot.bot.services.joke.impl.JokePrinter;
import net.mamot.bot.services.joke.impl.JokeResource;
import org.junit.Test;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.tester.BotTester.*;
import static net.mamot.bot.commands.JokeCommand.COMMAND;
import static net.mamot.bot.services.Stickers.ALONE;
import static net.mamot.bot.services.Stickers.LOL;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JokeCommandTest {

    private final JokeResource resource = mock(JokeResource.class);
    private JokeCommand sut = new JokeCommand(new MessageFromURL(resource, new JokePrinter()));

    @Test
    public void shouldReturnMessageFromResource() throws Exception {
        when(resource.fetch()).thenReturn(ImmutableMap.of("text", "very funny joke", "site", "site"));

        given(sut).
            got(COMMAND).
        then().
            shouldAnswer(sticker(LOL.id()),
                         message("very funny joke \n site").disableWebPagePreview(true).parseMode(HTML));
    }

    @Test
    public void shouldReturnApologize_IfResourceUnreachable() throws Exception {
        when(resource.fetch()).thenThrow(new RuntimeException());

        given(sut).
            got(COMMAND).
        then().
            shouldAnswer(sticker(ALONE.id()),
                         message("Связь с ноосферой потеряна...").disableWebPagePreview(true).parseMode(HTML));
    }
}
