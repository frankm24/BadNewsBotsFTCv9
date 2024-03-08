package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.hardware.ds.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Config
@TeleOp
public class MoveArmAndGrabbyManually extends LinearOpMode {
    public static double power = 0.5;
    public static double targetGrabbyPitch = 0;
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        GamepadEx gamepadEx1 = new GamepadEx(gamepad1);
        DcMotorEx armMotor = hardwareMap.get(DcMotorEx.class, "arm_motor");
        Servo grabbyGrabberServo = hardwareMap.get(Servo.class, "grabby_pitch_servo");
        armMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        int zeroPosition = armMotor.getCurrentPosition();
        waitForStart();
        while (opModeIsActive()) {
            gamepadEx1.update();
            if (gamepadEx1.dpadUp()) {
                armMotor.setPower(power);
            }
            else if (gamepadEx1.dpadDown()) {
                armMotor.setPower(-power);
            } else {
                armMotor.setPower(0);
            }
            if (gamepadEx1.y()) {
                targetGrabbyPitch = targetGrabbyPitch + 0.01;
            }
            if (gamepadEx1.a()) {
                targetGrabbyPitch = targetGrabbyPitch - 0.01;
            }
            grabbyGrabberServo.setPosition(targetGrabbyPitch);
            telemetry.addData("motor position", armMotor.getCurrentPosition() - zeroPosition);
            telemetry.addData("Target grabby pitch", targetGrabbyPitch);
            telemetry.addData("power", power);
            telemetry.addData("Power", armMotor.getPower());
            telemetry.addData("Current", armMotor.getCurrent(CurrentUnit.AMPS));
            telemetry.update();
        }
    }
}
