package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.SendMessage;

public class WhoStep extends DebtsWizardStep {
    private Favorites favorites = new Favorites();

    public WhoStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "Кто?\n" + transaction.toString();
    }

    protected KeyboardBuilder keyboard() {
        KeyboardBuilder keyboard = KeyboardBuilder.keyboard();
        favorites.getAll().forEach((s, s2) ->
                keyboard.row(s2, s)
        );
        keyboard.row("✏️ Ввести имя", "enter");
        return keyboard;
    }

    public WizardStep callback(String data) {
        if ("enter".equals(data)) {
            bot.execute(new SendMessage(transaction.me(), COMMAND + " enter name").replyMarkup(new ForceReply()));
            return this;
        }
        transaction.to(data);
        return new WhatStep(transaction, bot, originalMessage);
    }

    public void enter(String data) {
        favorites.add(data);
        show();
    }
}