package net.mamot.bot.subscriptions;

import net.mamot.bot.publications.Publisher;

import java.util.Objects;

public class Subscription {
    private final Subscriber subscriber;
    private final Publisher publisher;

    public Subscription(Subscriber subscriber, Publisher publisher) {
        this.subscriber = subscriber;
        this.publisher = publisher;
    }

    public Publisher publisher() {
        return publisher;
    }

    public Subscriber subscriber() {
        return subscriber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return subscriber.equals(that.subscriber) &&
                publisher.equals(that.publisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriber, publisher);
    }
}
