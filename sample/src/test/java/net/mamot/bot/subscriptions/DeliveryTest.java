package net.mamot.bot.subscriptions;

import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;

public class DeliveryTest {

    @Test
    public void shouldDeliverPublicationToSubscribers() {
        Collection<Subscription> subscriptions = new HashSet<>();
        DeliveryImpl sut = new DeliveryImpl(subscriptions);
        sut.deliver();
    }
}
