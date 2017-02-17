package net.mamot.bot.commands;

import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import net.mamot.bot.services.BridgeAdapter;
import net.mamot.bot.services.impl.FakeHueBridge;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.pengrad.telegrambot.model.request.InlineKeyboardMarkup.*;
import static com.pengrad.telegrambot.tester.BotTester.given;
import static com.pengrad.telegrambot.tester.BotTester.message;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LightsCommandTest {

    private final FakeHueBridge fakeBridge = new FakeHueBridge("1", "My Bridge 1", "url1");
    private final FakeHueBridge anotherFakeBridge = new FakeHueBridge("2", "My Bridge 2", "url2");
    private final BridgeAdapter bridgeAdapter = mock(BridgeAdapter.class);
    private final LightsCommand sut = new LightsCommand(bridgeAdapter);

    @Test
    public void whenOnlyOneAvailableBridge_setAsCurrentAndShowOptions() throws Exception {
        when(bridgeAdapter.search()).thenReturn(singletonList(fakeBridge));
        given(sut).
            got("/lights").
        then().
            shouldAnswer(
                message("Bridge: " + fakeBridge.desc()).replyMarkup(
                        keyboard(row(btn("off","off"),
                                     btn("on","on"))
                        )));
    }

    @Test
    public void whenSeveralAvailableBridges_askToChooseOne() throws Exception {
        when(bridgeAdapter.search()).thenReturn(asList(fakeBridge, anotherFakeBridge));
        given(sut).
            got("/lights").
        then().
            shouldAnswer(
                message("Choose bridge:").replyMarkup(
                        keyboard(row(btn(fakeBridge.desc(), fakeBridge.id()),
                                     btn(anotherFakeBridge.desc(), anotherFakeBridge.id()))
                        )));
    }

    @Test
    public void whenNoAvailableBridges_sorry() throws Exception {
        when(bridgeAdapter.search()).thenReturn(emptyList());

        given(sut).
            got("/lights").
        then().
            shouldAnswer(message("Sorry. There are no available bridges..."));
    }

    @Test
    public void givenAvailableBridge_whenChooseOptionAllOff_bridgeShouldTryToTurnOffAll() throws Exception {
        when(bridgeAdapter.search()).thenReturn(singletonList(fakeBridge));
        List<UpdateHandler> handlers = new ArrayList<>();
        handlers.add(sut);
        given(handlers, sut).
                gotCallback("off").
            then().
                shouldAnswer(
                        message("Bridge: " + fakeBridge.desc()).replyMarkup(
                                keyboard(row(btn("off","off"),
                                        btn("on","on"))
                                )));
    }
}