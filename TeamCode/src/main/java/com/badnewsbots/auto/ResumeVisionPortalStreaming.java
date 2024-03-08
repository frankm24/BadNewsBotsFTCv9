package com.badnewsbots.auto;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

public class ResumeVisionPortalStreaming implements AutonomousTask {
    private final VisionPortal visionPortal;

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public void init() {
        visionPortal.resumeStreaming();
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void updateTask(double deltaTime) {}

    public ResumeVisionPortalStreaming(VisionPortal visionPortal) {
        this.visionPortal = visionPortal;
    }
}

