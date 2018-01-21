package net.mamot.bot.services.holidays;

import java.time.LocalDateTime;

public interface HolidayService {
    boolean isHoliday(LocalDateTime date);
}
