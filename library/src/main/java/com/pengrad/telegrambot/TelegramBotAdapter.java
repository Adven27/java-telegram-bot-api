package com.pengrad.telegrambot;

import com.google.gson.Gson;
import com.pengrad.telegrambot.impl.FileApi;
import com.pengrad.telegrambot.impl.TelegramBotClient;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * stas
 * 8/4/15.
 */
public class TelegramBotAdapter {

    public static final String API_URL = "https://api.telegram.org/bot";

    public static TelegramBot build(String botToken) {
        return buildCustom(botToken, client(null));
    }

    public static TelegramBot buildDebug(String botToken) {
        return buildCustom(botToken, client(httpLoggingInterceptor()));
    }

    public static TelegramBot buildCustom(String botToken, OkHttpClient okHttpClient) {
        return buildCustom(botToken, okHttpClient, API_URL);
    }

    public static TelegramBot buildCustom(String botToken, OkHttpClient okHttpClient, String apiUrl) {
        TelegramBotClient client = new TelegramBotClient(okHttpClient, gson(), apiUrl(apiUrl, botToken));
        FileApi fileApi = new FileApi(botToken);
        return new TelegramBot.TB(client, fileApi);
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
