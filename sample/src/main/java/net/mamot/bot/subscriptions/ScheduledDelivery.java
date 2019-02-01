package net.mamot.bot.subscriptions;

public class ScheduledDelivery implements Delivery {
    private final Delivery inner;

    public ScheduledDelivery(Delivery inner) {
        this.inner = inner;
    }

    @Override
    public void deliver() {
        inner.deliver();
    }
}
