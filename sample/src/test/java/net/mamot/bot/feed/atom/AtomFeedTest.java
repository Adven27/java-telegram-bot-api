package net.mamot.bot.feed.atom;

import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.atom.AtomFeed.RetrieveFeedException;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AtomFeedTest {

    private static final int FEED_SIZE = 3;
    private static final int MORE_THAN_FEED_SIZE = 5;

    private AtomFeed sut;

    @Test
    public void shouldRetrieveEntryFromURL() {
        sut = new AtomFeed(getExistingResource());
        assertNotNull(sut.get());
    }

    @Test
    public void shouldRetrieveEntryFromPath() {
        sut = new AtomFeed(getExistingResourcePath());
        assertNotNull(sut.get());
    }

    @Test
    public void shouldGetFirstEntry() {
        sut = new AtomFeed(getExistingResource());

        assertEquals(sut.get().getId(), "latest");
    }

    @Test
    public void shouldGetRequestedNumberOfEntries() {
        sut = new AtomFeed(getExistingResource());

        Collection<Entry> entries = sut.get(2);
        assertNotNull(entries);
        assertEquals(entries.size(), 2);
    }

    @Test
    public void shouldGetRequestedNumberOfLatestEntriesInChronologicalOrder() {
        sut = new AtomFeed(getExistingResource());

        Collection<Entry> entries = sut.get(2);
        Iterator<Entry> iterator = entries.iterator();
        assertEquals(iterator.next().getId(), "prior");
        assertEquals(iterator.next().getId(), "latest");
    }

    @Test
    public void shouldGetAsMuchAsHas_IfRequestedMoreThenIs() {
        sut = new AtomFeed(getExistingResource());

        assertEquals(sut.get(MORE_THAN_FEED_SIZE).size(), FEED_SIZE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailForRequestedZeroNumberOfEntries() {
        sut = new AtomFeed(getExistingResource());
        sut.get(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailForRequestedNegativeNumberOfEntries() {
        sut = new AtomFeed(getExistingResource());
        sut.get(-1);
    }

    @Test
    public void shouldTraverseEntriesInChronologicalOrder() {
        sut = new AtomFeed(getExistingResource());
        String[] inChronologicalOrder = {"old", "prior", "latest"};

        int index = 0;
        for (Entry entry : sut) {
            assertEquals(entry.getId(), inChronologicalOrder[index++]);
        }
    }

    @Test(expectedExceptions = RetrieveFeedException.class)
    public void shouldFailForNonExistentResource() {
        sut = new AtomFeed(getNonExistentResource());
        sut.get(1);
    }

    @Test(expectedExceptions = RetrieveFeedException.class)
    public void shouldFailForParsingFeedError() {
        sut = new AtomFeed(getUnparsableResource());
        sut.get(1);
    }

    private URL getExistingResource() {
        return getResource("/net/mamot/bot/feed/atom/AtomExample.xml");
    }

    private String getExistingResourcePath() {
        return "http://blog.cleancoder.com/atom.xml";
    }

    private URL getNonExistentResource() {
        return getResource("");
    }

    private URL getUnparsableResource() {
        return getResource("/net/mamot/bot/feed/atom/UnparsableAtomExample.xml");
    }

    private URL getResource(String name) {
        return AtomFeedTest.class.getResource(name);
    }

}