package com.pengrad.telegrambot.listeners.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public interface CallbackHandler extends UpdateHandler {
    boolean callback(TelegramBot bot, CallbackQuery callbackQuery);
    InlineKeyboardMarkup signCallbackKeyboard(InlineKeyboardMarkup keyboard);
}