package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.listeners.handlers.CallbackHandler;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;

public abstract class CallbackCommand extends MessageCommand implements CallbackHandler {

    public CallbackCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    public boolean handle(TelegramBot bot, Update update) {
        CallbackQuery cb = update.callbackQuery();
        if (cb == null) {
            return super.handle(bot, update);
        }
        return isThisCommandCallback(cb) ? callback(bot, cb) : false;
    }

    private boolean isThisCommandCallback(CallbackQuery cb) {
        Integer om = originalMessage();
        return cb != null && (cb.message().messageId() == om || cb.message().messageId().equals(om));
    }
}