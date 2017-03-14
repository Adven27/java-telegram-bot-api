package net.mamot.bot.services.debts;

public interface WizardSession {
    void start(int id, WizardStep step);
    void show(int userId);
    void callback(int userId, String data);
    void enter(int userId, String data);
}