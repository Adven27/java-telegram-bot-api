package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;

import java.math.BigDecimal;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.Type.TEXT_EQUALS_DATA_LIST;
import static java.lang.Integer.parseInt;

public class AmountStep extends DebtsWizardStep {

    public AmountStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "Сколько?\n" + transaction.toString();
    }

    protected KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row(TEXT_EQUALS_DATA_LIST, "1", "5", "10").
                row(TEXT_EQUALS_DATA_LIST, "50", "100", "200").
                row(TEXT_EQUALS_DATA_LIST, "500", "1000", "2000").
                row("\uD83D\uDCC6 на срок", "due").
                row("\uD83D\uDD19", "back", "clear", "clear", "\uD83D\uDCB0 OK", "OK");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "back": return new WhatStep(transaction, bot, originalMessage);
            case "OK": return new ConfirmationStep(transaction, bot, originalMessage);
            case "due": return new DueStep(transaction, bot, originalMessage);
            case "clear": transaction.sum(BigDecimal.ZERO); return this;
            default:
                transaction.sum(transaction.sum().add(BigDecimal.valueOf(parseInt(data))));
                return this;
        }
    }

    @Override
    public void enter(String data) {
        //todo пользовательская сумма
    }
}