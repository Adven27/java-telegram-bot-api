package net.mamot.bot.feed;

import java.net.URL;
import java.util.List;

public interface Feed extends Iterable<Entry> {

    URL getUrl();

    Entry get();

    List<Entry> get(int number);
}
