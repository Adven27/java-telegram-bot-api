package net.mamot.bot.services.impl;

import java.io.IOException;

public interface Resource {

    String from(String url) throws IOException;
}
