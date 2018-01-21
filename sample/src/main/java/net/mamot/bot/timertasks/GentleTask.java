package net.mamot.bot.timertasks;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class GentleTask extends TimerTask {
    private final TimerTask task;
    private final SilencePeriod period;

    public GentleTask(TimerTask task, SilencePeriod period) {
        super(task.getTask(), -1);
        this.task = task;
        this.period = period;
    }

    @Override
    public long computeDelay() {
        LocalDateTime planned = plannedTime();
        if (period.includes(planned.toLocalTime())) {
            return delayToSilencePeriodEnd(planned);
        }
        return task.computeDelay();
    }

    private long delayToSilencePeriodEnd(LocalDateTime planned) {
        return MILLIS.between(now(), period.after(planned));
    }

    private LocalDateTime plannedTime() {
        long delayInMillis = task.computeDelay();
        return now().plusNanos(inNanos(delayInMillis));
    }

    private long inNanos(long millis) {
        return MILLISECONDS.toNanos(millis);
    }

    public static class SilencePeriod {
        private final LocalTime from;
        private final LocalTime to;

        public SilencePeriod(LocalTime from, LocalTime to) {
            this.from = from;
            this.to = to;
        }

        public boolean includes(LocalTime time) {

            return from.isBefore(to)
                    ? (time.isAfter(from) || time.equals(from)) && (time.isBefore(to) || time.equals(to))
                    : (time.isBefore(to) || time.equals(to) || time.isAfter(from) || time.equals(from));
        }

        public LocalDateTime after(LocalDateTime day) {
            LocalDateTime rightBorder = from.isBefore(to) ? day.with(to) : day.with(to).plusDays(1);
            return rightBorder.plusSeconds(1);
        }
    }
}
