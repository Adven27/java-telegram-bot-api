package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.logging.BotLogger;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.feed.Feed;
import net.mamot.bot.feed.atom.AtomFeed.RetrieveFeedException;
import net.mamot.bot.feed.printer.PublicationPrinter;
import net.mamot.bot.publications.Publication;
import net.mamot.bot.services.impl.PGSQLRepo;
import net.mamot.bot.timertasks.Task.BotTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static net.mamot.bot.services.Stickers.THINK;

public class FeedTask extends BotTask {//TODO: Remove BotTask extension.

    private final int subscriber;
    private final PublicationPrinter printer;

    private final Feed<Publication> feed;
    private final FeedRepo repo;
    private final int fetchLimit;

    public FeedTask(Feed<Publication> feed, FeedRepo repo, PublicationPrinter printer, int subscriber, int fetchLimit, TelegramBot bot/*Consumer instead of bot*/) {
        super(bot);
        this.subscriber = subscriber;
        this.printer = printer;
        this.fetchLimit = fetchLimit;

        this.feed = feed;
        this.repo = repo;
    }

    @Override
    public void execute() {
        try {
            for (Publication publication : feed.get(fetchLimit)) {
                if (!isPosted(publication)) {
                    post(publication);
                    setPosted(publication);
                }
            }
        } catch (RetrieveFeedException e) {
            BotLogger.error("FeedTask", e);
        }
    }

    @Override
    public String getName() {
        return "Feed polling task: " + feed.getUrl().getHost();
    }

    private void post(Publication publication) {
        bot.execute(new SendSticker(subscriber, THINK.id()));
        bot.execute(new SendMessage(subscriber, printer.print(publication)).parseMode(HTML).disableWebPagePreview(false));
    }

    private boolean isPosted(Publication publication) {
        return repo.isPosted(feed.getUrl().getHost(), publication);
    }

    private void setPosted(Publication publication) {
        repo.setPosted(feed.getUrl().getHost(), publication);
    }

    public interface FeedRepo {
        boolean isPosted(String url, Publication publication);

        void setPosted(String url, Publication publication);
    }

    public static class InMemoryFeedRepo implements FeedRepo {

        private Map<String, Set<String>> storage = new HashMap<>();

        public boolean isPosted(String url, Publication publication) {
            Set<String> feed = storage.get(url);
            return feed != null && feed.contains(publication.getId());
        }

        public void setPosted(String url, Publication publication) {
            Set<String> feed = storage.computeIfAbsent(url, v -> new HashSet<>());
            feed.add(publication.getId());
        }
    }

    public static class PGSQLFeedRepo extends PGSQLRepo implements FeedRepo {

        public PGSQLFeedRepo(String databaseUrl) {
            super(databaseUrl, "feeds", "last_posted");
        }

        @Override
        public boolean isPosted(String url, Publication publication) {
            return exists(getUniqueURL(url, publication.getId()), publication.getId());
        }

        @Override
        public void setPosted(String url, Publication publication) {
            insert(getUniqueURL(url, publication.getId()), publication.getId());
        }

        private String getUniqueURL(String url, String id) {
            return url + ":" + id;
        }
    }
}