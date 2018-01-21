package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.TelegramBot;
import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.Feed;
import net.mamot.bot.feed.printer.EntryPrinter;
import net.mamot.bot.timertasks.FeedTask.FeedRepo;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FeedTaskTest {

    private URL url;

    @Mock
    private FeedRepo repo;
    @Mock
    private EntryPrinter printer;
    @Mock
    private Feed feed;

    private FeedTask sut;

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        initMocks(this);
        url = new URL("http://www.example.com");

        when(feed.getUrl()).thenReturn(url);
        when(feed.get(anyInt())).thenReturn(getFeedEntries());

        sut = new FeedTask(feed, repo, printer, 0, 5, mock(TelegramBot.class));
    }

    @Test
    public void shouldStorePostedId() {
        InOrder inOrder = inOrder(repo);

        sut.execute();

        inOrder.verify(repo).setPosted(url.getHost(), "id1");
        inOrder.verify(repo).setPosted(url.getHost(), "id2");
        inOrder.verify(repo).setPosted(url.getHost(), "id3");
    }

    @Test
    public void shouldNotStoreAlreadyPostedAsPosted() {
        withAlreadyPosted("id2");

        sut.execute();

        verify(repo, never()).setPosted(url.getHost(), "id2");
    }

    @Test
    public void shouldPostNotPosted() {
        sut.execute();

        verify(printer).print(argThat(entryWithId("id1")));
        verify(printer).print(argThat(entryWithId("id2")));
        verify(printer).print(argThat(entryWithId("id3")));
    }

    @Test
    public void shouldNotPostAlreadyPosted() {
        withAlreadyPosted("id2");

        sut.execute();

        verify(printer, never()).print(argThat(entryWithId("id2")));
    }

    private void withAlreadyPosted(String id) {
        when(repo.isPosted(url.getHost(), id)).thenReturn(true);
    }

    private ArgumentMatcher<Entry> entryWithId(String id) {

        return new ArgumentMatcher<Entry>() {
            @Override
            public boolean matches(Object actual) {
                return id.equals(((Entry) actual).getId());
            }
        };
    }

    private List<Entry> getFeedEntries() {
        return Arrays.asList(createEntry("id1"), createEntry("id2"), createEntry("id3"));
    }

    private Entry createEntry(String id) {
        return new Entry(id, "title", new Date(), "content", "link");
    }
}