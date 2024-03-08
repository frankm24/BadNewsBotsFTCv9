package com.badnewsbots.auto;

import com.badnewsbots.hardware.drivetrains.MecanumDrive;

public final class SetMotorPowersFromGamepadVectorTask implements AutonomousTask {
    private final MecanumDrive drive;
    private double leftX, leftY, rightX, speedMultiplier;

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public void init() {
        drive.setMotorPowerFromGamepadVector(leftX, leftY, rightX, speedMultiplier);
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void updateTask(double deltaTime) {

    }

    public SetMotorPowersFromGamepadVectorTask(MecanumDrive drive, double leftX, double leftY, double rightX, double speedMultiplier) {
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.speedMultiplier = speedMultiplier;
        this.drive = drive;
    }

}
