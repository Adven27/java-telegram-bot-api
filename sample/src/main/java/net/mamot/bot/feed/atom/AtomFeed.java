package net.mamot.bot.feed.atom;

import com.rometools.rome.feed.synd.SyndEntry;
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
import java.util.ListIterator;

public class AtomFeed implements Feed {

    private static final int SINGLE = 0;

    private final URL url;

    public AtomFeed(String path) {
        this(getURL(path));
    }

    public AtomFeed(URL resource) {
        this.url = resource;
    }

    private List<SyndEntry> loadRawFeed() {
        try {
            return new SyndFeedInput().build(new XmlReader(url)).getEntries();
        } catch (FeedException | IOException e) {
            throw new RetrieveFeedException("Error during retrieving feed entry.", e);
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Entry get() {
        List<SyndEntry> raw = loadRawFeed();
        return raw.size() > 0 ? toEntry(raw.get(0)) : null;
    }

    @Override
    public List<Entry> get(int number) {
        if (number < 1) {
            throw new IllegalArgumentException("Number of requested entries should be greater than zero.");
        }

        int total = number;
        List<SyndEntry> raw = loadRawFeed();
        if (number - raw.size() > 0) {
            total = raw.size();
        }

        return getNumberOfLatestInReverseOrder(total);
    }

    public Iterator<Entry> iterator() {
        return new Iterator<Entry>() {
            private final ReverseIterator<SyndEntry> reverse = new ReverseIterator<>(loadRawFeed());

            public boolean hasNext() {
                return reverse.hasNext();
            }

            public Entry next() {
                return toEntry(reverse.next());
            }
        };
    }

    private List<Entry> getNumberOfLatestInReverseOrder(int total) {
        List<Entry> entries = new ArrayList<>(total);
        Iterator<SyndEntry> i = new ReverseIterator<>(loadRawFeed().subList(0, total));

        while (i.hasNext()) {
            entries.add(toEntry(i.next()));
        }
        return entries;
    }

    private Entry toEntry(SyndEntry rawEntry) {
        return new Entry(
                rawEntry.getUri(),
                rawEntry.getTitle(),
                rawEntry.getPublishedDate(),
                rawEntry.getContents().get(SINGLE).getValue(),
                rawEntry.getLink());
    }

    private static class ReverseIterator<T> implements Iterator<T> {

        private final ListIterator<T> iterator;

        public ReverseIterator(List<T> items) {
            iterator = items.listIterator(items.size());
        }

        @Override
        public boolean hasNext() {
            return iterator.hasPrevious();
        }

        @Override
        public T next() {
            return iterator.previous();
        }
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