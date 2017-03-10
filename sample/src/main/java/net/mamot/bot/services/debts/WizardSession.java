package net.mamot.bot.services.debts;

public interface WizardSession {
    WizardStep get(int id);
    WizardStep getOrStartFrom(int id, WizardStep step);
    void set(int id, WizardStep step);
}