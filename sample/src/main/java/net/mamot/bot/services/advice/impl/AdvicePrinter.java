package net.mamot.bot.services.advice.impl;

import net.mamot.bot.services.MessagePrinter;

import java.util.Map;

public class AdvicePrinter implements MessagePrinter {

    public String print(Map<String, String> data) {
        return String.format("%s", data.get("text").replace("&nbsp;"," "));
    }
}