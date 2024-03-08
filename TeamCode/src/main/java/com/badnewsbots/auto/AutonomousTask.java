package com.badnewsbots.auto;

public interface AutonomousTask {
    boolean isCompleted();
    void init();
    boolean isInitialized();
    void updateTask(double deltaTime);
}


