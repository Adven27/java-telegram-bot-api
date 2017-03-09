package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.model.User;

public interface DebtsManager {
    WizardStep stateFor(User user);
    WizardStep updateFor(User user, String data);
}
