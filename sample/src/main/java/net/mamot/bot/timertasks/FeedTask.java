package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.logging.BotLogger;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.Feed;
import net.mamot.bot.feed.atom.AtomFeed.RetrieveFeedException;
import net.mamot.bot.feed.printer.EntryPrinter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static net.mamot.bot.services.Stickers.THINK;

public class FeedTask extends DailyTask {

    private static final LocalTime START_TIME = LocalTime.of(17, 00);

    private final int subscriber;
    private final EntryPrinter printer;

    private final Feed feed;
    private final FeedRepo repo;
    private final int fetchLimit;

    public FeedTask(Feed feed, FeedRepo repo, EntryPrinter printer, int subscriber, int fetchLimit) {
        super("Feed polling task: " + feed.getUrl().getPath(), -1);
        this.subscriber = subscriber;
        this.printer = printer;
        this.fetchLimit = fetchLimit;

        this.feed = feed;
        this.repo = repo;
    }

    @Override
    public void execute() {
        try {
            for (Entry entry : feed.get(fetchLimit)) {
                if (!isLastPosted(entry)) {
                    post(entry);
                    setLastPosted(entry);
                } else {
                    break;
                }
            }
        } catch (RetrieveFeedException e) {
            BotLogger.error("FeedTask", e);
        }
    }

    @Override
    protected LocalDateTime startAt() {
        return LocalDateTime.now().with(START_TIME);
    }

    private void post(Entry entry) {
        bot.execute(new SendSticker(subscriber, THINK.id()));
        bot.execute(new SendMessage(subscriber, printer.print(entry)).parseMode(HTML).disableWebPagePreview(false));
    }

    private boolean isLastPosted(Entry entry) {
        return entry.getId().equals(repo.getLastEntryId(feed.getUrl().getPath()));
    }

    private void setLastPosted(Entry entry) {
        repo.setLastEntryId(feed.getUrl().getPath(), entry.getId());
    }

    public interface FeedRepo {
        String getLastEntryId(String url);

        void setLastEntryId(String url, String id);
    }

    public static class InMemoryFeedRepo implements FeedRepo {

        private Map<String, String> storage = new HashMap<>();

        public String getLastEntryId(String url) {
            return storage.get(url);
        }

        public void setLastEntryId(String url, String id) {
            storage.put(url, id);
        }
    }
}