package com.badnewsbots.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.badnewsbots.PUDController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

// The Pud (pick up, drop)
@Config
public final class PUD {
    private final Telemetry telemetry;
    private final DcMotorEx armMotor;
    private final Servo grabbyGrabberServo;
    private final Servo grabbyYawServo;
    private final Servo grabbyPitchServo;
    private final LimitSwitchEx zeroLimitSwitch;
    //private final LimitSwitchEx endLimitSwitch;

    public static int MAX_ROT_TICKS = 3000; // TODO: measure
    public static double MOTOR_TICKS_PER_DEGREE = 5281.1 / 360;
    public static double kP = 0.0012;
    public static double kI = 0;
    public static double kD = 0.004;//12.72V idle
    public static double f = 0.2;
    public static double multiMovementAdmissibleError = 100;
    public static PUDController pidController = new PUDController(kP, kI, kD);

    public static double grabbyYawLeftPos = 0.3;
    public static double grabbyYawCenterPos = 0.5;
    public static double grabbyYawRightPos = 0.7;

    public static double grabbyOpenPos = 0.66;
    public static double grabbyClosedPos1Px = 0.56;
    public static double grabbyClosedPos2Px = 0.53;

    public static double grabbyPickUpPos = 0.08;
    public static double grabbyDropPos = 0.6;

    public static int armDropPos = 2300;
    public static int armPrePickUpPos = 200;
    public static int armPickUpPos = 0;

    public static int zeroPosition = 0;
    public static int adjustedMaxPosition = MAX_ROT_TICKS;
    public static volatile int armTargetPositionTicksPlusZeroPos = 0;
    public static int armStartingAngleToZero = 45; // unit = ticks
    public static double powerCap = 1.0;
    private int armCurrentPositionTicks = 0;
    private int armCurrentPositionTicksPlusZeroPos = 0;
    private boolean armPowerDisabled = true;
    private double pid;
    private double ff;
    private double power;

    public enum GrabbyYaw {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum GrabbyPitch {
        PICK_UP,
        DROP
    }

    public enum GrabbyStatus {
        OPEN,
        CLOSED1PX,
        CLOSED2PX
    }

    public enum ArmAngle {
        PICK_UP,
        DROP,
    }

    private double grabbyTargetPitch = grabbyPickUpPos;
    private double grabbyTargetYaw = grabbyYawCenterPos;
    private double grabbyTargetStatus = grabbyOpenPos;
    //private ArmStatus armStatus = ArmStatus.PICKUP;

    public PUD(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        armMotor = hardwareMap.get(DcMotorEx.class, "arm_motor");
        final TouchSensor zeroTouchSensor = hardwareMap.get(TouchSensor.class, "zero_limit_switch");
        //final TouchSensor endTouchSensor = hardwareMap.get(TouchSensor.class, "end_limit_switch");
        zeroLimitSwitch = new LimitSwitchEx(zeroTouchSensor);
        //endLimitSwitch = new LimitSwitchEx(endTouchSensor);

        grabbyGrabberServo = hardwareMap.get(Servo.class, "grabby_grabber_servo");
        grabbyPitchServo = hardwareMap.get(Servo.class, "grabby_pitch_servo");
        grabbyYawServo = hardwareMap.get(Servo.class, "grabby_yaw_servo");
    }

    public void init() {
        grabbyGrabberServo.setPosition(grabbyTargetStatus);
        grabbyYawServo.setPosition(grabbyTargetYaw);
        grabbyPitchServo.setPosition(grabbyTargetPitch);
    }

    public void update(double deltaTime) {
        zeroLimitSwitch.update();
        //endLimitSwitch.update();

        grabbyGrabberServo.setPosition(grabbyTargetStatus);
        grabbyYawServo.setPosition(grabbyTargetYaw);
        grabbyPitchServo.setPosition(grabbyTargetPitch);

        armCurrentPositionTicks = armMotor.getCurrentPosition();
        armCurrentPositionTicksPlusZeroPos = armCurrentPositionTicks + zeroPosition;

        if (zeroLimitSwitch.isDown() && armTargetPositionTicksPlusZeroPos == 0) {
            armPowerDisabled = true;
            zeroArmFromMinAngle();
        } //else if (endLimitSwitch.isDown() && armTargetPositionTicks >= MAX_ROT_TICKS) {
            //armPowerDisabled = true;
            //zeroArmFromMaxAngle();
        //}
        else {
            armPowerDisabled = false;
        }

        if (armPowerDisabled) {
            armMotor.setPower(0);
        } else {
            pidController.setPIDCoefficents(kP, kI, kD);
            pid = pidController.calculate(armTargetPositionTicksPlusZeroPos - armCurrentPositionTicksPlusZeroPos, deltaTime);
            ff = Math.cos(Math.toRadians((armTargetPositionTicksPlusZeroPos - zeroPosition + armStartingAngleToZero) / MOTOR_TICKS_PER_DEGREE)) * f;
            power = pid + ff;
            armMotor.setPower(Math.min(power, powerCap));
        }

        telemetry.addData("ff", ff); // to see if feedforward peaks in appropriate place
        telemetry.addData("power", power);
        telemetry.addData("target angle deg", (armTargetPositionTicksPlusZeroPos - armStartingAngleToZero) / MOTOR_TICKS_PER_DEGREE);
        telemetry.addData("target position", armTargetPositionTicksPlusZeroPos);
        telemetry.addData("current position", armCurrentPositionTicks); // remove after experiment)
        telemetry.addData("adjusted current position", armCurrentPositionTicksPlusZeroPos);
        telemetry.addData("zero limit switch is down", zeroLimitSwitch.isDown());
        //telemetry.addData("end limit switch is down", endLimitSwitch.isDown());
        telemetry.addData("zeroPos", zeroPosition);
        telemetry.addData("maxPos", adjustedMaxPosition);
    }

    public void zeroArmFromMinAngle() {
        zeroPosition = armCurrentPositionTicks;
        adjustedMaxPosition = MAX_ROT_TICKS + zeroPosition;
    }

    public void zeroArmFromMaxAngle() {
        adjustedMaxPosition = armCurrentPositionTicks;
        zeroPosition = adjustedMaxPosition - MAX_ROT_TICKS;
    }

    public void moveArmToAngleTicksAsync(int angleTicks) {
        armTargetPositionTicksPlusZeroPos = angleTicks + zeroPosition;
    }

    public void moveArmToKnownAngle(ArmAngle targetAngle) {
        if (targetAngle == ArmAngle.DROP) {moveArmToAngleTicksAsync(armDropPos);}
        else if (targetAngle == ArmAngle.PICK_UP) {
            Thread thread = new Thread(() -> {
                moveArmToAngleTicksAsync(armPrePickUpPos); //set target pos to 200
                while (Math.abs(armTargetPositionTicksPlusZeroPos - armCurrentPositionTicksPlusZeroPos) > multiMovementAdmissibleError) {
                    Thread.yield();// while not within error margin wait
                }
                moveArmToAngleTicksAsync(0);
                // once there, async set target pos 0
            });
            thread.start();
        }
    }

    public void setGrabbyTargetPitch(GrabbyPitch targetPitch) {
        if (targetPitch == GrabbyPitch.PICK_UP) {grabbyTargetPitch = grabbyPickUpPos;}
        else {grabbyTargetPitch = grabbyDropPos;}
    }

    public void setGrabbyTargetYaw(GrabbyYaw targetYaw) {
        if (targetYaw == GrabbyYaw.LEFT) {grabbyTargetYaw = grabbyYawLeftPos;}
        else if (targetYaw == GrabbyYaw.CENTER) {grabbyTargetYaw = grabbyYawCenterPos;}
        else grabbyTargetYaw = grabbyYawRightPos;
    }

    public void setGrabbyTargetStatus(GrabbyStatus targetStatus) {
        if (targetStatus == GrabbyStatus.OPEN) {grabbyTargetStatus = grabbyOpenPos;}
        else if (targetStatus == GrabbyStatus.CLOSED1PX) {grabbyTargetStatus = grabbyClosedPos1Px;}
        else grabbyTargetStatus = grabbyClosedPos2Px;
    }

    public Servo getGrabbyGrabberServo() {
        return grabbyGrabberServo;
    }

    public Servo getGrabbyYawServo() {
        return grabbyYawServo;
    }

}
