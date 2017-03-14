package com.pengrad.telegrambot.listeners.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

public interface MessageHandler extends UpdateHandler {
    void execute(TelegramBot bot, User user, Chat chat, String params);
    void reply(TelegramBot bot, User user, Chat chat, String params, Message original);
}