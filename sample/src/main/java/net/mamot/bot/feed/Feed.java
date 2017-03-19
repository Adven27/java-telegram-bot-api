package net.mamot.bot.feed;

import java.util.List;

public interface Feed extends Iterable<Entry> {

    Entry get();

    List<Entry> get(int number);
}
