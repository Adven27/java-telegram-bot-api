package net.mamot.bot.timertasks;

import net.mamot.bot.services.holidays.HolidayService;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalDateTime.now;
import static net.mamot.bot.utils.DateUtils.getDurationInMillis;

public final class WorkingDayTask extends TimerTask {

    private final LocalTime time;
    private final WorkingCalendar calendar;

    public WorkingDayTask(Task task, LocalTime time, WorkingCalendar calendar) {
        super(task, -1);
        this.time = time;
        this.calendar = calendar;
    }

    @Override
    public long computeDelay() {
        final LocalDateTime now = now();
        LocalDateTime nextTime = now.with(time);
        if (!calendar.isWorkingDay(nextTime) || getDurationInMillis(now, nextTime) < 0) {
            nextTime = calendar.nextWorkingDay(nextTime);
        }
        return getDurationInMillis(now, nextTime);
    }

    public static class WorkingCalendar {
        private final HolidayService holidayService;

        public WorkingCalendar(HolidayService holidayService) {
            this.holidayService = holidayService;
        }

        public boolean isWorkingDay(LocalDateTime day) {
            return !holidayService.isHoliday(day);
        }

        public LocalDateTime nextWorkingDay(LocalDateTime day) {
            LocalDateTime next = day.plusDays(1);
            while (!isWorkingDay(next)) {
                next = next.plusDays(1);
            }
            return next;
        }
    }
}
