package net.mamot.bot.timertasks;

public final class RepetitiveTask extends TimerTask {

    private final long delay;

    public RepetitiveTask(Task task, long delay) {
        super(task, -1);
        this.delay = delay;
    }

    @Override
    public long computeDelay() {
        return delay;
    }
}