package net.mamot.bot.commands;

import com.google.common.collect.ImmutableMap;
import net.mamot.bot.services.impl.MessageFromURL;
import net.mamot.bot.services.quote.impl.QuotePrinter;
import net.mamot.bot.services.quote.impl.QuoteResource;
import org.junit.Test;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.tester.BotTester.*;
import static net.mamot.bot.commands.QuoteCommand.COMMAND;
import static net.mamot.bot.services.Stickers.ALONE;
import static net.mamot.bot.services.Stickers.THINK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuoteCommandTest {

    private final QuoteResource resource = mock(QuoteResource.class);
    private QuoteCommand sut = new QuoteCommand(new MessageFromURL(resource, new QuotePrinter()));

    @Test
    public void shouldReturnMessageFromResource() throws Exception {
        when(resource.fetch()).thenReturn(ImmutableMap.of("text", "very smart quote", "author", "Smart Guy", "link", "link"));

        given(sut).
            got(COMMAND).
        then().
            shouldAnswer(sticker(THINK.id()),
                         message("very smart quote \n\n Smart Guy (link)").disableWebPagePreview(true).parseMode(HTML));
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
