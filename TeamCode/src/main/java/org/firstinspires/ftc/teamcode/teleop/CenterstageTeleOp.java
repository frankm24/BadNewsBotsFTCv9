package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.hardware.DroneLauncher;
import com.badnewsbots.hardware.PUD;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.hardware.ds.GamepadEx;
import com.badnewsbots.hardware.robots.CenterstageCompBot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.HashMap;
import java.util.Hashtable;

@Config
@TeleOp
// never dies from static shocks
public final class CenterstageTeleOp extends LinearOpMode {
    private enum DriveMode {
        DRIVERS_IN_CONTROL,
        CPU_IN_CONTROL
    }
    private volatile DriveMode currentDriveMode = DriveMode.DRIVERS_IN_CONTROL;

    private Hashtable<Integer, CenterstageCompBot.Level> levelHashtable;

    public static double gamepad1SpeedMultiplier = 1.0;
    public static double gamepad2SpeedMultiplier = 0.4;

    private CenterstageCompBot robot;
    private GamepadEx gamepadEx1;
    private GamepadEx gamepadEx2;
    private MecanumDrive drive;
    private PUD pud;
    private DroneLauncher droneLauncher;

    private DcMotorEx armMotor;
    public static int X = 1;
    private int currentX = X;

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17);
        gamepadEx1 = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        robot = new CenterstageCompBot(hardwareMap, telemetry);
        drive = robot.getDrive();
        pud = robot.getPud();
        droneLauncher = robot.getDroneLauncher();
        armMotor = hardwareMap.get(DcMotorEx.class, "arm_motor");
        double prevTime = getRuntime();

        firstInit();
        while (!isStarted() && !isStopRequested()) {
            double currentTime = getRuntime();
            double deltaTime = currentTime - prevTime;
            gamepadEx1.update();
            gamepadEx2.update();
            initLoop(deltaTime);
            telemetry.update();
            prevTime = currentTime;
        }
        firstUpdate();
        while (opModeIsActive()) {
            double currentTime = getRuntime();
            double deltaTime = currentTime - prevTime;

            gamepadEx1.update();
            gamepadEx2.update();
            controlLoop(deltaTime);
            telemetry.addData("∆t", deltaTime);
            telemetry.update();
            prevTime = currentTime;
        }
    }

    private void firstInit() {
        pud.teleOpInit();
        levelHashtable = pud.getLevelHashtable();
        droneLauncher.init();
    }

    private void firstUpdate() {
        pud.firstUpdate();
    }

    private void initLoop(double deltaTime) {
        pud.initUpdate(deltaTime);
    }

    private void controlLoop(double deltaTime) {
        float gamepad1LeftStickY = -gamepadEx1.leftStickY();
        float gamepad1LeftStickX = gamepadEx1.leftStickX();
        float gamepad1RightStickX = gamepadEx1.rightStickX();
        float gamepad2LeftStickY = -gamepadEx2.leftStickY();
        float gamepad2LeftStickX = gamepadEx2.leftStickX();
        float gamepad2RightStickX = gamepadEx2.rightStickX();

        if (gamepadEx1.a() || gamepadEx2.dpadLeft()) {
            pud.setCurrentArmMode(PUD.ArmMode.MANUAL);
            pud.setManualMovePower(0.4);
        } else if (gamepadEx1.y() || gamepadEx2.dpadRight()) {
            pud.setCurrentArmMode(PUD.ArmMode.MANUAL);
            pud.setManualMovePower(-0.4);
        } else if (gamepadEx1.dpadUp()) {
            pud.setCurrentArmMode(PUD.ArmMode.MANUAL);
            pud.setManualMovePower(-1);
        } else {
            pud.setManualMovePower(0);
        }
        if (gamepadEx1.bPressed()) {
            if (X > 6) X = 6;
            if (X < 1) X = 1;
            CenterstageCompBot.Level level = levelHashtable.get(X);
            pud.moveArmToKnownAngle(PUD.ArmAngle.DROP_X);
            pud.moveArmToAngleTicksAsync(level.armAngle);
            pud.setGrabbyTargetPitch(level.grabbyPitch);
        }
        if (gamepadEx1.xPressed()) {
            pud.setGrabbyTargetYaw(PUD.GrabbyYaw.CENTER);
            pud.setGrabbyTargetPitch(PUD.GrabbyPitch.HOVER);
            pud.moveArmToKnownAngle(PUD.ArmAngle.HOVER);
        }
        if (gamepadEx2.backPressed()) {
            droneLauncher.launch();
        }
        if (gamepadEx1.rightBumperPressed() || gamepadEx2.rightBumperPressed()) {
            if (pud.getTargetKnownArmAngle() == PUD.ArmAngle.DROP_X) {
                Thread thread = new Thread(() -> {
                    pud.setGrabbyTargetGrip(PUD.GrabbyGrip.OPEN);
                    sleep(400);
                    currentDriveMode = DriveMode.CPU_IN_CONTROL;
                    drive.setMotorPowerFromGamepadVector(0, -0.4,0, 1);
                    sleep(300);
                    drive.setMotorPowerFromGamepadVector(0, 0, 0, 1);
                    currentDriveMode = DriveMode.DRIVERS_IN_CONTROL;
                    pud.moveArmToKnownAngle(PUD.ArmAngle.PICK_UP);
                    pud.setGrabbyTargetYaw(PUD.GrabbyYaw.CENTER);
                    pud.setGrabbyTargetPitch(PUD.GrabbyPitch.HOVER);
                    while (!pud.isLimitSwitchDown()) {
                        idle();
                    }
                    pud.moveArmToKnownAngle(PUD.ArmAngle.HOVER);
                });
                thread.start();
            } else {
                pud.setGrabbyTargetGrip(PUD.GrabbyGrip.OPEN);
            }
        }
        if ( (gamepadEx1.leftBumperPressed() || gamepadEx2.leftBumperPressed()) ) {
            Thread thread = new Thread(() -> {
                pud.setGrabbyTargetPitch(PUD.GrabbyPitch.HOVER);
                pud.moveArmToKnownAngle(PUD.ArmAngle.PICK_UP);
                sleep(600);
                pud.setGrabbyTargetGrip(PUD.GrabbyGrip.CLOSED2PX);
                sleep(200);
                pud.incrementGrabbyTargetPitch(-0.05);
                pud.moveArmToAngleTicksAsync(600);
            });
            thread.start();
        }
        if (gamepadEx2.bPressed() && pud.getTargetKnownArmAngle() == PUD.ArmAngle.DROP_X) {
            pud.setGrabbyTargetYaw(PUD.GrabbyYaw.LEFT);
            pud.incrementGrabbyTargetPitch(-0.05);
        }
        if (gamepadEx2.xPressed() && pud.getTargetKnownArmAngle() == PUD.ArmAngle.DROP_X) {
            pud.setGrabbyTargetYaw(PUD.GrabbyYaw.RIGHT);
            pud.incrementGrabbyTargetPitch(-0.05);
        }
        if (gamepadEx2.yPressed() && pud.getTargetKnownArmAngle() == PUD.ArmAngle.DROP_X) {
            CenterstageCompBot.Level level = levelHashtable.get(currentX);
            pud.setGrabbyTargetPitch(level.grabbyPitch);
            pud.setGrabbyTargetYaw(PUD.GrabbyYaw.CENTER);
        }
        if (gamepadEx2.dpadUpPressed()) {
            X++;
            if (pud.getTargetKnownArmAngle() == PUD.ArmAngle.DROP_X) {
                if (X > 6) X = 6;
                if (X < 1) X = 1;
                currentX = X;
                CenterstageCompBot.Level level = levelHashtable.get(X);
                pud.moveArmToKnownAngle(PUD.ArmAngle.DROP_X);
                pud.moveArmToAngleTicksAsync(level.armAngle);
                pud.setGrabbyTargetPitch(level.grabbyPitch);
            }
        }
        if (gamepadEx2.dpadDownPressed()) {
            X--;
            if (pud.getTargetKnownArmAngle() == PUD.ArmAngle.DROP_X) {
                if (X > 6) X = 6;
                if (X < 1) X = 1;
                currentX = X;
                CenterstageCompBot.Level level = levelHashtable.get(X);
                pud.moveArmToKnownAngle(PUD.ArmAngle.DROP_X);
                pud.moveArmToAngleTicksAsync(level.armAngle);
                pud.setGrabbyTargetPitch(level.grabbyPitch);
            }
        }
        if (gamepadEx1.startPressed()) {
            pud.moveArmToAngleTicksAsync(0);
        }
        // drive:
        if (currentDriveMode == DriveMode.DRIVERS_IN_CONTROL) {
            if (gamepad1LeftStickX == 0 && gamepad1LeftStickY == 0 && gamepad1RightStickX == 0) {
                drive.setMotorPowerFromGamepadVector(gamepad2LeftStickX, gamepad2LeftStickY, gamepad2RightStickX, gamepad2SpeedMultiplier);
            } else {
                drive.setMotorPowerFromGamepadVector(gamepad1LeftStickX, gamepad1LeftStickY, gamepad1RightStickX, gamepad1SpeedMultiplier);
            }
        }
        telemetry.addData("X", X);
        pud.update(deltaTime);
    }
}
