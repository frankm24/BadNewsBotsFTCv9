package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.ds.devices.GamepadEx;
import com.badnewsbots.robots.outreach.BeeLineChassis;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.badnewsbots.ds.devices.GamepadEx;
import com.badnewsbots.robots.outreach.BeeLineChassis;

@TeleOp
public final class BeeLineTeleOp extends LinearOpMode {
    // Robot object
    private BeeLineChassis robot;

    private float SpeedMultiplier = 1.0f; // scale movement speed
    private int flywheelTargetSpeed = 1000;
    private boolean flywheelOn = false;

    private enum PusherState {
        IN,
        MOVING_OUT,
        OUT,
        MOVING_IN
    }
    private PusherState pusherState = PusherState.IN;
    private double pusherTime = 0;

    private FtcDashboard ftcDashboard;

    private GamepadEx smartGamepad;

    private void mainLoop() {
        double prevTime = 0;
        while (opModeIsActive()) {
            double currentTime = getRuntime();
            double deltaTime = currentTime - prevTime;
            smartGamepad.update();
            float LeftStickY = 1 * smartGamepad.leftStickY() * SpeedMultiplier;
            float LeftStickX = smartGamepad.leftStickX() * SpeedMultiplier;
            float RightStickY = -1 * smartGamepad.rightStickY() * SpeedMultiplier;
            float RightStickX = smartGamepad.rightStickX() * SpeedMultiplier;

            if (smartGamepad.startPressed()) {
                if (SpeedMultiplier == 0.5f) {
                    SpeedMultiplier = 1.0f;
                } else {
                    SpeedMultiplier = 0.5f;
                }
            }
            // In case the flywheel defaults to the wrong direction :)
            if (smartGamepad.yPressed()) {flywheelTargetSpeed *= -1;}
            if (smartGamepad.dpadUpPressed()) {flywheelTargetSpeed += 500;}
            if (smartGamepad.dpadDownPressed()) {flywheelTargetSpeed -= 500;}
            if (smartGamepad.aPressed()) {flywheelOn = !flywheelOn;}
            if (smartGamepad.rightTriggerPressed()) {
                telemetry.addLine("right trigger pressed");
                if (pusherState == PusherState.IN) {
                    pusherState = PusherState.MOVING_OUT;
                    pusherTime = currentTime;
                    robot.pusher.setPosition(0);
                }
            }
            if (pusherState == PusherState.MOVING_OUT) {
                if (currentTime - pusherTime >= 0.460) {
                    robot.pusher.setPosition(0.48);
                    pusherTime = currentTime;
                    pusherState = PusherState.MOVING_IN;
                }
            }
            if (pusherState == PusherState.MOVING_IN) {
                if (currentTime - pusherTime >= 0.460) {
                    pusherState = PusherState.IN;
                }
            }
            if (smartGamepad.startPressed()) {
                if (SpeedMultiplier == 0.5f) {
                    SpeedMultiplier = 1.0f;
                } else {
                    SpeedMultiplier = 0.5f;
                }
            }

            if (flywheelOn) {
                robot.flywheel.setVelocity(flywheelTargetSpeed);
            } else {
                robot.flywheel.setVelocity(0);
            }

            double front_leftPower = LeftStickY - RightStickX;
            double back_leftPower = LeftStickY - RightStickX;
            double front_rightPower = LeftStickY + RightStickX;
            double back_rightPower = LeftStickY + RightStickX;

            robot.front_left.setPower(front_leftPower);
            robot.back_left.setPower(back_leftPower);
            robot.front_right.setPower(front_rightPower);
            robot.back_right.setPower(back_rightPower);

            telemetry.addData("Pusher state", pusherState);
            telemetry.addData("Flywheel target speed", flywheelTargetSpeed);
            telemetry.addData("Front left power (%)", front_leftPower*100);
            telemetry.addData("Back left power (%)", back_leftPower*100);
            telemetry.addData("Front right power (%)", front_rightPower*100);
            telemetry.addData("Back right power (%)", back_rightPower*100);
            telemetry.addData("Δt (ms)", deltaTime*1E3);
            telemetry.update();
            prevTime = currentTime;
        }
    }

    @Override
    public void runOpMode() {
        smartGamepad = new GamepadEx(gamepad1);
        robot = new BeeLineChassis(this);
        ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());

        while (!isStarted() && !isStopRequested()) {
            telemetry.addData("Status: ", "Initialized");
            telemetry.update();
            idle();
        }
        mainLoop();
    }
}



