package net.mamot.bot.services.joke.impl;

import net.mamot.bot.services.MessagePrinter;
import org.jsoup.safety.Whitelist;

import java.util.Map;

import static java.lang.String.format;
import static org.jsoup.Jsoup.clean;

public class JokePrinter implements MessagePrinter {

    public String print(Map<String, String> data) {
        return format("%s \n %s", clean(data.get("text"), Whitelist.basic()).replace("<br>", ""), data.get("site"));
    }
}