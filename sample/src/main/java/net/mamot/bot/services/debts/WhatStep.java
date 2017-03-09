package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class WhatStep implements WizardStep {
    @Override
    public String screen() {
        return "What?";
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("income", "in").
                row("outcome", "out").
                row("even", "even").
                row("back", "back");
    }

    @Override
    public WizardStep callback(String data) {
        return new WhoStep();
    }
}
