package e2e;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import net.mamot.bot.commands.LightsCommand;
import net.mamot.bot.services.LightsService;
import org.junit.Test;

import static com.pengrad.telegrambot.tester.BotTester.*;
import static net.mamot.bot.services.Stickers.BLA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LightsCommandE2E {

    private final LightsService lightsService = mock(LightsService.class);
    private final LightsCommand sut = new LightsCommand(lightsService);
/*
    @Test
    public void shouldReturnDoneMessageAndInvokeTurnOffMethod() throws Exception {
        given(sut).
            got("/lights").
        then().
            shouldAnswer(sticker(BLA.id()),
                         message("Done"));

        verify(lightsService).turnOffAll();
    }*/

    @Test
    public void lights_whenKnowsBridge_showsBridgeInfoAndAvailableLightModes() throws Exception {
        final LightsService.BridgeInfo expectedInfo = new LightsService.BridgeInfo("My Bridge");
        when(lightsService.bridgeInfo()).thenReturn(expectedInfo);

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("off").callbackData("off"),
                        new InlineKeyboardButton("on").callbackData("on"),
                });

        given(sut).
            got("/lights").
        then().
            shouldAnswer(message("Bridge: " + expectedInfo.desc()).replyMarkup(inlineKeyboard));
    }

    @Test
    public void lights_whenDoesNotKnowBridge_showsAvailableBridges() throws Exception {
        given(sut).
            got("/lights").
        then().
            shouldAnswer(sticker(BLA.id()), message("Done"));

        verify(lightsService).turnOffAll();
    }
}
