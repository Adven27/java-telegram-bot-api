package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class ConfirmationStep implements WizardStep {
    private final Transaction transaction;

    public ConfirmationStep(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String screen() {
        return "Подтвердите:\n" + transaction.toString();
    }

    @Override
    public KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().
                row("✅ Ok", "OK").
                row("\uD83D\uDEAB Cancel", "cancel").
                row("\uD83D\uDE4E\u200D♂️ Изменить кто", "who").
                row("\uD83D\uDCB0 Изменить сумму", "amount");

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
