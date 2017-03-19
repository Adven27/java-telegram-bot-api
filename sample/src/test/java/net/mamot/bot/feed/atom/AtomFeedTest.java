package net.mamot.bot.feed.atom;

import net.mamot.bot.feed.Entry;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AtomFeedTest {

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

        assertEquals(sut.get().getId(), "1");
    }

    @Test
    public void shouldGetRequestedNumberOfEntries() {
        sut = new AtomFeed(getExistingResource());

        List<Entry> entries = sut.get(3);
        assertNotNull(entries);
        assertEquals(entries.size(), 3);
    }

    @Test
    public void shouldGetRequestedNumberOfEntriesInReverseChronologicalOrder() {
        sut = new AtomFeed(getExistingResource());

        List<Entry> entries = sut.get(3);
        assertEquals(entries.get(0).getId(), "1");
        assertEquals(entries.get(1).getId(), "2");
        assertEquals(entries.get(2).getId(), "3");
    }

    @Test
    public void shouldTraverseEntriesInReverseChronologicalOrder() {
        sut = new AtomFeed(getExistingResource());

        int index = 1;
        for (Entry entry : sut) {
            assertEquals(entry.getId(), String.valueOf(index++));
        }
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
    public void shouldGetAsMuchAsHas_IfRequestedMoreThenIs() {
        sut = new AtomFeed(getExistingResource());

        assertEquals(sut.get(5).size(), 3);
    }


    @Test()
    public void shouldFailForNoNextEntry() {

    }
    @Test(expectedExceptions = AtomFeed.RetrieveFeedException.class)
    public void shouldFailForNonExistentResource() {
        sut = new AtomFeed(getNonExistentResource());
    }

    @Test(expectedExceptions = AtomFeed.RetrieveFeedException.class)
    public void shouldFailForParsingFeedError() {
        sut = new AtomFeed(getUnparsableResource());
    }

    private Entry createEntry(String id) {
        return new Entry(id, "title", new Date(), "content", "link");
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