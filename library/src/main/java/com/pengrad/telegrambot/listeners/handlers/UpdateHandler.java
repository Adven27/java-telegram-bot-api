package com.pengrad.telegrambot.listeners.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

public interface UpdateHandler {
    boolean handle(TelegramBot bot, Update update);
}