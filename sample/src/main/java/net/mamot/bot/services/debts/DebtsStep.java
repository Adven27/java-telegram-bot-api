package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;
import net.mamot.bot.services.impl.Registry;

import static java.util.stream.Collectors.joining;

public class DebtsStep extends DebtsWizardStep {
    private DebtsRepo debtsRepo = (DebtsRepo) Registry.provide(DebtsRepo.class);

    public DebtsStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        super(transaction, bot, originalMessage);
    }

    protected String screen() {
        return "Долги:\n" +
                debtsRepo.select(transaction.me()).stream().map(Debt::toString).collect(joining("\n"));
    }

    protected KeyboardBuilder keyboard() {
        return KeyboardBuilder.keyboard().row("\uD83D\uDD19", "back");
    }

    public WizardStep callback(String data) {
        switch (data) {
            case "back": return new WhoStep(transaction, bot, originalMessage);
            default: return this;
        }
    }
}