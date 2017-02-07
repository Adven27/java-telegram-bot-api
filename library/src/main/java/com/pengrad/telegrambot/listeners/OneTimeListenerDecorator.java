package com.pengrad.telegrambot.listeners;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public class OneTimeListenerDecorator implements UpdatesListener {

    private final TelegramBot bot;
    private final UpdatesListener original;

    public OneTimeListenerDecorator(TelegramBot bot, UpdatesListener original) {
        this.bot = bot;
        this.original = original;
    }

    @Override
    public int process(List<Update> updates) {
        int res = original.process(updates);
        bot.removeGetUpdatesListener();
        return res;
    }
}
