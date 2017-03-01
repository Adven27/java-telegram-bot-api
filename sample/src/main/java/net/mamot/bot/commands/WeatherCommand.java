package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.logging.BotLogger;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.services.weather.Weather;

import static com.pengrad.telegrambot.model.request.ParseMode.Markdown;
import static com.pengrad.telegrambot.request.SendMessage.message;

public class WeatherCommand extends MessageCommand {
    private static final String LOGTAG = "WEATHERCOMMAND";
    private final Weather weather;

    public WeatherCommand(Weather weather) {
        super("/weather", "current weather");
        this.weather = weather;
    }
    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(message(chat, print()).disableWebPagePreview(true).parseMode(Markdown));
    }

    public String print() {
        try {
            return weather.printCurrentFor(39.888599, 59.2187, "ru", "metric") + "\n\n" +
                   weather.printForecastFor(39.888599, 59.2187, "ru", "metric");
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            return "Связь с атмосферой потеряна...";
        }
    }
}