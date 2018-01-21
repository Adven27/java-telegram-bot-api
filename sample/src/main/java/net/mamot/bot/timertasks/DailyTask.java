package net.mamot.bot.timertasks;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalDateTime.now;
import static net.mamot.bot.utils.DateUtils.getDurationInMillis;

public final class DailyTask extends TimerTask {

    private final LocalTime time;

    public DailyTask(Task task, LocalTime time) {
        super(task, -1);
        this.time = time;
    }

    @Override
    public long computeDelay() {
        final LocalDateTime now = now();
        LocalDateTime nextTime = now.with(time);
        while (getDurationInMillis(now, nextTime) < 0) {
            nextTime = nextTime.plusDays(1);
        }
        return getDurationInMillis(now, nextTime);
    }
}
