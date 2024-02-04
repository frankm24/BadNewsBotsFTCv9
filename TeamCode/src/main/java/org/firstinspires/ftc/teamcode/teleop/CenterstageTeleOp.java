package org.firstinspires.ftc.teamcode.teleop;

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
    private CenterstageCompBot robot;
    private GamepadEx gamepadEx1;
    //private GamepadEx gamepadEx2;
    private MecanumDrive drive;
    private PUD pud;
    private Servo turnServo;
    private Servo grabbyServo;

    private DcMotorEx armMotor;

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17);
        gamepadEx1 = new GamepadEx(gamepad1);
       //gamepadEx2 = new GamepadEx(gamepad2);

        robot = new CenterstageCompBot(hardwareMap, telemetry);
        drive = robot.getDrive();
        pud = robot.getPud();
        armMotor = hardwareMap.get(DcMotorEx.class, "arm_motor");
        double prevTime = getRuntime();

        while (!isStarted() && !isStopRequested()) {
            double currentTime = getRuntime();
            double deltaTime = currentTime - prevTime;

            gamepadEx1.update();
            //gamepadEx2.update();
            init(deltaTime);
            initLoop(deltaTime);
            telemetry.update();
        }
        while (opModeIsActive()) {
            double currentTime = getRuntime();
            double deltaTime = currentTime - prevTime;

            gamepadEx1.update();
            //gamepadEx2.update();
            controlLoop(deltaTime);
            telemetry.update();
        }
    }


    private void init(double deltaTime) {
        pud.init();
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
        drive.setMotorPowerFromGamepadVector(leftStickX, leftStickY, rightStickX, 1);

        if (gamepadEx1.dpadLeftPressed()) {
            pud.setGrabbyTargetYaw(PUD.GrabbyYaw.LEFT);
        }
        if (gamepadEx1.dpadUpPressed()) {
            pud.setGrabbyTargetYaw(PUD.GrabbyYaw.CENTER);
        }
        if (gamepadEx1.dpadRightPressed()) {
            pud.setGrabbyTargetYaw(PUD.GrabbyYaw.RIGHT);
        }
        if (gamepadEx1.aPressed()) {
            pud.setGrabbyTargetStatus(PUD.GrabbyStatus.OPEN);
            pud.moveArmToAngleTicksAsync(100);
        }
        if (gamepadEx1.bPressed()) {
            pud.moveArmToAngleTicksAsync(0);
            pud.setGrabbyTargetStatus(PUD.GrabbyStatus.CLOSED1PX);
        }
        if (gamepadEx1.xPressed()) {
            pud.moveArmToAngleTicksAsync(150);
        }
        if (gamepadEx1.yPressed()) {
            pud.moveArmToAngleTicksAsync(0);
            pud.setGrabbyTargetStatus(PUD.GrabbyStatus.CLOSED2PX);
        }
        if (gamepadEx1.leftBumperPressed()) {
            pud.setGrabbyTargetPitch(PUD.GrabbyPitch.PICK_UP);
            pud.moveArmToKnownAngle(PUD.ArmAngle.PICK_UP);
        }
        if (gamepadEx1.rightBumperPressed()) {
            pud.setGrabbyTargetPitch(PUD.GrabbyPitch.DROP);
            pud.moveArmToKnownAngle(PUD.ArmAngle.DROP);
        }
        pud.update(deltaTime);
    }
}
