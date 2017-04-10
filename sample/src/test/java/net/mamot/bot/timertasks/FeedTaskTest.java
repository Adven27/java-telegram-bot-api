package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.TelegramBot;
import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.Feed;
import net.mamot.bot.feed.printer.EntryPrinter;
import net.mamot.bot.timertasks.FeedTask.FeedRepo;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

public class FeedTaskTest {

    private URL url;

    private FeedRepo repo;
    private EntryPrinter printer;
    private Feed feed;

    private FeedTask sut;

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        url = new URL("http://www.example.com");

        mockFeed();
        mockRepo();
        mockPrinter();

        sut = new FeedTask(feed, repo, printer, 0, 5);

        mockBot();
    }

    @Test
    public void shouldStoreLastPostedId() {
        InOrder inOrder = inOrder(repo);

        sut.execute();

        inOrder.verify(repo).setLastEntryId(url.getPath(), "id1");
        inOrder.verify(repo).setLastEntryId(url.getPath(), "id2");
        inOrder.verify(repo).setLastEntryId(url.getPath(), "id3");
    }

    @Test
    public void shouldNotStoreAlreadyPostedAsLastPosted() {
        withAlreadyPosted("id2");

        sut.execute();

        verify(repo).setLastEntryId(url.getPath(), "id1");
        verify(repo, never()).setLastEntryId(url.getPath(), "id2");
        verify(repo, never()).setLastEntryId(url.getPath(), "id3");
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

        verify(printer).print(argThat(entryWithId("id1")));
        verify(printer, never()).print(argThat(entryWithId("id2")));
        verify(printer, never()).print(argThat(entryWithId("id3")));

    }

    private void withAlreadyPosted(String id) {
        when(repo.getLastEntryId(url.getPath())).thenReturn(id);
    }

    private ArgumentMatcher<Entry> entryWithId(String id) {

        return new ArgumentMatcher<Entry>() {
            @Override
            public boolean matches(Object actual) {
                return id.equals(((Entry)actual).getId());
            }
        };
    }

    private void mockFeed() {
        feed = mock(Feed.class);
        when(feed.getUrl()).thenReturn(url);

        when(feed.get(anyInt())).thenReturn(getFeedEntries());
    }

    private void mockRepo() {
        repo = mock(FeedRepo.class);
    }

    private void mockPrinter() {
        printer = mock(EntryPrinter.class);
    }

    private void mockBot() {
        sut.bot = mock(TelegramBot.class);
    }

    private List<Entry> getFeedEntries() {
        return Arrays.asList(createEntry("id1"), createEntry("id2"), createEntry("id3"));
    }

    private Entry createEntry(String id) {
        return new Entry(id, "title", new Date(), "content", "link");
    }

}