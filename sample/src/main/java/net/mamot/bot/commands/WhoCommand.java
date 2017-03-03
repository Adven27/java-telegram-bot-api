package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import java.util.Random;

import static com.pengrad.telegrambot.request.SendMessage.message;

//TODO Rewrite + tests
public class WhoCommand extends MessageCommand {

    public WhoCommand() {
        super("/who","who buys cookies..?");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        String[] strings = params.split("");
        String m = strings.length == 0 ? "Пример: /who я ты мамай"
                                         : strings[new Random().nextInt(strings.length)];
        bot.execute(message(chat, m));
    }
}