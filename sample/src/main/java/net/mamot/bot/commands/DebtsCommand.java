package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import net.mamot.bot.services.debts.Transaction;
import net.mamot.bot.services.debts.WhoStep;
import net.mamot.bot.services.debts.WizardSession;
import net.mamot.bot.services.debts.WizardStep;

import static com.pengrad.telegrambot.request.EditMessageText.editMessage;
import static com.pengrad.telegrambot.request.SendMessage.message;

public class DebtsCommand extends CallbackCommand {
    public static final String COMMAND = "/debts";
    private final WizardSession session;

    public DebtsCommand(WizardSession session) {
        super(COMMAND, "Try to beat me, skin bastard...");
        this.session = session;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        WizardStep step = session.getOrStartFrom(user.id(), new WhoStep(new Transaction(user.id ())));
        bot.execute(message(chat, step.screen()).replyMarkup(getKeyboard(step)));
    }

    @Override
    public void reply(TelegramBot bot, User user, Chat chat, String params) {
        WizardStep step = session.get(user.id());
        bot.execute(message(chat, step.screen()).replyMarkup(getKeyboard(step)));
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        WizardStep state = session.get(cb.from().id());
        if (state == null) {
            bot.execute(editMessage(cb.message(), "Wizard is broken"));
        } else {
            if ("enter".equals(cb.data())) {
                bot.execute(new SendMessage(cb.message().chat().id(), COMMAND + " enter name").replyMarkup(new ForceReply()));
            } else {
                WizardStep updatedStep = state.callback(cb.data());
                if (updatedStep == null) {
                    bot.execute(editMessage(cb.message(), "Wizard is broken"));
                } else {
                    session.set(cb.from().id(), updatedStep);
                    bot.execute(editMessage(cb.message(), updatedStep.screen()).replyMarkup(getKeyboard(updatedStep)));
                }
            }
        }
        return true;
    }

    private InlineKeyboardMarkup getKeyboard(WizardStep state) {
        return state.keyboard().build();
    }
}