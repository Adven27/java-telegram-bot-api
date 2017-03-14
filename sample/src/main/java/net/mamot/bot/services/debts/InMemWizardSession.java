package net.mamot.bot.services.debts;

import java.util.HashMap;
import java.util.Map;

public class InMemWizardSession implements WizardSession {
    private final Map<Integer, WizardStep> stepMap = new HashMap<>();

    private WizardStep get(int id) {
        return stepMap.get(id);
    }

    @Override
    public void start(int id, WizardStep step) {
        stepMap.put(id, step);
        step.show();
    }

    @Override
    public void show(int userId) {
        get(userId).show();
    }

    @Override
    public void callback(int userId, String data) {
        WizardStep nextStep = get(userId).callback(data);
        if (nextStep != null) {
            stepMap.put(userId, nextStep);
            nextStep.show();
        }
    }

    @Override
    public void enter(int userId, String data) {
        get(userId).enter(data);
    }
}