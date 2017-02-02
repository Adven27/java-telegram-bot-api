package com.pengrad.telegrambot;

import com.pengrad.telegrambot.impl.UpdatesHandler;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.BaseResponse;

/**
 * Stas Parshin
 * 16 October 2015
 */
public class TelegramBot {

    private final BotAPI api;
    private final UpdatesHandler updatesHandler;

    public TelegramBot(BotAPI api) {
        this.api = api;
        this.updatesHandler = new UpdatesHandler();
    }

    public <T extends BaseRequest, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
        return api.send(request);
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback) {
        api.send(request, callback);
    }

    public void setUpdatesListener(UpdatesListener listener) {
        setUpdatesListener(listener, new GetUpdates());
    }

    public void setUpdatesListener(UpdatesListener listener, GetUpdates request) {
        updatesHandler.start(this, listener, request);
    }

    public void removeGetUpdatesListener() {
        updatesHandler.stop();
    }
}