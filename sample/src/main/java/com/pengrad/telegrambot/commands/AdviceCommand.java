package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.services.impl.MessageFromURL;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static net.mamot.bot.services.Stickers.ALONE;
import static net.mamot.bot.services.Stickers.BLA;

public class AdviceCommand extends BotCommand {

    private final MessageFromURL messageFromURL;

    public AdviceCommand(MessageFromURL messageFromURL) {
        super("/advice", "Fucking great advice");
        this.messageFromURL = messageFromURL;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        String text = "";
        String sticker = BLA.id();
        try {
            text = messageFromURL.print();
        } catch (Exception e) {
            sticker = ALONE.id();
            text = "Ñגÿחü ס םממספונמי ןמעונÿםא...";
        }
        bot.execute(new SendSticker(chat.id(), sticker));
        bot.execute(new SendMessage(chat.id(), text).
                disableWebPagePreview(true).parseMode(HTML));
    }
}