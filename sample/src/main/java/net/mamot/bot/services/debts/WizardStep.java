package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public interface WizardStep {
    String screen();
    KeyboardBuilder keyboard();
    WizardStep callback(String data);
}
