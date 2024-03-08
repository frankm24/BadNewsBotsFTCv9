package com.badnewsbots.auto;

import com.badnewsbots.hardware.drivetrains.MecanumDrive;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Deprecated
public class SetDrivePowerForTimeWithEncoder implements AutonomousTask {
    private final MecanumDrive drive;
    private final double leftX, leftY, rightX, speedMultiplier;
    private final Telemetry telemetry;

    private double elapsedTime = 0;
    private double time = -1;
    private boolean initialized = false;

    @Override
    public boolean isCompleted() {return elapsedTime >= time;}

    @Override
    public void init() {
        drive.setMotorPowerFromGamepadVector(leftX, leftY, rightX, speedMultiplier);
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void updateTask(double deltaTime) {
        elapsedTime += deltaTime;
        telemetry.addData("Front Left Motor Position (ticks)", drive.getFrontLeftPosition());
        telemetry.addData("Back Left Motor Position (ticks)", drive.getBackLeftPosition());
        telemetry.addData("Front Right Motor Position (ticks)", drive.getFrontRightPosition());
        telemetry.addData("Back Right Motor Position (ticks)", drive.getBackRightPosition());
    }

    public SetDrivePowerForTimeWithEncoder(MecanumDrive drive, Telemetry telemetry, double leftX, double leftY, double rightX, double speedMultiplier, double driveTimeSeconds) {
        this.drive = drive;
        this.telemetry = telemetry;
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.speedMultiplier = speedMultiplier;
        this.time = driveTimeSeconds;
    }
}
