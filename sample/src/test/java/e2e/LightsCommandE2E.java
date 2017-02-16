package e2e;

import net.mamot.bot.commands.LightsCommand;
import net.mamot.bot.services.LightsService;
import net.mamot.bot.services.impl.BridgeFinder;
import net.mamot.bot.services.impl.HueLightsService;
import org.junit.Test;

import static com.pengrad.telegrambot.model.request.InlineKeyboardMarkup.*;
import static com.pengrad.telegrambot.tester.BotTester.given;
import static com.pengrad.telegrambot.tester.BotTester.message;
import static org.mockito.Mockito.mock;

public class LightsCommandE2E {

    private final BridgeFinder bridgeFinder = mock(BridgeFinder.class);
    private final LightsService lightsService = new HueLightsService(bridgeFinder);
    private final LightsCommand sut = new LightsCommand(lightsService);

    @Test
    public void lights_whenKnowsBridge_showsBridgeInfoAndAvailableLightModes() throws Exception {
        given(sut).
            got("/lights").
        then().
            shouldAnswer(
                message("Bridge: ").replyMarkup(
                    keyboard(
                            row(btn("off","off"),btn("on","on"))
                    )));
    }

    @Test
    public void lights_whenDoesNotKnowBridgeAndThereAreAvailableBridges_showsThem() throws Exception {
        given(sut).
            got("/lights").
        then().
            shouldAnswer(
                message("Choose bridge:").replyMarkup(
                    keyboard(
                            row(btn("Royal Philips Electronics Philips hue bridge 2012 929000226503","2f402f80-da50-11e1-9b23-0017881c3be3"))
                    )));
    }

    @Test
    public void lights_whenDoesNotKnowBridgeAndThereAreNoAvailableBridges_showsSorry() throws Exception {
        given(sut).
            got("/lights").
        then().
            shouldAnswer(message("Sorry. There are no available bridges..."));
    }
}
