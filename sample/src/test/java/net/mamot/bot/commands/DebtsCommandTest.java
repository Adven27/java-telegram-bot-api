package net.mamot.bot.commands;

import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.tester.BotTester;
import net.mamot.bot.services.debts.WizardSession;
import net.mamot.bot.services.debts.WizardStep;
import org.junit.Test;

import static com.pengrad.telegrambot.tester.BotTester.given;
import static com.pengrad.telegrambot.tester.BotTester.givenGotCallbackFor;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class DebtsCommandTest {
    private final WizardSession wizardSession = mock(WizardSession.class);
    private final DebtsCommand sut = new DebtsCommand(wizardSession);
    private final User heisenberg = BotTester.createUser(737, "heisenberg");

    @Test
    public void shouldStartSession() throws Exception {
        given(sut).
            got("/debts").from(heisenberg).
        then(sut).
            shouldAnswer(new SendMessage(heisenberg.id(), "..."));

        verify(wizardSession).start(eq(heisenberg.id()), any(WizardStep.class));
    }

    @Test
    public void shouldRethrowCallbackToDebtsManagerAndShowUpdatedStep() throws Exception {
        givenGotCallbackFor(sut, "btn").from(heisenberg).
        then(sut).
            shouldAnswer();

        verify(wizardSession).callback(heisenberg.id(), "btn");
        verifyNoMoreInteractions(wizardSession);
    }

    //todo reply case
}