package e2e;

import net.mamot.bot.commands.LightsCommand;
import net.mamot.bot.services.LightsService;
import org.junit.Test;

import static com.pengrad.telegrambot.tester.BotTester.*;
import static net.mamot.bot.services.Stickers.BLA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LightsCommandE2E {

    private final LightsService lightsService = mock(LightsService.class);
    private final LightsCommand sut = new LightsCommand(lightsService);

    @Test
    public void shouldReturnDoneMessageAndInvokeTurnOffMethod() throws Exception {
        given(sut).
            got("/lights").
        then().
            shouldAnswer(sticker(BLA.id()),
                         message("Done"));

        verify(lightsService).turnOffAll();
    }
}
