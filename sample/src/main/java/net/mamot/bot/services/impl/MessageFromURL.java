package net.mamot.bot.services.impl;

import net.mamot.bot.services.MessagePrinter;
import net.mamot.bot.services.URLResource;

import java.io.IOException;

public class MessageFromURL {
    private final URLResource resource;
    private final MessagePrinter printer;

    public MessageFromURL(URLResource resource, MessagePrinter printer) {
        this.resource = resource;
        this.printer = printer;
    }

    public String print() {
        try {
            return printer.print(resource.fetch());
        } catch (IOException e) {
            return "Связь с ноосферой";
        }
    }
}