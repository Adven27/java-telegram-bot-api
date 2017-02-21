package com.pengrad.telegrambot.listeners;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

import static java.util.Arrays.asList;

public class HandlersChainListener implements UpdatesListener {
    private final TelegramBot bot;
    private final UpdateHandler defaultConsumer;
    private final List<UpdateHandler> handlers;

    public HandlersChainListener(TelegramBot bot,UpdateHandler defaultHandler, UpdateHandler... handlers) {
        this.bot = bot;
        this.defaultConsumer = defaultHandler;
        this.handlers = asList(handlers);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::process);
        return CONFIRMED_UPDATES_ALL;
    }

    void process(Update u) {
        if (!handlers.stream().filter(h -> h.handle(bot, u)).findFirst().isPresent() && defaultConsumer != null) {
            defaultConsumer.handle(bot, u);
        }
    }
}