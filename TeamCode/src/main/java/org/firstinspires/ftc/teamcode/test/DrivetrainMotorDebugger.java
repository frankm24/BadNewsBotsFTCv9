package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.hardware.ds.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
/*
 * Note from Frank: I copied this idea (and ASCII art) from RoadRunner LOL. This version doesn't require RR
 * objects to work. It just requires that you use the same naming scheme that our team has used as long as I've
 * been coding.
 * This is a simple teleop routine for debugging your motor configuration.
 * Pressing each of the buttons will power its respective motor.
 *
 * Button Mappings:
 *
 * Xbox and Logitech/PS4 Button - Motor
 *   X / ▢         - Front Left
 *   Y / Δ         - Front Right
 *   B / O         - Rear  Right
 *   A / X         - Rear  Left
 *                                    The buttons are mapped to match the wheels spatially if you
 *                                    were to rotate the gamepad 45deg°. x/square is the front left
 *                    ________        and each button corresponds to the wheel as you go clockwise
 *                   / ______ \
 *     ------------.-'   _  '-..+              Front of Bot
 *              /   _  ( Y )  _  \                  ^
 *             |  ( X )  _  ( B ) |     Front Left   \    Front Right
 *        ___  '.      ( A )     /|       Wheel       \      Wheel
 *      .'    '.    '-._____.-'  .'       (x/▢)        \     (Y/Δ)
 *     |       |                 |                      \
 *      '.___.' '.               |          Back Left    \   Back Right
 *               '.             /             Wheel       \    Wheel
 *                \.          .'              (A/X)        \   (B/O)
 *                  \________/
 *
 *
 */
@TeleOp(group = "Test")
public final class DrivetrainMotorDebugger extends LinearOpMode {
    private FtcDashboard ftcDashboard;
    private GamepadEx gamepadEx1;
    private DcMotor front_left;
    private DcMotor back_left;
    private DcMotor front_right;
    private DcMotor back_right;

    @Override
    public void runOpMode() throws InterruptedException {
        gamepadEx1 = new GamepadEx(gamepad1);
        ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());

        back_left = hardwareMap.get(DcMotor.class, "back_left");
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        back_right = hardwareMap.get(DcMotor.class, "back_right");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        front_left.setDirection(DcMotor.Direction.REVERSE);
        back_left.setDirection(DcMotor.Direction.REVERSE);

        while (!isStarted() && !isStopRequested()) {
            telemetry.addData("Status: ", "Initialized");
            telemetry.update();
            idle();
        }
        mainLoop();
    }

    private void mainLoop() {
        double prevTime = 0;
        while (opModeIsActive()) {
            double currentTime = getRuntime();
            double deltaTime = currentTime - prevTime;
            gamepadEx1.update();

            if (gamepadEx1.a()) {
                back_left.setPower(1);
                telemetry.addLine("Testing back_left");
            } else back_left.setPower(0);
            if (gamepadEx1.b()) {
                back_right.setPower(1);
                telemetry.addLine("Testing back_right");
            } else back_right.setPower(0);
            if (gamepadEx1.x()) {
                front_left.setPower(1);
                telemetry.addLine("Testing front_left");
            } else front_left.setPower(0);
            if (gamepadEx1.y()) {
                front_right.setPower(1);
                telemetry.addLine("Testing front_right");
            } else front_right.setPower(0);

            telemetry.addData("Δt (ms)", deltaTime * 1E3);
            telemetry.update();
            prevTime = currentTime;
        }
    }
}
