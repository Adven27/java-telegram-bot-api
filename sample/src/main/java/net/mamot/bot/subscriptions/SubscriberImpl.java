package net.mamot.bot.subscriptions;

import net.mamot.bot.publications.Publisher;

import java.util.Collection;
import java.util.HashSet;

public class SubscriberImpl implements Subscriber {

    private final Collection<Subscription> subscriptions = new HashSet<>();

    @Override
    public void subscribe(Publisher publisher) {
        subscriptions.add(new Subscription(this, publisher));
    }

    @Override
    public void unsubscribe(Publisher publisher) {
        subscriptions.remove(new Subscription(this, publisher));
    }

    @Override
    public Collection<Subscription> subscriptions() {
        return subscriptions;
    }
}
