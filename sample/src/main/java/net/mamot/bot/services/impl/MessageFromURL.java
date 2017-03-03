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

    public String print() throws IOException {
        return printer.print(resource.fetch());
    }
}