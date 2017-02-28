package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.listeners.handlers.CallbackHandler;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

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
        String data = cb.data();
        String prefix = prefix();
        if(data.startsWith(prefix)) {
            cb.setData(data.substring(prefix.length()));
            return true;
        }
        return false;
    }

    public String signCallback(String data) {
        return prefix() + data;
    }

    private final String prefix() {
        return identifier() + ":";
    }

    public InlineKeyboardMarkup signCallbackKeyboard(InlineKeyboardMarkup markup) {
        for (InlineKeyboardButton[] row : markup.keyboard()) {
            for (InlineKeyboardButton button : row) {
                button.callbackData(signCallback(button.callbackData()));
            }
        }
        return markup;
    }
}