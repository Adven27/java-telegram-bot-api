package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class DueStep implements WizardStep {
    private Transaction transaction;

    public DueStep(Transaction transaction) {
        this.transaction = transaction;
    }
    @Override
    public String screen() {
        return "На сколько?\n" + transaction.toString();
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("До завтра", "t").
                row("До конца недели", "w").
                row("До конца месяца", "m").
                row("\uD83D\uDD19", "back");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "back": return new AmountStep(transaction);
            default: return new AmountStep(transaction);
        }
    }
}
