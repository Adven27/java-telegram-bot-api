package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.logging.BotLogger;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.Feed;
import net.mamot.bot.feed.atom.AtomFeed.RetrieveFeedException;
import net.mamot.bot.feed.printer.EntryPrinter;
import net.mamot.bot.services.impl.PGSQLRepo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        super("Feed polling task: " + feed.getUrl().getHost(), -1);
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
                if (!isPosted(entry)) {
                    post(entry);
                    setPosted(entry);
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

    private boolean isPosted(Entry entry) {
        return repo.isPosted(feed.getUrl().getHost(), entry.getId());
    }

    private void setPosted(Entry entry) {
        repo.setPosted(feed.getUrl().getHost(), entry.getId());
    }

    public interface FeedRepo {
        boolean isPosted(String url, String id);

        void setPosted(String url, String id);
    }

    public static class InMemoryFeedRepo implements FeedRepo {

        private Map<String, Set<String>> storage = new HashMap<>();

        public boolean isPosted(String url, String id) {
            Set<String> feed = storage.get(url);
            return feed != null && feed.contains(id);
        }

        public void setPosted(String url, String id) {
            Set<String> feed = storage.computeIfAbsent(url, v -> new HashSet<>());
            feed.add(id);
        }
    }

    public static class PGSQLFeedRepo extends PGSQLRepo implements FeedRepo {

        public PGSQLFeedRepo(String databaseUrl) {
            super(databaseUrl, "feeds", "last_posted");
        }

        @Override
        public boolean isPosted(String url, String id) {
            return exists(getUniqueURL(url, id), id);
        }

        @Override
        public void setPosted(String url, String id) {
            insert(getUniqueURL(url, id), id);
        }

        private String getUniqueURL(String url, String id) {
            return url + ":" + id;
        }
    }
}