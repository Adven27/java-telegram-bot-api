package net.mamot.bot.services.impl;

import com.pengrad.telegrambot.logging.BotLogger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class HttpResource implements Resource {
    public String from(String url) throws IOException {
        BotLogger.info("HttpResource", "fetching from " + url);
        OkHttpClient client =  new OkHttpClient.Builder().build();
        Response response = client.newCall(new Request.Builder().url(url).build()).execute();
        return response.body().string();
    }
}
