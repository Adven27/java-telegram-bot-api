package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class WhoStep implements WizardStep {
    private final Transaction transaction;
    private Favorites favorites = new Favorites();

    public WhoStep(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String screen() {
        return "Кто?\n" + transaction.toString();
    }

    @Override
    public KeyboardBuilder keyboard() {
        KeyboardBuilder keyboard = KeyboardBuilder.keyboard();
        favorites.favorites().forEach((s, s2) ->
                keyboard.row(s2, s)
        );
        return keyboard;
    }

    @Override
    public WizardStep callback(String data) {
        if ("enter".equals(data)) {

        }
        transaction.to(data);
        return new WhatStep(transaction);
    }
}
