package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.logging.BotLogger;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.Feed;
import net.mamot.bot.feed.atom.AtomFeed;
import net.mamot.bot.feed.printer.EntryPrinter;

import java.util.HashMap;
import java.util.Map;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static net.mamot.bot.services.Stickers.THINK;

public class AtomFeedTask extends CustomTimerTask {

    private static final long POLLING_DAILY = 86400000;
    private final String url;
    private final int subscriber;
    private final EntryPrinter printer;

    private final FeedRepo repo;

    public AtomFeedTask(String url, int subscriber, EntryPrinter printer, FeedRepo repo) {
        super("Feed polling task: " + url, -1);
        this.url = url;
        this.subscriber = subscriber;
        this.printer = printer;
        this.repo = repo;
    }

    @Override
    public void execute() {
        try {
            Feed feed = new AtomFeed(url);
            for (Entry entry : feed) {
                if (!isLastPosted(entry)) {
                    post(entry);
                } else {
                    break;
                }
            }
            setLastPosted(feed.get());
        } catch (AtomFeed.RetrieveFeedException e) {
            BotLogger.error("AtomFeedTask", e);
        }
    }

    @Override
    public long computeDelay() {
        return POLLING_DAILY;
    }

    private void post(Entry entry) {
        bot.execute(new SendSticker(subscriber, THINK.id()));
        bot.execute(new SendMessage(subscriber, printer.print(entry)).parseMode(HTML).disableWebPagePreview(false));
    }

    private boolean isLastPosted(Entry entry) {
        return entry.getId().equals(repo.getLastEntryId(url));
    }

    private void setLastPosted(Entry entry) {
        repo.setLastEntryId(url, entry.getId());
    }

    public static class FeedRepo {

        private Map<String, String> storage = new HashMap<>();

        public String getLastEntryId(String url) {
            return storage.get(url);
        }

        public void setLastEntryId(String url, String id) {
            storage.put(url, id);
        }
    }
}