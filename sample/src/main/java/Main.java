import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import com.pengrad.telegrambot.model.Chat;
import net.mamot.bot.commands.*;
import net.mamot.bot.services.LocalizationService;
import net.mamot.bot.services.advice.impl.AdvicePrinter;
import net.mamot.bot.services.advice.impl.AdviceResource;
import net.mamot.bot.services.bardak.BardakMenu;
import net.mamot.bot.services.games.impl.LeaderBoardImpl;
import net.mamot.bot.services.games.impl.PGSQLGameLeaderBoardRepo;
import net.mamot.bot.services.games.impl.PGSQLGameRepo;
import net.mamot.bot.services.impl.DAO;
import net.mamot.bot.services.impl.MessageFromURL;
import net.mamot.bot.services.joke.impl.JokePrinter;
import net.mamot.bot.services.joke.impl.JokeResource;
import net.mamot.bot.services.lights.impl.UpnpBridgeAdapter;
import net.mamot.bot.services.poll.PollsInMemRepo;
import net.mamot.bot.services.quote.impl.QuotePrinter;
import net.mamot.bot.services.quote.impl.QuoteResource;
import net.mamot.bot.services.twitter.TwitterServiceImpl;
import net.mamot.bot.services.weather.Weather;
import net.mamot.bot.services.weather.impl.SimpleWeather;
import net.mamot.bot.services.weather.impl.WeatherLoggingDecorator;
import net.mamot.bot.services.weather.impl.WeatherPrinter;
import net.mamot.bot.services.weather.impl.WeatherResource;

import static com.pengrad.telegrambot.TelegramBotAdapter.build;
import static com.pengrad.telegrambot.TelegramBotAdapter.buildDebug;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static com.pengrad.telegrambot.request.SendSticker.sticker;
import static java.lang.Boolean.parseBoolean;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static net.mamot.bot.services.Stickers.HELP;

public class Main {

    public static void main(String[] args) {
        final String token = System.getenv("TELEGRAM_TOKEN");
        final boolean debug = parseBoolean(System.getenv("DEBUG"));
        final TelegramBot bot = debug? buildDebug(token) : build(token);
        final UpdateHandler[] handlers = updateHandlers();

        bot.setUpdatesListener(new HandlersChainListener(bot,
                (b, u) -> {
                    Chat chat = u.message().chat();
                    b.execute(sticker(chat, HELP.id()));
                    b.execute(message(chat, helpMessage(handlers)));
                    return true;
                },
                handlers
        ));
    }

    private static String helpMessage(UpdateHandler[] handlers) {
        return stream(handlers).filter(h -> h instanceof MessageCommand).
            map(h -> ((MessageCommand) h).description()).
            collect(joining("\n"));
    }

    private static UpdateHandler[] updateHandlers() {
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
                new TwitterGirlCommand(new TwitterServiceImpl())};
    }
}