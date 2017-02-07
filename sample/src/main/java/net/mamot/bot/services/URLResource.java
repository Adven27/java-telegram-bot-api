package net.mamot.bot.services;

import java.io.IOException;
import java.util.Map;

public interface URLResource {
    Map<String, String> fetch() throws IOException;
}