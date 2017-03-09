package net.mamot.bot.services.debts;

import java.util.HashMap;
import java.util.Map;

public class InMemWizardSession implements WizardSession {
    private WizardStep initStep;
    private final Map<Integer, WizardStep> stepMap = new HashMap<>();

    public InMemWizardSession(WizardStep initStep) {
        this.initStep = initStep;
    }

    @Override
    public WizardStep get(int id) {
        return stepMap.get(id) == null ? initStep : stepMap.get(id);
    }

    @Override
    public void set(int id, WizardStep step) {
        stepMap.put(id, step);
    }
}
