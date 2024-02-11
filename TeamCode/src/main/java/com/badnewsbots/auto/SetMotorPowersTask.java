package com.badnewsbots.auto;

import com.badnewsbots.hardware.drivetrains.MecanumDrive;

public final class SetMotorPowersTask implements AutonomousTask {
    private final MecanumDrive drive;
    private double leftX, leftY, rightX, speedMultiplier;

    @Override
    public boolean isTaskCompleted() {
        return true;
    }

    @Override
    public void init() {
        drive.setMotorPowerFromGamepadVector(leftX, leftY, rightX, speedMultiplier);
    }

    @Override
    public void updateTask(double deltaTime) {

    }

    public SetMotorPowersTask(MecanumDrive drive, double leftX, double leftY, double rightX, double speedMultiplier) {
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.speedMultiplier = speedMultiplier;
        this.drive = drive;
    }

}
