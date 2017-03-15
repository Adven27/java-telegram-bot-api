package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;

import static java.lang.Boolean.parseBoolean;

public class WhatStep extends DebtsWizardStep {

    public WhatStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "Что?\n" + transaction.toString();
    }

    protected KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("➕ Дал мне", "true").
                row("➖ Взял у меня", "false").
                row("⚖️ В расчете", "even").
                row("\uD83D\uDD19", "back");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "back": return new WhoStep(transaction, bot, originalMessage);
            case "even": return new EvenStep(transaction, bot, originalMessage);
            default:
                transaction.income(parseBoolean(data));
                return new AmountStep(transaction, bot, originalMessage);
        }
    }
}
