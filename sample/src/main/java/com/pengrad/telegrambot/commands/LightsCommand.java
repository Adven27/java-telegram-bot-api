package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.services.LightsService;

import static net.mamot.bot.services.Stickers.BLA;

public class LightsCommand extends BotCommand {

    private final LightsService lightsService;

    public LightsCommand(LightsService lightsService) {
        super("/lights", "Lights");
        this.lightsService = lightsService;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        lightsService.turnOffAll();
        bot.execute(new SendSticker(chat.id(), BLA.id()));
        bot.execute(new SendMessage(chat.id(), "Done"));
    }
}