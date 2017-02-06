package tester;

import com.google.gson.Gson;
import com.pengrad.telegrambot.BotAPI;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class FakeBotApi implements BotAPI {
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
        //TODO normal logging
        System.out.println(">> [" + request + "]");
        R response = null;
        if (request instanceof GetUpdates) {
            response = getUpdate(GetUpdatesResponse.class);
        } else {
            requests.add(request);
        }
        System.out.println("<< " + response);
        return response;
    }

    private <R extends BaseResponse> R getUpdate(Type type) {
        return new Gson().fromJson(updateReq, type);
    }
}