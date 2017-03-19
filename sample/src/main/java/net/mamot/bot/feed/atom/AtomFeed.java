package net.mamot.bot.feed.atom;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.Feed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AtomFeed implements Feed {

    private static final int SINGLE = 0;

    private final SyndFeed feed;

    public AtomFeed(String path) {
        this(getURL(path));
    }

    public AtomFeed(URL resource) {
        try {
            feed = new SyndFeedInput().build(new XmlReader(resource));
        } catch (FeedException | IOException e) {
            throw new RetrieveFeedException("Error during retrieving feed entry.", e);
        }
    }

    @Override
    public Entry get() {
        Iterator<Entry> i = iterator();
        return i.hasNext() ? i.next() : null;
    }

    @Override
    public List<Entry> get(int number) {
        if (number < 1) {
            throw new IllegalArgumentException("Number of requested entries should be greater than zero.");
        }
        List<Entry> entries = new ArrayList<>(number);
        Iterator<Entry> i = iterator();
        for (int n = 0; n < number && i.hasNext(); n++) {
            entries.add(i.next());
        }
        return entries;
    }

    public Iterator<Entry> iterator() {
        return new Iterator<Entry>() {

            private int currentIndex;

            public boolean hasNext() {
                return currentIndex < feed.getEntries().size();
            }

            public Entry next() {
                SyndEntry atomEntry = feed.getEntries().get(currentIndex++);

                return new Entry(
                        atomEntry.getUri(),
                        atomEntry.getTitle(),
                        atomEntry.getPublishedDate(),
                        atomEntry.getContents().get(SINGLE).getValue(),
                        atomEntry.getLink());
            }
        };
    }

    private static URL getURL(String path) {
        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            throw new RetrieveFeedException("Error during retrieving feed entry.", e);
        }
    }

    public static class RetrieveFeedException extends RuntimeException {
        public RetrieveFeedException(String message, Exception e) {
            super(message, e);
        }
    }
}