package com.pengrad.telegrambot;

import com.google.gson.Gson;
import com.pengrad.telegrambot.impl.FileApi;
import com.pengrad.telegrambot.impl.TelegramBotClient;
import com.pengrad.telegrambot.impl.UpdatesHandler;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.response.BaseResponse;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public interface TelegramBot {
    <T extends BaseRequest, R extends BaseResponse> R execute(BaseRequest<T, R> request);

    <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback);

    void setUpdatesListener(UpdatesListener listener);

    void setUpdatesListener(UpdatesListener listener, GetUpdates request);

    void removeGetUpdatesListener();


    public class TB implements TelegramBot {

        private final TelegramBotClient api;
        private final FileApi fileApi;
        private final UpdatesHandler updatesHandler;

        public TB(String botToken) {
            this(new Builder(botToken));
        }

        TB(Builder builder) {
            this.api = builder.api;
            this.fileApi = builder.fileApi;
            this.updatesHandler = builder.updatesHandler;
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

        public static final class Builder {

            static final String API_URL = "https://api.telegram.org/bot";

            private final String botToken;
            private final FileApi fileApi;
            private TelegramBotClient api;
            private UpdatesHandler updatesHandler;

            private OkHttpClient okHttpClient;
            private String apiUrl;

            public Builder(String botToken) {
                this.botToken = botToken;
                api = new TelegramBotClient(client(null), gson(), apiUrl(API_URL, botToken));
                fileApi = new FileApi(botToken);
                updatesHandler = new UpdatesHandler(100);
            }

            public Builder debug() {
                okHttpClient = client(httpLoggingInterceptor());
                return this;
            }

            public Builder okHttpClient(OkHttpClient client) {
                okHttpClient = client;
                return this;
            }

            public Builder apiUrl(String apiUrl) {
                this.apiUrl = apiUrl;
                return this;
            }

            public Builder updateListenerSleep(long millis) {
                updatesHandler = new UpdatesHandler(millis);
                return this;
            }

            public TelegramBot build() {
                if (okHttpClient != null || apiUrl != null) {
                    OkHttpClient client = okHttpClient != null ? okHttpClient : client(null);
                    String baseUrl = apiUrl(apiUrl != null ? apiUrl : API_URL, botToken);
                    api = new TelegramBotClient(client, gson(), baseUrl);
                }
                return new TelegramBot.TB(this);
            }

            private static OkHttpClient client(Interceptor interceptor) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                if (interceptor != null) builder.addInterceptor(interceptor);
                return builder.build();
            }

            private static Interceptor httpLoggingInterceptor() {
                return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
            }

            private static Gson gson() {
                return new Gson();
            }

            private static String apiUrl(String apiUrl, String botToken) {
                return apiUrl + botToken + "/";
            }
        }
    }
}