package com.pengrad.telegrambot.tester;

import com.google.gson.Gson;
import com.pengrad.telegrambot.BotAPI;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.logging.BotLogger;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

class FakeBotApi implements BotAPI {
    private static final String TAG = FakeBotApi.class.getSimpleName();

    private final String updateReq;
    List<BaseRequest> requests = new ArrayList<>();

    public FakeBotApi(String updateReq) {
        this.updateReq = updateReq;
    }

    public List<BaseRequest> requests() {
        return requests;
    }

    @Override
    public <T extends BaseRequest, R extends BaseResponse> void send(T request, Callback<T, R> callback) {
        callback.onResponse(request, (R) getR(request));
    }

    @Override
    public <T extends BaseRequest, R extends BaseResponse> R send(BaseRequest<T, R> request) {
        return getR(request);
    }

    private <T extends BaseRequest, R extends BaseResponse> R getR(BaseRequest<T, R> request) {
        BotLogger.info(TAG, ">> [" + request + "]");
        R response = null;

        //TODO make more adequate request-response mapping
        if (request instanceof GetUpdates) {
            response = getUpdate(GetUpdatesResponse.class);
        } else if (request instanceof SendMessage){
            requests.add(request);
            Map<String, Object> params = request.getParameters();
            response = new Gson().fromJson("{\"ok\":true,\"result\":{\"message_id\": " + requests.size() + "," +
                    "\"from\":{\"id\":" + params.get("chat_id") + ",\"first_name\":\"fake\",\"username\":\"fake\"}," +
                    "\"chat\":{\"id\":" + params.get("chat_id") + ",\"first_name\":\"fake\",\"last_name\":\"fake\",\"username\":\"fake\",\"type\":\"private\"}," +
                    "\"date\":" + new Date().getTime()/1000 + ",\"text\":\"" + params.get("text") + "\"}}", request.getResponseType());
        }
        BotLogger.info(TAG, "<< " + response);
        return response;
    }

    private <R extends BaseResponse> R getUpdate(Type type) {
        return new Gson().fromJson(updateReq, type);
    }
}