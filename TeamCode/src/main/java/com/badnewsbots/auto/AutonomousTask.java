package com.badnewsbots.auto;

public interface AutonomousTask {
    boolean isTaskCompleted();
    void init();
    void updateTask(double deltaTime);
}


