package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.services.impl.MessageFromURL;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static com.pengrad.telegrambot.request.SendSticker.sticker;
import static net.mamot.bot.services.Stickers.ALONE;
import static net.mamot.bot.services.Stickers.THINK;

public class QuoteCommand extends MessageCommand {

    public static final String COMMAND = "/quote";
    private final MessageFromURL messageFromURL;

    public QuoteCommand(MessageFromURL messageFromURL) {
        super(COMMAND, "Print cool quote");
        this.messageFromURL = messageFromURL;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        String text = "";
        String sticker = THINK.id();
        try {
            text = messageFromURL.print();
        } catch (Exception e) {
            sticker = ALONE.id();
            text = "Связь с ноосферой потеряна...";
        }
        bot.execute(sticker(chat, sticker));
        bot.execute(message(chat, text).parseMode(HTML).disableWebPagePreview(true));
    }
}