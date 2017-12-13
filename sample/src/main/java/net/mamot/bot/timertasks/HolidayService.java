package net.mamot.bot.timertasks;

import net.mamot.bot.services.impl.Resource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.pengrad.telegrambot.logging.BotLogger.error;

public class HolidayService {

    private static final String TAG = HolidayService.class.getName();

    private static final String URL = "https://isdayoff.ru/";

    private final Resource res;

    public HolidayService(Resource res) {
        this.res = res;
    }

    public boolean isHoliday(Date date) {
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

    private String getRequestURL(Date date) {
        return URL + getFormattedDate(date);
    }

    private String getFormattedDate(Date date) {
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

}
