package com.badnewsbots.auto;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

public class ResumeVisionPortalStreaming implements AutonomousTask {
    private final VisionPortal visionPortal;
    private boolean taskCompleted = false;

    @Override
    public boolean isTaskCompleted() {
        return taskCompleted;
    }

    @Override
    public void init() {
        visionPortal.resumeStreaming();
    }

    @Override
    public void updateTask(double deltaTime) {
        taskCompleted = true;
    }

    public ResumeVisionPortalStreaming(VisionPortal visionPortal) {
        this.visionPortal = visionPortal;
    }
}

