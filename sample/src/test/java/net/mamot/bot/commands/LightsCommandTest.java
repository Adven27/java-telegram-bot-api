package net.mamot.bot.commands;

import net.mamot.bot.services.LightsService;
import net.mamot.bot.services.LightsService.BridgeInfo;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.pengrad.telegrambot.model.request.InlineKeyboardMarkup.*;
import static com.pengrad.telegrambot.tester.BotTester.given;
import static com.pengrad.telegrambot.tester.BotTester.message;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LightsCommandTest {

    private final LightsService lightsService = mock(LightsService.class);
    private final LightsCommand sut = new LightsCommand(lightsService);

    @Test
    public void lights_whenKnowsBridge_showsBridgeInfoAndAvailableLightModes() throws Exception {
        final BridgeInfo currentBridge = new BridgeInfo("id", "My Bridge", "url");
        when(lightsService.currentBridge()).thenReturn(Optional.of(currentBridge));

        given(sut).
            got("/lights").
        then().
            shouldAnswer(
                message("Bridge: " + currentBridge.desc()).replyMarkup(
                        keyboard(
                                row(btn("off","off"),btn("on","on"))
                        )));
    }

    @Test
    public void lights_whenDoesNotKnowBridgeAndThereAreAvailableBridges_showsThem() throws Exception {
        List<BridgeInfo> availableBridges = asList(new BridgeInfo("1", "bridge 1", "url"), new BridgeInfo("2", "bridge 2", "url"));
        when(lightsService.currentBridge()).thenReturn(Optional.empty());
        when(lightsService.searchBridges()).thenReturn(availableBridges);

        given(sut).
            got("/lights").
        then().
            shouldAnswer(
                message("Choose bridge:").replyMarkup(
                        keyboard(
                                row(btn("bridge 1","1"),btn("bridge 2","2"))
                        )));
    }

    @Test
    public void lights_whenDoesNotKnowBridgeAndThereAreNoAvailableBridges_showsSorry() throws Exception {
        when(lightsService.currentBridge()).thenReturn(Optional.empty());
        when(lightsService.searchBridges()).thenReturn(emptyList());

        given(sut).
            got("/lights").
        then().
            shouldAnswer(message("Sorry. There are no available bridges..."));
    }

}
