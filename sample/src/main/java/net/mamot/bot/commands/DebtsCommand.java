package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import net.mamot.bot.services.debts.DebtsManager;
import net.mamot.bot.services.debts.WizardStep;

import static com.pengrad.telegrambot.request.EditMessageText.editMessage;
import static com.pengrad.telegrambot.request.SendMessage.message;

public class DebtsCommand extends CallbackCommand {
    public static final String COMMAND = "/debts";
    private final DebtsManager debtsManager;

    public DebtsCommand(DebtsManager debtsManager) {
        super(COMMAND, "Try to beat me, skin bastard...");
        this.debtsManager = debtsManager;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        WizardStep state = debtsManager.stateFor(user);
        if (state == null) {
            bot.execute(message(chat, "Wizard is broken"));
        } else  {
            bot.execute(message(chat, state.screen()).replyMarkup(getKeyboard(state)));
        }
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        WizardStep state = debtsManager.updateFor(cb.from(), cb.data());
        if (state == null) {
            bot.execute(editMessage(cb.message(), "Wizard is broken"));
        } else {
            bot.execute(editMessage(cb.message(), state.screen()).replyMarkup(getKeyboard(state)));
        }
        return true;
    }

    private InlineKeyboardMarkup getKeyboard(WizardStep state) {
        return state.keyboard().build();
    }
}