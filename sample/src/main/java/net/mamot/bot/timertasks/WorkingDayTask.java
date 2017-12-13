package net.mamot.bot.timertasks;

public class WorkingDayTask extends CustomTimerTask {

    private final DailyTask task;

    public WorkingDayTask(String taskName, int times, DailyTask task) {
        super(taskName, times);
        this.task = task;
    }

    @Override
    public void execute() {
        if (isWorkingDay()) {
            task.execute();
        }
    }

    private boolean isWorkingDay() {
        return true;
    }

    @Override
    public long computeDelay() {
        return task.computeDelay();
    }
}
