package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;
import net.mamot.bot.services.impl.Injector;

public class ConfirmationStep extends DebtsWizardStep {
    private Favorites favorites = (Favorites) Injector.provide(Favorites.class);

    public ConfirmationStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "Все верно?\n" + transaction.toString();
    }

    protected KeyboardBuilder keyboard() {
        KeyboardBuilder kb = KeyboardBuilder.keyboard().
                row("✅ OK", "OK").
                row("\uD83D\uDEAB Cancel", "cancel").
                row("\uD83D\uDC64 Изменить кто", "who").
                row("\uD83D\uDCB0 Изменить сумму", "amount");
        if (favorites.getAllTransactions(transaction.me()).contains(transaction)) {
            kb.row("\uD83D\uDCA5 Удалить из избранного", "unfav");
        }
        return kb;
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "cancel":
            case "who": return new WhoStep(transaction, bot, originalMessage);
            case "amount": return new AmountStep(transaction, bot, originalMessage);
            case "unfav": favorites.removeTransactions(transaction.me(), transaction); return this;
            case "OK": transaction.commit(); return new DoneStep(transaction, bot, originalMessage);
            default: return this;
        }
    }
}
