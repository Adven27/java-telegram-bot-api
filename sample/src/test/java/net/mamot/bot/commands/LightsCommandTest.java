package net.mamot.bot.commands;

import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import net.mamot.bot.services.BridgeAdapter;
import net.mamot.bot.services.HueBridge;
import net.mamot.bot.services.impl.FakeHueBridge;
import org.junit.Test;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.keyboard;
import static com.pengrad.telegrambot.tester.BotTester.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static net.mamot.bot.commands.LightsCommand.CALLBACK_OFF;
import static net.mamot.bot.commands.LightsCommand.CALLBACK_ON;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class LightsCommandTest {
    private final FakeHueBridge fakeBridge = spy(new FakeHueBridge("1", "My Bridge 1", "url1"));
    private final FakeHueBridge anotherFakeBridge = spy(new FakeHueBridge("2", "My Bridge 2", "url2"));
    private final FakeHueBridge yetAnotherFakeBridge = spy(new FakeHueBridge("3", "My Bridge 3", "url3"));
    private final BridgeAdapter bridgeAdapter = mock(BridgeAdapter.class);
    private TestDoubleCommand sut = new TestDoubleCommand(bridgeAdapter);
    private SendMessage sorryNoBridgesMessage = message("Sorry. There are no available bridges...");
    private AnswerCallbackQuery doneCallbackAnswer = new AnswerCallbackQuery(null).text("Готово");

    @Test
    public void r2_whenOnlyOneAvailableBridge_setAsCurrentAndShowOptions() throws Exception {
        when(bridgeAdapter.search()).thenReturn(singletonList(fakeBridge));

        given(sut).
            got("/lights").
        then(sut).
            shouldAnswer(message("Bridge: " + fakeBridge.desc()).replyMarkup(
                keyboard().row("off", CALLBACK_OFF, "on", CALLBACK_ON).build()
            ));
    }

    @Test
    public void r3_whenSeveralAvailableBridges_askToChooseOne() throws Exception {
        when(bridgeAdapter.search()).thenReturn(asList(fakeBridge, anotherFakeBridge));

        given(sut).
            got("/lights").
        then(sut).
            shouldAnswer(message("Choose bridge:").replyMarkup(
                keyboard().row(fakeBridge.desc(), fakeBridge.id(), anotherFakeBridge.desc(), anotherFakeBridge.id()).build()
            ));
    }

    @Test
    public void r1_whenNoAvailableBridges_sorry() throws Exception {
        when(bridgeAdapter.search()).thenReturn(emptyList());

        given(sut).
            got("/lights").
        then().
            shouldAnswer(sorryNoBridgesMessage);

        verify(bridgeAdapter).search();
        verifyNoMoreInteractions(bridgeAdapter);
    }

    @Test
    public void r31_whenBridgeWasChosen_setItAndInformUser() throws Exception {
        when(bridgeAdapter.search()).thenReturn(asList(fakeBridge, anotherFakeBridge));

        givenGotCallbackFor(sut, fakeBridge.id()).then().shouldAnswer(doneCallbackAnswer);

        assertEquals(fakeBridge.id(), sut.bridge().id());
        verify(bridgeAdapter).search();
        verifyNoMoreInteractions(bridgeAdapter);
    }

    @Test
    public void r32_whenBridgeWasChosen_butItDisappearedInformUserAndAskToChooseAgain() throws Exception {
        when(bridgeAdapter.search()).thenReturn(singletonList(anotherFakeBridge));

        givenGotCallbackFor(sut, fakeBridge.id()).then(sut).shouldAnswer(
                message("Em... Bridge " + fakeBridge.id() + " suddenly disappeared... Choose again:").replyMarkup(
                        keyboard().row(anotherFakeBridge.desc(), anotherFakeBridge.id()).build()
                ));

        assertEquals(sut.bridge(), null);
        verify(bridgeAdapter).search();
        verifyNoMoreInteractions(bridgeAdapter);
    }

    @Test
    public void r41_givenActiveBridge_whenOptionAllOffWasChosen_bridgeShouldTryToTurnOffAll() throws Exception {
        sut.withBridge(fakeBridge);

        givenGotCallbackFor(sut, CALLBACK_OFF).then().shouldAnswer(doneCallbackAnswer);

        verify(fakeBridge).turnOffAll();
        verifyNoMoreInteractions(bridgeAdapter);
        verifyNoMoreInteractions(fakeBridge);
    }

    @Test
    public void r42_givenActiveBridge_whenOptionAllOnWasChosen_bridgeShouldTryToTurnOnAll() throws Exception {
        sut.withBridge(fakeBridge);

        givenGotCallbackFor(sut, CALLBACK_ON).then().shouldAnswer(doneCallbackAnswer);

        verify(fakeBridge).turnOnAll();
        verifyNoMoreInteractions(bridgeAdapter);
        verifyNoMoreInteractions(fakeBridge);
    }

    @Test
    public void r51_givenStaleCurrentBridgeAndNoAvailableBridges_whenAnyOptionWasChosen_sorry() throws Exception {
        doThrow(HueBridge.BridgeUnreachableEx.class).when(fakeBridge).turnOnAll();
        sut.withBridge(fakeBridge);

        givenGotCallbackFor(sut, CALLBACK_ON).then().shouldAnswer(sorryNoBridgesMessage);

        verify(fakeBridge).turnOnAll();
        verify(bridgeAdapter).search();
        verifyNoMoreInteractions(bridgeAdapter);
        verifyNoMoreInteractions(fakeBridge);
    }

    @Test
    public void r52_givenStaleCurrentBridgeAndThereIsOneAvailableBridge_whenAnyOptionWasChosen_setAvailableAsCurrentAndTryToDoOption() throws Exception {
        doThrow(HueBridge.BridgeUnreachableEx.class).when(fakeBridge).turnOnAll();
        sut.withBridge(fakeBridge);
        when(bridgeAdapter.search()).thenReturn(singletonList(anotherFakeBridge));

        givenGotCallbackFor(sut, CALLBACK_ON).then().shouldAnswer(doneCallbackAnswer);

        verify(fakeBridge).turnOnAll();
        verify(anotherFakeBridge).turnOnAll();
        verify(bridgeAdapter).search();
        verifyNoMoreInteractions(bridgeAdapter);
        verifyNoMoreInteractions(fakeBridge);
        verifyNoMoreInteractions(anotherFakeBridge);
    }

    @Test
    public void r53_givenStaleCurrentBridgeAndThereIsOneAvailableBridgeThatGotStaleAfterSetAsCurrent_whenAnyOptionWasChosen_sorry() throws Exception {
        doThrow(HueBridge.BridgeUnreachableEx.class).when(fakeBridge).turnOnAll();
        doThrow(HueBridge.BridgeUnreachableEx.class).when(anotherFakeBridge).turnOnAll();
        sut.withBridge(fakeBridge);
        when(bridgeAdapter.search()).thenReturn(singletonList(anotherFakeBridge));

        givenGotCallbackFor(sut, CALLBACK_ON).then().shouldAnswer(sorryNoBridgesMessage);

        verify(fakeBridge).turnOnAll();
        verify(anotherFakeBridge).turnOnAll();
        verify(bridgeAdapter).search();
        verifyNoMoreInteractions(bridgeAdapter);
        verifyNoMoreInteractions(fakeBridge);
        verifyNoMoreInteractions(anotherFakeBridge);
    }

    @Test
    public void r54_givenStaleCurrentBridgeAndThereAreManyAvailableBridges_whenAnyOptionWasChosen_askToChooseBridge() throws Exception {
        doThrow(HueBridge.BridgeUnreachableEx.class).when(fakeBridge).turnOnAll();
        sut.withBridge(fakeBridge);
        when(bridgeAdapter.search()).thenReturn(asList(anotherFakeBridge, yetAnotherFakeBridge));

        givenGotCallbackFor(sut, CALLBACK_ON).then(sut).shouldAnswer(message("Choose bridge:").replyMarkup(
            keyboard().row(anotherFakeBridge.desc(), anotherFakeBridge.id(), yetAnotherFakeBridge.desc(), yetAnotherFakeBridge.id()).build()
        ));

        verify(fakeBridge).turnOnAll();
        verify(bridgeAdapter).search();
        verifyNoMoreInteractions(bridgeAdapter);
        verifyNoMoreInteractions(fakeBridge);
    }

    private class TestDoubleCommand extends LightsCommand {
        TestDoubleCommand(BridgeAdapter adapter) {
            super(adapter);
        }

        private void withBridge(HueBridge bridge) {
            this.bridge = bridge;
        }
    }
}