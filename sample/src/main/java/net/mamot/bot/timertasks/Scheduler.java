package net.mamot.bot.timertasks;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import static com.pengrad.telegrambot.logging.BotLogger.severe;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class Scheduler {
    private static final String LOGTAG = "Scheduler";
    private static final ThreadPoolTaskScheduler executorService = new ThreadPoolTaskScheduler();

    private static volatile Scheduler instance;

    private Scheduler() {
        executorService.initialize();
    }

    public static Scheduler getInstance() {
        final Scheduler currentInstance;
        if (instance == null) {
            synchronized (Scheduler.class) {
                if (instance == null) {
                    instance = new Scheduler();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }

        return currentInstance;
    }

    public void schedule(TimerTask task) {
        if (task.getTimes() != 0) {
            long delay = task.computeDelay();
            executorService.schedule(getWrapped(task), new PeriodicTrigger(delay, MILLISECONDS));
        }
    }

    public void schedule(Task task, String cron) {
        executorService.schedule(task::execute, new CronTrigger(cron));
    }

    private Runnable getWrapped(TimerTask task) {
        return () -> {
            try {
                task.execute();
                task.reduceTimes();
                schedule(task);
            } catch (Exception e) {
                severe(LOGTAG, "Bot threw an unexpected exception at Scheduler", e);
            }
        };
    }

    public void stop() {
        executorService.shutdown();
    }

    @Override
    public String toString() {
        return executorService.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.stop();
        } finally {
            super.finalize();
        }
    }
}
