package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;
import net.mamot.bot.services.impl.Injector;

public class DoneStep extends DebtsWizardStep {
    private Favorites favorites = (Favorites) Injector.provide(Favorites.class);

    public DoneStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    public String screen() {
        return "Запомнил:\n" + transaction.toString();
    }

    public KeyboardBuilder keyboard() {
        KeyboardBuilder kb = KeyboardBuilder.keyboard().row("OK","OK");
        if (!favorites.getAllTransactions(transaction.me()).contains(transaction)) {
            kb.row("\uD83C\uDF1F Добавить в избранное", "fav");
        }
        return kb.row("\uD83D\uDDC2 Показать список долгов", "debts");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "fav": favorites.addTransaction(transaction.me(), transaction);
            //FALL THROUGH
            case "OK" : return new WhoStep(new Transaction(transaction.me()), bot, originalMessage);
            case "debts": return new DebtsStep(transaction, bot, originalMessage);
            default: return this;
        }
    }
}
