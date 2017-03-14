package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class EvenStep extends DebtsWizardStep {

    public EvenStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "В расчете";
    }

    protected KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("Ok", "Ok").
                row("\uD83D\uDD19", "back");
    }

    public WizardStep callback(String data) {
        switch (data) {
            case "back": return new WhatStep(transaction, bot, originalMessage);
            case "OK": return new DoneStep(transaction, bot, originalMessage);
            default: return this;
        }
    }
}
