package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.hardware.ds.GamepadEx;
import com.badnewsbots.hardware.robots.CenterstageCompBot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class CenterstageTeleOp extends LinearOpMode {
    private CenterstageCompBot robot;
    private GamepadEx gamepadEx1;
    private MecanumDrive drive;

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17);
        gamepadEx1 = new GamepadEx(gamepad1);

        robot = new CenterstageCompBot(hardwareMap);
        drive = robot.getDrive();

        double prevTime = getRuntime();

        while (!isStarted() && !isStopRequested()) {
            double currentTime = getRuntime();
            double deltaTime = currentTime - prevTime;

            gamepadEx1.update();
            initLoop(deltaTime);
            telemetry.update();
        }

        while (opModeIsActive()) {
            double currentTime = getRuntime();
            double deltaTime = currentTime - prevTime;

            gamepadEx1.update();
            controlLoop(deltaTime);
            telemetry.update();
        }
    }

    private void initLoop(double deltaTime) {

    }

    private void controlLoop(double deltaTime) {
        // Tasks we need to complete
        // move arm between intake position and output position
        // move claw between left, center, right positions
        float leftStickY = -gamepadEx1.leftStickY();
        float leftStickX = gamepadEx1.leftStickX();
        float rightStickX = gamepadEx1.rightStickX();
        drive.setMotorPowerFromControllerVector(leftStickX, leftStickY, rightStickX, 1);

    }
}
