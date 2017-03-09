package net.mamot.bot.commands;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.tester.BotTester;
import net.mamot.bot.services.debts.DebtsManager;
import net.mamot.bot.services.debts.WizardStep;
import org.junit.Test;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.Type.TEXT_EQUALS_DATA_LIST;
import static com.pengrad.telegrambot.tester.BotTester.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DebtsCommandTest {

    private final DebtsManager debtsManager = mock(DebtsManager.class);
    private DebtsCommand sut = new DebtsCommand(debtsManager);
    private final User heisenberg = BotTester.createUser(737, "heisenberg");
    private final WizardStep someStep = new WizardStep() {
        @Override
        public String screen() { return "step desc"; }

        @Override
        public KeyboardBuilder keyboard() { return KeyboardBuilder.keyboard().row(TEXT_EQUALS_DATA_LIST, "btn1", "btn2"); }

        @Override
        public WizardStep callback(String data) {
            return null;
        }
    };

    @Test
    public void shouldShowInfoFromStep() throws Exception {
        when(debtsManager.stateFor(heisenberg)).thenReturn(someStep);

        given(sut).
            got("/debts").from(heisenberg).
        then(sut).
            shouldAnswer(message(someStep.screen()).replyMarkup(someStep.keyboard().build()));

        verify(debtsManager).stateFor(heisenberg);
        verifyNoMoreInteractions(debtsManager);
    }

    @Test
    public void shouldShowErrorIfNoStepFound() throws Exception {
        when(debtsManager.stateFor(any(User.class))).thenReturn(null);

        given(sut).
            got("/debts").
        then(sut).
            shouldAnswer(message("Wizard is broken"));

        verify(debtsManager).stateFor(any(User.class));
        verifyNoMoreInteractions(debtsManager);
    }

    @Test
    public void shouldRethrowCallbackToDebtsManagerAndShowUpdatedStep() throws Exception {
        when(debtsManager.updateFor(heisenberg, "btn1")).thenReturn(someStep);

        givenGotCallbackFor(sut, "btn1").from(heisenberg).
        then(sut).
            shouldAnswer(editMessage(someStep.screen()).replyMarkup(someStep.keyboard().build()));

        verify(debtsManager).updateFor(heisenberg, "btn1");
        verifyNoMoreInteractions(debtsManager);
    }

    @Test
    public void shouldShowErrorIfManagerDidNotReturnUpdatedStep() throws Exception {
        when(debtsManager.updateFor(any(User.class), eq("some callback"))).thenReturn(null);

        givenGotCallbackFor(sut, "some callback").
        then(sut).
            shouldAnswer(editMessage("Wizard is broken"));

        verify(debtsManager).updateFor(any(User.class), eq("some callback"));
        verifyNoMoreInteractions(debtsManager);
    }
}
