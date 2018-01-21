package net.mamot.bot.services.holidays;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

public class WeekendsHolidayService implements HolidayService {

    @Override
    public boolean isHoliday(LocalDateTime date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == SATURDAY || dayOfWeek == SUNDAY;
    }
}