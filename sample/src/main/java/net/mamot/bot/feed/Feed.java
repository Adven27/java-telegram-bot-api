package net.mamot.bot.feed;

import net.mamot.bot.publications.Publication;
import net.mamot.bot.publications.PublicationsSource;

import java.net.URL;

public interface Feed<T extends Publication> extends PublicationsSource<T> {
    URL getUrl();
}
