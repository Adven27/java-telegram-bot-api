package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class DueStep extends DebtsWizardStep {

    public DueStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "На сколько?\n" + transaction.toString();
    }

    protected KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("До завтра", "t").
                row("До конца недели", "w").
                row("До конца месяца", "m").
                row("\uD83D\uDD19", "back");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "back": return new AmountStep(transaction, bot, originalMessage);
            default: return new AmountStep(transaction, bot, originalMessage);
        }
    }

    @Override
    public void enter(String data) {
        //todo пользовательская дата
    }
}
