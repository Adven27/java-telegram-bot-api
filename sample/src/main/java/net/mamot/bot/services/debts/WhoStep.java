package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.SendMessage;
import net.mamot.bot.services.impl.Injector;

public class WhoStep extends DebtsWizardStep {
    private Favorites favorites = (Favorites) Injector.provide(Favorites.class);

    public WhoStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "Кто?\n" + transaction.toString();
    }

    protected KeyboardBuilder keyboard() {
        KeyboardBuilder keyboard = KeyboardBuilder.keyboard();

        favorites.getAllTransactions(transaction.me()).forEach((t) ->
                keyboard.row("\uD83C\uDF1F " + t.toString(), t.id())
        );

        favorites.getAllCounterparties(transaction.me()).forEach((s) ->
                keyboard.row(s, s)
        );
        keyboard.row("✏️ Ввести имя", "enter").row("\uD83D\uDDC2 Показать список долгов", "debts");
        return keyboard;
    }

    public WizardStep callback(String data) {
        if ("enter".equals(data)) {
            bot.execute(new SendMessage(transaction.me(), COMMAND + " enter name").replyMarkup(new ForceReply()));
            return this;
        }

        if ("debts".equals(data)) {
            return new DebtsStep(transaction, bot, originalMessage) ;
        }

        Transaction trn = Transaction.fromId(data);
        if (trn != null) {
            return new ConfirmationStep(trn, bot, originalMessage);
        }
        transaction.to(data);
        return new WhatStep(transaction, bot, originalMessage);
    }

    public void enter(String data) {
        favorites.addCounterparty(transaction.me(), data);
        show();
    }
}