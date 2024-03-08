package com.badnewsbots.auto;

import com.qualcomm.robotcore.util.ElapsedTime;

public final class WaitSecondsTask implements AutonomousTask {
    private ElapsedTime elapsedTime;
    private final double waitTimeSeconds;
    private boolean initialized;

    @Override
    public boolean isCompleted() {
        return elapsedTime.seconds() >= waitTimeSeconds;
    }

    @Override
    public void init() {
        elapsedTime = new ElapsedTime();
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void updateTask(double deltaTime) {}

    public WaitSecondsTask(double waitTimeSeconds) {
        this.waitTimeSeconds = waitTimeSeconds;
    }
}
