package com.badnewsbots.auto;

import com.qualcomm.robotcore.util.ElapsedTime;

public final class WaitSecondsTask implements AutonomousTask {
    private ElapsedTime elapsedTime;
    private final double waitTimeSeconds;

    @Override
    public boolean isTaskCompleted() {
        return elapsedTime.seconds() >= waitTimeSeconds;
    }

    @Override
    public void init() {
        elapsedTime = new ElapsedTime();
    }

    @Override
    public void updateTask(double deltaTime) {}

    public WaitSecondsTask(double waitTimeSeconds) {
        this.waitTimeSeconds = waitTimeSeconds;
    }
}
