package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class DoneStep implements WizardStep {
    private final Transaction transaction;

    public DoneStep(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String screen() {
        return "Done\n" + transaction.toString();
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("To favorites", "fav").
                row("Ok", "OK");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "fav": return new DoneStep(transaction);
            case "OK": return new WhoStep(new Transaction(transaction.me()));
            default: return new DoneStep(transaction);
        }
    }
}
