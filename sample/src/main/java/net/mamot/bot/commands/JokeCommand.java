package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.services.impl.MessageFromURL;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static com.pengrad.telegrambot.request.SendSticker.sticker;
import static net.mamot.bot.services.Stickers.LOL;

public class JokeCommand extends MessageCommand {

    private final MessageFromURL messageFromURL;

    public JokeCommand(MessageFromURL messageFromURL) {
        super("/ha","Print cool joke");
        this.messageFromURL = messageFromURL;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(sticker(chat, LOL.id()));
        bot.execute(message(chat, messageFromURL.print()).parseMode(HTML).disableWebPagePreview(true));
    }
}