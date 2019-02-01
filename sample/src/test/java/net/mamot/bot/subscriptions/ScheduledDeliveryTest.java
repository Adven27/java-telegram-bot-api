package net.mamot.bot.subscriptions;

import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ScheduledDeliveryTest {

    @Test
    public void shouldDeliverBySchedule() {
        Delivery delivery = mock(Delivery.class);
        ScheduledDelivery sut = new ScheduledDelivery(delivery);

        sut.deliver();
        verify(delivery).deliver();
    }
}