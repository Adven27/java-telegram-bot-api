package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import net.mamot.bot.services.debts.Transaction;
import net.mamot.bot.services.debts.WhoStep;
import net.mamot.bot.services.debts.WizardSession;

public class DebtsCommand extends CallbackCommand {
    public static final String COMMAND = "/debts";
    private final WizardSession session;

    public DebtsCommand(WizardSession session) {
        super(COMMAND, "Debts tracker...");
        this.session = session;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        SendResponse response = bot.execute(new SendMessage(user.id(), "..."));
        session.start(user.id(), new WhoStep(new Transaction(user.id()), bot, response.message().messageId()));
    }

    @Override
    public void reply(TelegramBot bot, User user, Chat chat, String params, Message original) {
        session.enter(user.id(), params);
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        session.callback(cb.from().id(), cb.data());
        return true;
    }
}