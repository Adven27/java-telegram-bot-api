package com.pengrad.telegrambot;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

public interface BotAPI {
    <T extends BaseRequest, R extends BaseResponse> void send(T request, Callback<T, R> callback);

    <T extends BaseRequest, R extends BaseResponse> R send(BaseRequest<T, R> request);
}
