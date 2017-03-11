package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class EvenStep implements WizardStep {
    private final Transaction transaction;

    public EvenStep(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String screen() {
        return "В расчете";
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("Ok", "Ok").
                row("\uD83D\uDD19", "back");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "back": return new WhatStep(transaction);
            case "OK": return new DoneStep(transaction);
            default: return new EvenStep(transaction);
        }
    }
}
