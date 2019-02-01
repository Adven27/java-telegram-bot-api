package net.mamot.bot.publications;

import java.util.Collection;

public interface PublicationsSource<T extends Publication> extends Iterable<T> {
    T get();
    Collection<T> get(int number);
}
