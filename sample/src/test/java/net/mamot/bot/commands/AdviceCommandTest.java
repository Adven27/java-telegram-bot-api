package net.mamot.bot.commands;

import net.mamot.bot.services.impl.AdvicePrinter;
import net.mamot.bot.services.impl.AdviceResource;
import net.mamot.bot.services.impl.MessageFromURL;
import org.junit.Test;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.tester.BotTester.*;
import static java.util.Collections.singletonMap;
import static net.mamot.bot.services.Stickers.ALONE;
import static net.mamot.bot.services.Stickers.BLA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdviceCommandTest {

    private final AdviceResource resource = mock(AdviceResource.class);

    @Test
    public void shouldReturnMessageFromAdviceResource() throws Exception {
        when(resource.fetch()).thenReturn(singletonMap("text", "fucking great advice"));

        given(new AdviceCommand(new MessageFromURL(resource, new AdvicePrinter()))).
            got("/advice").
        then().
            shouldAnswer(sticker(BLA.id()),
                         message("fucking great advice").disableWebPagePreview(true).parseMode(HTML));
    }

    @Test
    public void shouldReturnApologize_IfAdviceResourceUnreachable() throws Exception {
        when(resource.fetch()).thenThrow(new RuntimeException());

        given(new AdviceCommand(new MessageFromURL(resource, new AdvicePrinter()))).
            got("/advice").
        then().
            shouldAnswer(sticker(ALONE.id()),
                         message("Связь с ноосферой потеряна...").disableWebPagePreview(true).parseMode(HTML));
    }
}
