package net.mamot.bot.services.debts;

import java.util.HashMap;
import java.util.Map;

public class InMemWizardSession implements WizardSession {
    private final Map<Integer, WizardStep> stepMap = new HashMap<>();

    @Override
    public WizardStep get(int id) {
        return stepMap.get(id);
    }

    @Override
    public WizardStep getOrStartFrom(int id, WizardStep step) {
        if (stepMap.get(id) == null) {
            stepMap.put(id, step);
            return step;
        }
        return stepMap.get(id);
    }

    @Override
    public void set(int id, WizardStep step) {
        stepMap.put(id, step);
    }
}