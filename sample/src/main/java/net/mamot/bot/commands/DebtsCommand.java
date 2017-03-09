package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
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
        WizardStep state = session.get(user.id());
        if (state == null) {
            bot.execute(message(chat, "Wizard is broken"));
        } else  {
            bot.execute(message(chat, state.screen()).replyMarkup(getKeyboard(state)));
        }
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        WizardStep state = session.get(cb.from().id());
        if (state == null) {
            bot.execute(editMessage(cb.message(), "Wizard is broken"));
        } else {
            WizardStep updatedStep = state.callback(cb.data());
            if (updatedStep == null) {
                bot.execute(editMessage(cb.message(), "Wizard is broken"));
            } else {
                session.set(cb.from().id(), updatedStep);
                bot.execute(editMessage(cb.message(), updatedStep.screen()).replyMarkup(getKeyboard(updatedStep)));
            }
        }
        return true;
    }

    private InlineKeyboardMarkup getKeyboard(WizardStep state) {
        return state.keyboard().build();
    }
}