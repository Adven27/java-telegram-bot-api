package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;

public class ConfirmationStep extends DebtsWizardStep {

    public ConfirmationStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "Подтвердите:\n" + transaction.toString();
    }

    protected KeyboardBuilder keyboard() {
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
            case "who": return new WhoStep(transaction, bot, originalMessage);
            case "amount": return new AmountStep(transaction, bot, originalMessage);
            case "OK": transaction.commit(); return new DoneStep(transaction, bot, originalMessage);
            default: return this;
        }
    }
}
