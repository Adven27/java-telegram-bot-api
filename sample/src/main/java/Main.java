import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.request.SendVideo;
import net.mamot.bot.commands.AdviceCommand;
import net.mamot.bot.commands.Game2048Command;
import net.mamot.bot.commands.ImgFromTextCommand;
import net.mamot.bot.commands.JokeCommand;
import net.mamot.bot.commands.QuoteCommand;
import net.mamot.bot.commands.RandomVideoNote;
import net.mamot.bot.commands.SexyGirlCommand;
import net.mamot.bot.commands.SupCommand;
import net.mamot.bot.commands.TicTacToeCommand;
import net.mamot.bot.commands.UncleBobCommand;
import net.mamot.bot.commands.WeatherCommand;
import net.mamot.bot.feed.atom.AtomFeed;
import net.mamot.bot.feed.printer.impl.PreviewPrinter;
import net.mamot.bot.services.LocalizationService;
import net.mamot.bot.services.Stickers;
import net.mamot.bot.services.advice.impl.AdvicePrinter;
import net.mamot.bot.services.advice.impl.AdviceResource;
import net.mamot.bot.services.games.impl.LeaderBoardImpl;
import net.mamot.bot.services.games.impl.PGSQLGameLeaderBoardRepo;
import net.mamot.bot.services.games.impl.PGSQLGameRepo;
import net.mamot.bot.services.holidays.WeekendsHolidayService;
import net.mamot.bot.services.impl.DAO;
import net.mamot.bot.services.impl.Injector;
import net.mamot.bot.services.impl.MessageFromURL;
import net.mamot.bot.services.joke.impl.JokePrinter;
import net.mamot.bot.services.joke.impl.JokeResource;
import net.mamot.bot.services.quote.impl.QuotePrinter;
import net.mamot.bot.services.quote.impl.QuoteResource;
import net.mamot.bot.services.twitter.TwitterService;
import net.mamot.bot.services.weather.Weather;
import net.mamot.bot.services.weather.impl.SimpleWeather;
import net.mamot.bot.services.weather.impl.WeatherLoggingDecorator;
import net.mamot.bot.services.weather.impl.WeatherPrinter;
import net.mamot.bot.services.weather.impl.WeatherResource;
import net.mamot.bot.timertasks.DailyTask;
import net.mamot.bot.timertasks.Events;
import net.mamot.bot.timertasks.FeedTask;
import net.mamot.bot.timertasks.FeedTask.PGSQLFeedRepo;
import net.mamot.bot.timertasks.GentleTask;
import net.mamot.bot.timertasks.GentleTask.SilencePeriod;
import net.mamot.bot.timertasks.RepetitiveTask;
import net.mamot.bot.timertasks.Scheduler;
import net.mamot.bot.timertasks.Task;
import net.mamot.bot.timertasks.TimerTask;
import net.mamot.bot.timertasks.TwitterTask;
import net.mamot.bot.timertasks.WorkingDayTask.WorkingCalendar;

import java.time.LocalTime;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.pengrad.telegrambot.TelegramBotAdapter.build;
import static com.pengrad.telegrambot.TelegramBotAdapter.buildDebug;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static com.pengrad.telegrambot.request.SendSticker.sticker;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Long.parseLong;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static net.mamot.bot.services.Stickers.HELP;

public class Main {

    private static final long SBT_TEAM_CHAT_ID = parseLong(System.getenv("TEAM_CHAT"));
    private static final long CHAT_TO_REPOST = parseLong(System.getenv("REPOST_CHAT"));
    private static final String NY_CRON = System.getenv("NY_CRON") != null ? System.getenv("NY_CRON") : "0 0 0 01 01 ?";
    private static final String NY_CAPTION = System.getenv("NY_CAPTION");
    private static final String NY_PHOTO = System.getenv("NY_PHOTO");
    private static final String TELEGRAM_TOKEN = System.getenv("TELEGRAM_TOKEN");
    private static final String DATABASE_URL = System.getenv("DATABASE_URL");

    private static final SilencePeriod SILENCE_PERIOD = new SilencePeriod(LocalTime.of(22, 00), LocalTime.of(9, 30));

    private static final TwitterService twitter = (TwitterService) Injector.provide(TwitterService.class);
    private static final int FEED_FETCH_LIMIT = 5;

    private static final long TWITTER_POLLING_DELAY = 1800000;

    public static void main(String[] args) {
        new Main().run(TELEGRAM_TOKEN);
    }

    private void run(String token) {
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
        scheduleTasks(getTimerTasks(bot));
        Scheduler.getInstance().schedule(
            new Task.BotTask(bot) {
                @Override
                public void execute() {
                    bot.execute(new SendSticker(SBT_TEAM_CHAT_ID, Stickers.DANCE.id()));
                    bot.execute(new SendPhoto(SBT_TEAM_CHAT_ID, NY_PHOTO)
                        .caption(NY_CAPTION));
                }

                @Override
                public String getName() {
                    return "New Year!";
                }
            },
            NY_CRON
        );
    }

    private UpdateHandler[] updateHandlers() {
        final LocalizationService localizationService = new LocalizationService();
        final DAO dao = new DAO();
        final WeatherPrinter weatherPrinter = new WeatherPrinter(localizationService, dao);
        final Weather weather = new WeatherLoggingDecorator(
            new SimpleWeather(weatherPrinter, new WeatherResource()), localizationService);

        return new UpdateHandler[]{
            new WeatherCommand(weather),
            new TicTacToeCommand(),
            new Game2048Command(new PGSQLGameRepo(DATABASE_URL), new LeaderBoardImpl(new PGSQLGameLeaderBoardRepo(DATABASE_URL))),
            new AdviceCommand(new MessageFromURL(new AdviceResource(), new AdvicePrinter())),
            new QuoteCommand(new MessageFromURL(new QuoteResource(), new QuotePrinter())),
            new SupCommand(dao),
            new JokeCommand(new MessageFromURL(new JokeResource(), new JokePrinter())),
            new ImgFromTextCommand(),
            new UncleBobCommand(new PreviewPrinter()),
            new SexyGirlCommand(twitter),
            new RandomVideoNote()
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

    private void scheduleTasks(List<TimerTask> tasks) {
        for (TimerTask t : tasks) {
            Scheduler.getInstance().schedule(t);
        }
    }

    private List<TimerTask> getTimerTasks(TelegramBot bot) {
        List<TimerTask> tasks = newArrayList();
        WorkingCalendar calendar = new WorkingCalendar(new WeekendsHolidayService());
        for (Events e : Events.values()) {
            //tasks.add(new WorkingDayTask(new EventTask(e, SBT_TEAM_CHAT_ID, bot), e.time(), calendar));
        }

//        tasks.add(twitterTask(bot, GIRL_NAME_IN_TWITTER));

        tasks.add(feedTask(bot, "http://blog.cleancoder.com/atom.xml"));
//        tasks.add(feedTask(bot, "https://martinfowler.com/feed.atom"));
        return tasks;
    }

    private TimerTask twitterTask(TelegramBot bot, String twitterAccount) {
        return new GentleTask(
            new RepetitiveTask(
                new TwitterTask(twitter, twitterAccount, SBT_TEAM_CHAT_ID, bot),
                TWITTER_POLLING_DELAY),
            SILENCE_PERIOD);
    }

    private TimerTask feedTask(TelegramBot bot, String feed) {
        return new DailyTask(
            new FeedTask(
                new AtomFeed(feed),
                new PGSQLFeedRepo(DATABASE_URL),
                new PreviewPrinter(),
                SBT_TEAM_CHAT_ID, FEED_FETCH_LIMIT,
                bot),
            LocalTime.of(17, 00));
    }
}