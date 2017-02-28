package com.pengrad.telegrambot;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.BaseResponse;

public interface TelegramBot {
    <T extends BaseRequest, R extends BaseResponse> R execute(BaseRequest<T, R> request);
    <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback);
    void setUpdatesListener(UpdatesListener listener);
    void setUpdatesListener(UpdatesListener listener, GetUpdates request);
    void removeGetUpdatesListener();
}