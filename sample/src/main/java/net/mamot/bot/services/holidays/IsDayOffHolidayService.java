package net.mamot.bot.services.holidays;

import net.mamot.bot.services.impl.Resource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.pengrad.telegrambot.logging.BotLogger.error;

public class IsDayOffHolidayService implements HolidayService {

    private static final String TAG = IsDayOffHolidayService.class.getName();

    private static final String URL = "https://isdayoff.ru/";

    private final Resource res;

    public IsDayOffHolidayService(Resource res) {
        this.res = res;
    }

    @Override
    public boolean isHoliday(LocalDateTime date) {
        String url = getRequestURL(date);
        try {
            String response = res.from(url);
            switch (response) {
                case "0":
                    return false;
                case "1":
                    return true;
                default:
                    error(TAG, url + " responses " + response);
                    return false;
            }
        } catch (IOException e) {
            error(TAG, "Could not retrieve information about calendar from " + url, e);
            return false;
        }
    }

    private String getRequestURL(LocalDateTime date) {
        return URL + getFormattedDate(date);
    }

    private String getFormattedDate(LocalDateTime date) {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(date);
    }

}
