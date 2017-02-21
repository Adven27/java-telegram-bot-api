package com.pengrad.telegrambot.listeners.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;

public interface CallbackHandler extends UpdateHandler {
    boolean callback(TelegramBot bot, CallbackQuery callbackQuery);
    Integer originalMessage();
}