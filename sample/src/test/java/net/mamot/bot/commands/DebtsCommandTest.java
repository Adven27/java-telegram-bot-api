package net.mamot.bot.commands;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.tester.BotTester;
import net.mamot.bot.services.debts.WizardSession;
import net.mamot.bot.services.debts.WizardStep;
import org.junit.Ignore;
import org.junit.Test;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.Type.TEXT_EQUALS_DATA_LIST;
import static com.pengrad.telegrambot.tester.BotTester.*;
import static org.mockito.Mockito.*;

public class DebtsCommandTest {

    private final WizardSession wizardSession = mock(WizardSession.class);
    private final WizardStep step = mock(WizardStep.class);
    private final WizardStep nextStep = mock(WizardStep.class);
    private final DebtsCommand sut = new DebtsCommand(wizardSession);
    private final User heisenberg = BotTester.createUser(737, "heisenberg");
    private final KeyboardBuilder keyboard = KeyboardBuilder.keyboard().row(TEXT_EQUALS_DATA_LIST, "btn1", "btn2");

    @Test @Ignore
    public void shouldShowInfoFromStep() throws Exception {
        when(step.screen()).thenReturn("step desc");
        when(step.keyboard()).thenReturn(keyboard);
        when(wizardSession.get(heisenberg.id())).thenReturn(step);

        given(sut).
            got("/debts").from(heisenberg).
        then(sut).
            shouldAnswer(message("step desc").replyMarkup(keyboard.build()));

        verify(step).screen();
        verify(step).keyboard();
        verifyNoMoreInteractions(step);
        verify(wizardSession).get(heisenberg.id());
        verifyNoMoreInteractions(wizardSession);
    }

    @Test  @Ignore
    public void shouldShowErrorIfNoStepFound() throws Exception {
        when(wizardSession.get(anyInt())).thenReturn(null);

        given(sut).
            got("/debts").
        then(sut).
            shouldAnswer(message("Wizard is broken"));

        verify(wizardSession).get(anyInt());
        verifyNoMoreInteractions(wizardSession);
    }

    @Test
    public void shouldRethrowCallbackToDebtsManagerAndShowUpdatedStep() throws Exception {
        when(nextStep.screen()).thenReturn("step desc");
        when(nextStep.keyboard()).thenReturn(keyboard);
        when(step.callback("btn")).thenReturn(nextStep);
        when(wizardSession.get(heisenberg.id())).thenReturn(step);

        givenGotCallbackFor(sut, "btn").from(heisenberg).
        then(sut).
            shouldAnswer(editMessage("step desc").replyMarkup(keyboard.build()));

        verify(step).callback("btn");
        verifyNoMoreInteractions(step);
        verify(nextStep).screen();
        verify(nextStep).keyboard();
        verifyNoMoreInteractions(nextStep);
        verify(wizardSession).get(heisenberg.id());
        verify(wizardSession).set(heisenberg.id(), nextStep);
        verifyNoMoreInteractions(wizardSession);
    }

    @Test
    public void shouldShowErrorIfRepoDidNotReturnCurrentStep() throws Exception {
        when(wizardSession.get(anyInt())).thenReturn(null);

        givenGotCallbackFor(sut, "some callback").
        then(sut).
            shouldAnswer(editMessage("Wizard is broken"));

        verify(wizardSession).get(anyInt());
        verifyNoMoreInteractions(wizardSession);
    }

    @Test
    public void shouldShowErrorIfCurrentStepDidNotReturnUpdatedStep() throws Exception {
        when(step.callback("btn")).thenReturn(null);
        when(wizardSession.get(heisenberg.id())).thenReturn(step);

        givenGotCallbackFor(sut, "btn").from(heisenberg).
        then(sut).
            shouldAnswer(editMessage("Wizard is broken"));

        verify(step).callback("btn");
        verifyNoMoreInteractions(step);
        verify(wizardSession).get(heisenberg.id());
        verifyNoMoreInteractions(wizardSession);
    }
}
