package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.services.Stickers;
import net.mamot.bot.services.impl.DAO;

import static com.pengrad.telegrambot.request.SendMessage.message;
import static com.pengrad.telegrambot.request.SendSticker.sticker;

public class SupCommand extends MessageCommand {

    private final DAO dao;

    public SupCommand(DAO dao) {
        super("/sup","mamot loves you");
        this.dao = dao;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(sticker(chat, Stickers.random().id()));
        bot.execute(message(chat, dao.getComplement()).disableWebPagePreview(true));
    }
}