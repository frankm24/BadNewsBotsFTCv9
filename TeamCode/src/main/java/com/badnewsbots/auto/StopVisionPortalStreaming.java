package com.badnewsbots.auto;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

public class StopVisionPortalStreaming implements AutonomousTask {
    private final VisionPortal visionPortal;
    private boolean taskCompleted = false;

    @Override
    public boolean isCompleted() {
        return taskCompleted;
    }

    @Override
    public void init() {
        visionPortal.stopStreaming();
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void updateTask(double deltaTime) {
        taskCompleted = true;
    }

    public StopVisionPortalStreaming(VisionPortal visionPortal) {
        this.visionPortal = visionPortal;
    }
}

