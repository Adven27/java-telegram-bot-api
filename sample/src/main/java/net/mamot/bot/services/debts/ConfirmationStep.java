package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class ConfirmationStep implements WizardStep {
    private final Transaction transaction;

    public ConfirmationStep(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String screen() {
        return "Confirm?\n" + transaction.toString();
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("Change 'who'", "who").
                row("Change amount", "amount").
                row("Cancel", "cancel").
                row("Ok", "OK");
    }

    @Override
    public WizardStep callback(String data) {
        switch (data) {
            case "cancel":
            case "who": return new WhoStep(transaction);
            case "amount": return new AmountStep(transaction);
            case "OK": transaction.commit(); return new DoneStep(transaction);
            default: return new ConfirmationStep(transaction);
        }
    }
}
