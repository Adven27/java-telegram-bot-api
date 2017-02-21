package net.mamot.bot.commands;

import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import net.mamot.bot.services.BridgeAdapter;
import net.mamot.bot.services.impl.FakeHueBridge;
import org.junit.Test;

import static com.pengrad.telegrambot.model.request.InlineKeyboardMarkup.*;
import static com.pengrad.telegrambot.tester.BotTester.given;
import static com.pengrad.telegrambot.tester.BotTester.message;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

public class LightsCommandTest {

    private final FakeHueBridge fakeBridge = spy(new FakeHueBridge("1", "My Bridge 1", "url1"));
    private final FakeHueBridge anotherFakeBridge = spy(new FakeHueBridge("2", "My Bridge 2", "url2"));
    private final BridgeAdapter bridgeAdapter = mock(BridgeAdapter.class);
    private LightsCommand sut = new LightsCommand(bridgeAdapter);

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

        given(new LightsCommand(bridgeAdapter, fakeBridge)).
            gotCallback("off").
        then().
            shouldAnswer(new AnswerCallbackQuery(null).text("Готово"));

        verify(fakeBridge).turnOffAll();
        verifyNoMoreInteractions(fakeBridge);
    }
}