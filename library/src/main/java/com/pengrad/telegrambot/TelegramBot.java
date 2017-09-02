package com.pengrad.telegrambot;

import com.pengrad.telegrambot.impl.FileApi;
import com.pengrad.telegrambot.impl.TelegramBotClient;
import com.pengrad.telegrambot.impl.UpdatesHandler;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.BaseResponse;

public interface TelegramBot {
    <T extends BaseRequest, R extends BaseResponse> R execute(BaseRequest<T, R> request);
    <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback);
    void setUpdatesListener(UpdatesListener listener);
    void setUpdatesListener(UpdatesListener listener, GetUpdates request);
    void removeGetUpdatesListener();


    public class TB implements TelegramBot{

        private final TelegramBotClient api;
        private final FileApi fileApi;
        private final UpdatesHandler updatesHandler;

        TelegramBot(TelegramBotClient api, FileApi fileApi) {
            this.api = api;
            this.fileApi = fileApi;
            this.updatesHandler = new UpdatesHandler();
        }

        public <T extends BaseRequest, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
            return api.send(request);
        }

        public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback) {
            api.send(request, callback);
        }

        public String getFullFilePath(File file) {
            return fileApi.getFullFilePath(file.filePath());
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
}