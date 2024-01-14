package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.hardware.PUD;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.hardware.ds.GamepadEx;
import com.badnewsbots.hardware.robots.CenterstageCompBot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
@Config
@TeleOp
public final class CenterstageTeleOp extends LinearOpMode {
    public static double turnLeftPos = 0.3;
    public static double turnCenterPos = 0.5;
    public static double turnRightPos = 0.7;
    public static double grabbyOpenPos = 0.6;
    public static double grabbyClosedPos = 0.5;

    private CenterstageCompBot robot;
    private GamepadEx gamepadEx1;
    private MecanumDrive drive;
    private PUD pud;
    private Servo turnServo;
    private Servo grabbyServo;
    private enum GrabbyPosition {
        OPEN,
        CLOSED
    }
    private GrabbyPosition currentGrabbyPosition = GrabbyPosition.OPEN;

    private DcMotorEx armMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17);
        gamepadEx1 = new GamepadEx(gamepad1);

        robot = new CenterstageCompBot(hardwareMap, telemetry);
        drive = robot.getDrive();
        pud = robot.getPud();
        turnServo = robot.getPud().getTurnServo();
        grabbyServo = robot.getPud().getGrabbyServo();
        grabbyServo.setPosition(grabbyOpenPos);

        armMotor = hardwareMap.get(DcMotorEx.class, "arm_motor");
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
        pud.init();
    }

    private void controlLoop(double deltaTime) {
        // Tasks we need to complete
        // move arm between intake position and output position
        // move claw between left, center, right positions
        float leftStickY = -gamepadEx1.leftStickY();
        float leftStickX = gamepadEx1.leftStickX();
        float rightStickX = gamepadEx1.rightStickX();
        drive.setMotorPowerFromGamepadVector(leftStickX, leftStickY, rightStickX, 1);

        pud.update(deltaTime);
        if (gamepadEx1.xPressed()) {
            turnServo.setPosition(turnLeftPos);
        }
        if (gamepadEx1.yPressed()) {
            turnServo.setPosition(turnCenterPos);
        }
        if (gamepadEx1.bPressed()) {
            turnServo.setPosition(turnRightPos);
        }
        if (gamepadEx1.aPressed()) {
            if (currentGrabbyPosition == GrabbyPosition.CLOSED) {
                grabbyServo.setPosition(grabbyOpenPos);
                currentGrabbyPosition = GrabbyPosition.OPEN;
            } else {
                grabbyServo.setPosition(grabbyClosedPos);
                currentGrabbyPosition = GrabbyPosition.CLOSED;
            }
        }
    }
}
