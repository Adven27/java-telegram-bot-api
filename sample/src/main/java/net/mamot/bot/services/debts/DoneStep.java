package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class DoneStep extends DebtsWizardStep {

    public DoneStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    public String screen() {
        return "Выполнено:\n" + transaction.toString();
    }

    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("Ok", "OK").
                row("\uD83C\uDF1F Добавить в избранное", "fav");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "fav": return this;
            case "OK": return new WhoStep(new Transaction(transaction.me()), bot, originalMessage);
            default: return this;
        }
    }
}
