package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

import static java.lang.Boolean.parseBoolean;

public class WhatStep implements WizardStep {
    private final Transaction transaction;

    public WhatStep(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String screen() {
        return "Что сделал?\n" + transaction.toString();
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("\uD83D\uDCC8 Дал мне денег", "true").
                row("\uD83D\uDCC9 Взял у меня денег", "false").
                row("⚖️ В расчете", "even").
                row("\uD83D\uDD19", "back");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "back": return new WhoStep(transaction);
            case "even": return new EvenStep(transaction);
            default:
                transaction.income(parseBoolean(data));
                return new AmountStep(transaction);
        }
    }
}
