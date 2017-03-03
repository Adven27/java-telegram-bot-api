package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.services.bardak.BardakMenu;

import static com.pengrad.telegrambot.request.SendMessage.message;

public class BardakCommand extends MessageCommand {
    public static final String COMMAND = "/bardak";
    private final BardakMenu menu;

    public BardakCommand(BardakMenu menu) {
        super(COMMAND,"Bardak menu");
        this.menu = menu;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(message(chat, menu.today()).disableWebPagePreview(true));
    }
}