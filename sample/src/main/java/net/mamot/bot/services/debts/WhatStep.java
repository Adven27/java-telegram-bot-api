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
        return "What?\n" + transaction.toString();
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("income", "true").
                row("outcome", "false").
                row("even", "even").
                row("back", "back");
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
