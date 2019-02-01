package net.mamot.bot.subscriptions;

import net.mamot.bot.publications.Publisher;

import java.util.Collection;

public interface Subscriber {
    void subscribe(Publisher publisher);

    Collection<Subscription> subscriptions();

    void unsubscribe(Publisher publisher);
}
