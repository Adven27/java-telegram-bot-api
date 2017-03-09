package net.mamot.bot.services.debts;

public interface WizardSession {
    WizardStep get(int id);
    void set(int id, WizardStep step);
}
