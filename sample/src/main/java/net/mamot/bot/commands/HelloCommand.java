package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;

public class HelloCommand extends MessageCommand {
    public HelloCommand() {
        super("/hello", "Hello world!!!");
    }

    @Override
    final public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(new SendMessage(chat.id(), "Hi"));
    }
}
