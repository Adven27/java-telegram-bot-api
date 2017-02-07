package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

public class HelloCommand extends BotCommand {
    public HelloCommand() {
        super("/hello", "Hello world!!!");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(new SendMessage(chat.id(), "Hi"));
    }
}
