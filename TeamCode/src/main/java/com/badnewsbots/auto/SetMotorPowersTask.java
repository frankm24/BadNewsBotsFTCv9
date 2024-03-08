package com.badnewsbots.auto;

import com.badnewsbots.hardware.drivetrains.MecanumDrive;

public final class SetMotorPowersTask implements AutonomousTask {
    private final MecanumDrive drive;
    private final double frontLeftPower, frontRightPower, backLeftPower, backRightPower;

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public void init() {
        drive.setIndividualMotorPowers(frontLeftPower, frontRightPower, backLeftPower, backRightPower);
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public void updateTask(double deltaTime) {

    }

    public SetMotorPowersTask(MecanumDrive drive, double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower) {
        this.drive = drive;
        this.frontLeftPower = frontLeftPower;
        this.frontRightPower = frontRightPower;
        this.backLeftPower = backLeftPower;
        this.backRightPower = backRightPower;
    }

}
