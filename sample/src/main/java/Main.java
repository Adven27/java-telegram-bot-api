import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.*;
import net.mamot.bot.commands.*;
import net.mamot.bot.feed.atom.AtomFeed;
import net.mamot.bot.feed.printer.impl.PreviewPrinter;
import net.mamot.bot.services.LocalizationService;
import net.mamot.bot.services.advice.impl.AdvicePrinter;
import net.mamot.bot.services.advice.impl.AdviceResource;
import net.mamot.bot.services.bardak.BardakMenu;
import net.mamot.bot.services.debts.InMemWizardSession;
import net.mamot.bot.services.games.impl.LeaderBoardImpl;
import net.mamot.bot.services.games.impl.PGSQLGameLeaderBoardRepo;
import net.mamot.bot.services.games.impl.PGSQLGameRepo;
import net.mamot.bot.services.impl.DAO;
import net.mamot.bot.services.impl.MessageFromURL;
import net.mamot.bot.services.impl.Registry;
import net.mamot.bot.services.joke.impl.JokePrinter;
import net.mamot.bot.services.joke.impl.JokeResource;
import net.mamot.bot.services.lights.impl.UpnpBridgeAdapter;
import net.mamot.bot.services.poll.PollsInMemRepo;
import net.mamot.bot.services.quote.impl.QuotePrinter;
import net.mamot.bot.services.quote.impl.QuoteResource;
import net.mamot.bot.services.twitter.TwitterService;
import net.mamot.bot.services.weather.Weather;
import net.mamot.bot.services.weather.impl.SimpleWeather;
import net.mamot.bot.services.weather.impl.WeatherLoggingDecorator;
import net.mamot.bot.services.weather.impl.WeatherPrinter;
import net.mamot.bot.services.weather.impl.WeatherResource;
import net.mamot.bot.timertasks.*;
import net.mamot.bot.timertasks.FeedTask.PGSQLFeedRepo;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.pengrad.telegrambot.TelegramBotAdapter.build;
import static com.pengrad.telegrambot.TelegramBotAdapter.buildDebug;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static com.pengrad.telegrambot.request.SendSticker.sticker;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static net.mamot.bot.commands.TwitterGirlCommand.GIRL_NAME_IN_TWITTER;
import static net.mamot.bot.services.Stickers.HELP;

public class Main {

    private static final int SBT_TEAM_CHAT_ID = parseInt(System.getenv("TEAM_CHAT"));
    private static final int CHAT_TO_REPOST = parseInt(System.getenv("REPOST_CHAT"));

    private static final TwitterService twitter = (TwitterService) Registry.provide(TwitterService.class);

    private static final int FEED_FETCH_LIMIT = 5;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        final String token = System.getenv("TELEGRAM_TOKEN");
        final boolean debug = parseBoolean(System.getenv("DEBUG"));
        final TelegramBot bot = debug ? buildDebug(token) : build(token);
        final UpdateHandler[] handlers = updateHandlers();

        bot.setUpdatesListener(new HandlersChainListener(
                bot,
                (b, u) -> u.message() != null
                        ? printHelp(handlers, b, u.message().chat())
                        : repostFromChannel(b, u),
                handlers
        ));
        scheduleTasks(bot, getTimerTasks());
    }

    private UpdateHandler[] updateHandlers() {
        final LocalizationService localizationService = new LocalizationService();
        final DAO dao = new DAO();
        final WeatherPrinter weatherPrinter = new WeatherPrinter(localizationService, dao);
        final Weather weather = new WeatherLoggingDecorator(
                new SimpleWeather(weatherPrinter, new WeatherResource()), localizationService);

        return new UpdateHandler[]{
                new LightsCommand(new UpnpBridgeAdapter()),
                new WeatherCommand(weather),
                new TicTacToeCommand(),
                new Game2048Command(new PGSQLGameRepo(), new LeaderBoardImpl(new PGSQLGameLeaderBoardRepo())),
                new AdviceCommand(new MessageFromURL(new AdviceResource(), new AdvicePrinter())),
                new QuoteCommand(new MessageFromURL(new QuoteResource(), new QuotePrinter())),
                new SupCommand(dao),
                new PollCommand(new PollsInMemRepo()),
                new JokeCommand(new MessageFromURL(new JokeResource(), new JokePrinter())),
                new BardakCommand(new BardakMenu(dao)),
                new ImgFromTextCommand(),
                new DebtsCommand(new InMemWizardSession()),
                new TwitterGirlCommand(twitter),
                new UncleBobCommand(new PreviewPrinter()),
                new SexyGirlCommand(twitter),
                new BeautifulGirlCommand()
        };
    }

    private boolean printHelp(UpdateHandler[] handlers, TelegramBot b, Chat chat) {
        b.execute(sticker(chat, HELP.id()));
        b.execute(message(chat, helpMessage(handlers)));
        return true;
    }

    private String helpMessage(UpdateHandler[] handlers) {
        return stream(handlers).filter(h -> h instanceof MessageCommand).
                map(h -> ((MessageCommand) h).description()).
                collect(joining("\n"));
    }

    private static boolean repostFromChannel(TelegramBot b, Update u) {
        Message post = u.channelPost() == null ? u.editedChannelPost() : u.channelPost();
        if (post != null) {
            if (post.document() != null) {
                b.execute(new SendDocument(CHAT_TO_REPOST, post.document().fileId()));
            } else if (post.audio() != null) {
                b.execute(new SendAudio(CHAT_TO_REPOST, post.audio().fileId()));
            } else if (post.video() != null) {
                b.execute(new SendVideo(CHAT_TO_REPOST, post.video().fileId()));
            } else if (post.photo() != null) {
                b.execute(new SendPhoto(CHAT_TO_REPOST, post.photo()[0].fileId()));
            } else if (!isNullOrEmpty(post.text())) {
                b.execute(new SendMessage(CHAT_TO_REPOST, post.text()));
            }
            return true;
        }
        return false;
    }

    private void scheduleTasks(TelegramBot bot, List<CustomTimerTask> tasks) {
        for (CustomTimerTask t : tasks) {
            t.setBot(bot);
            TimerExecutor.getInstance().schedule(t);
        }
    }

    private List<CustomTimerTask> getTimerTasks() {
        List<CustomTimerTask> tasks = newArrayList();
        for (Events e : Events.values()) {
            tasks.add(new DailyTask(e.name(), -1) {
                @Override
                protected LocalDateTime startAt() {
                    return now().with(e.time());
                }

                @Override
                public void execute() {
                    bot.execute(new SendSticker(SBT_TEAM_CHAT_ID, e.sticker().id()));
                    bot.execute(new SendMessage(SBT_TEAM_CHAT_ID, e.msg()).disableWebPagePreview(true));
                }
            });
        }
        tasks.add(new TwitterTask(twitter, GIRL_NAME_IN_TWITTER, SBT_TEAM_CHAT_ID));
        tasks.add(new TwitterTask(twitter, "razbor_poletov", SBT_TEAM_CHAT_ID));
        tasks.add(new FeedTask(new AtomFeed("http://blog.cleancoder.com/atom.xml"), new PGSQLFeedRepo(), new PreviewPrinter(), SBT_TEAM_CHAT_ID, FEED_FETCH_LIMIT));
        return tasks;
    }
}