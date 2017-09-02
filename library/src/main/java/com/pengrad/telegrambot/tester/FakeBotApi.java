package com.pengrad.telegrambot.tester;

import com.pengrad.telegrambot.BotAPI;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.logging.BotLogger;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

class FakeBotApi implements BotAPI {
    private static final String TAG = FakeBotApi.class.getSimpleName();

    private final GetUpdatesResponse updatesResponse;
    private List<BaseRequest> requests = new ArrayList<>();

    public FakeBotApi(GetUpdatesResponse updatesResponse) {
        this.updatesResponse = updatesResponse;
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
        BotLogger.info(TAG, ">> " + request);
        R response = null;

        //TODO make more adequate request-response mapping
        if (request instanceof GetUpdates) {
            response = (R) updatesResponse;
        } else if (request instanceof SendMessage) {
            requests.add(request);
            Map<String, Object> params = request.getParameters();
            SendResponse sendResponse = new SendResponse();
            Message message = new Message();
            message.setMessage_id(requests.size());

            User from = new User();
            from.setId(Integer.parseInt(params.get("chat_id").toString()));
            from.setFirst_name("fake");
            message.setFrom(from);

            Chat chat = new Chat();
            chat.id(Long.valueOf(params.get("chat_id").toString()));
            chat.firstName("fake");
            chat.type(Chat.Type.Private);
            message.setChat(chat);

            message.setDate((int) (new Date().getTime() / 1000));
            message.setText(params.get("text").toString());

            sendResponse.message(message);
            response = (R) sendResponse;
        } else {
            requests.add(request);
        }
        BotLogger.info(TAG, "<< " + response);
        return response;
    }

} 