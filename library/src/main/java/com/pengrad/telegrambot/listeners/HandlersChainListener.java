package com.pengrad.telegrambot.listeners;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public class HandlersChainListener implements UpdatesListener {
    private final TelegramBot bot;
    private final List<UpdateHandler> handlers;

    public HandlersChainListener(TelegramBot bot, List<UpdateHandler> handlers) {
        this.bot = bot;
        this.handlers = handlers;
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(u -> handlers.stream().filter(h -> h.handle(bot, u)).findFirst());
        return CONFIRMED_UPDATES_ALL;
    }
}