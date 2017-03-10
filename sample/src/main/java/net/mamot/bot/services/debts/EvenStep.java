package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

import static java.lang.Integer.parseInt;

public class EvenStep implements WizardStep {
    private final Transaction transaction;

    public EvenStep(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String screen() {
        return "Even?";
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("jessy", "1").
                row("gus", "2").
                row("saul", "3");
    }

    @Override
    public WizardStep callback(String data) {
        transaction.to(parseInt(data));
        return new WhatStep(transaction);
    }
}
