package net.mamot.bot.services.debts;

public interface WizardStep {
    WizardStep callback(String data);
    void enter(String data);
    void show();
}
