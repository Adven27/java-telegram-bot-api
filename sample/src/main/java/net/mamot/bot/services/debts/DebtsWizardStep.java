package net.mamot.bot.services.debts;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.fluent.KeyboardBuilder;
import com.pengrad.telegrambot.request.EditMessageText;
import net.mamot.bot.commands.DebtsCommand;

public abstract class DebtsWizardStep implements WizardStep {
    public final static String COMMAND = DebtsCommand.COMMAND;
    protected final Transaction transaction;
    protected final TelegramBot bot;
    protected final Integer originalMessage;

    public DebtsWizardStep(Transaction transaction, TelegramBot bot, Integer originalMessage) {
        this.transaction = transaction;
        this.bot = bot;
        this.originalMessage = originalMessage;
    }

    @Override
    public void show() {
        bot.execute(new EditMessageText(transaction.me(), originalMessage, screen()).replyMarkup(keyboard().build()));
    }

    @Override
    public void enter(String data) { }


    protected abstract String screen();
    protected abstract KeyboardBuilder keyboard();
}