package net.mamot.bot.subscriptions;

import net.mamot.bot.publications.PublicationsSource;
import net.mamot.bot.publications.Publisher;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;

public class SubscriberTest {

    @Test
    public void subscribeShouldAddSubscriptionOnPublisher() {
        SubscriberImpl sut = new SubscriberImpl();
        Publisher publisher = new PublisherStub();

        sut.subscribe(publisher);
        assertThat(sut.subscriptions(), contains(subscriptionOn(publisher)));
    }

    @Test
    public void unsubscribeShouldRemoveSubscriptionOnPublisher() {
        SubscriberImpl sut = new SubscriberImpl();
        Publisher publisher = new PublisherStub();
        sut.subscribe(publisher);

        sut.unsubscribe(publisher);
        assertThat(sut.subscriptions(), not(contains(subscriptionOn(publisher))));
    }

    private Matcher<Subscription> subscriptionOn(Publisher publisher) {
        return new CustomMatcher<Subscription>("Unexpected publisher") {
            @Override
            public boolean matches(Object s) {
                return publisher.equals(((Subscription) s).publisher());
            }
        };
    }

    private class PublisherStub implements Publisher {
        @Override
        public PublicationsSource publications() {
            return null;
        }
    }
}
