package net.mamot.bot.timertasks;


public abstract class TimerTask {

    private int times = 1;
    private final Task task;

    public TimerTask(Task task, int times) {
        this.times = times;
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public int getTimes() {
        return this.times;
    }

    public void reduceTimes() {
        if (this.times > 0) {
            this.times -= 1;
        }
    }

    public void execute() {
        task.execute();
    }

    public abstract long computeDelay();
}