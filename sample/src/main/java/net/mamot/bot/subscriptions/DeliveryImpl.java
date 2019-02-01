package net.mamot.bot.subscriptions;

import java.util.Collection;

public class DeliveryImpl implements Delivery {
    private final Collection<Subscription> subscriptions;

    public DeliveryImpl(Collection<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public void deliver() {
        subscriptions.forEach(s -> s.subscriber());
    }
}
