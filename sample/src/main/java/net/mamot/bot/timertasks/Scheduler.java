package net.mamot.bot.timertasks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.pengrad.telegrambot.logging.BotLogger.severe;
import static com.pengrad.telegrambot.logging.BotLogger.warn;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class Scheduler {
    private static final String LOGTAG = "Scheduler";
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private static volatile Scheduler instance;

    private Scheduler() {
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
            executorService.schedule(getWrapped(task), delay, MILLISECONDS);
        }
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
        warn(LOGTAG, "Rejected tasks: " + executorService.shutdownNow().size());
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
