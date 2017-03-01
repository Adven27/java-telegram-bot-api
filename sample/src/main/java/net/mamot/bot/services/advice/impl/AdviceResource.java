package net.mamot.bot.services.advice.impl;

import com.google.gson.Gson;
import com.pengrad.telegrambot.logging.BotLogger;
import net.mamot.bot.services.URLResource;
import net.mamot.bot.services.impl.JsonHttpResource;

import java.io.IOException;
import java.util.Map;

public class AdviceResource extends JsonHttpResource implements URLResource {
    private static final String LOGTAG = "ADVICERESOURCE";
    private static final String BASEURL = "http://fucking-great-advice.ru/api/random";

    public AdviceResource() {}
    
    public Map<String, String> fetch() throws IOException {
        String js = from(BASEURL);
        BotLogger.info(LOGTAG, js);
        return new Gson().fromJson(js, Map.class);
    }
}