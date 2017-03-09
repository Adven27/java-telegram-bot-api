package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.model.User;

import java.util.HashMap;
import java.util.Map;

public class FakeDebtsManager implements DebtsManager {
    private static Map<Integer, WizardStep> userStates = new HashMap<>();

    @Override
    public WizardStep stateFor(User user) {
        WizardStep step = userStates.get(user.id());
        if (step == null) {
            WizardStep newStep = new WhoStep();
            userStates.put(user.id(), newStep);
            return newStep;
        }
        return step;
    }

    @Override
    public WizardStep updateFor(User user, String data) {
        WizardStep step = userStates.get(user.id());
        return step.callback(data);
    }
}
