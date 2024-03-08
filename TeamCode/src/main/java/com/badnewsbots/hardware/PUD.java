package com.badnewsbots.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.badnewsbots.PIDController;
import com.badnewsbots.hardware.robots.CenterstageCompBot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.teleop.CenterstageTeleOp;

import java.util.Hashtable;

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

    private enum DriveMode {
        DRIVERS_IN_CONTROL,
        CPU_IN_CONTROL
    }

    private Hashtable<Integer, CenterstageCompBot.Level> levelHashtable;
    public static int MAX_ROT_TICKS = 8000; // TODO: measure
    public static double MOTOR_TICKS_PER_DEGREE = (384.5d / 360d) * (1d / 28d);
    public static double kP = 0.01;
    public static double kI = 0.0;
    public static double kD = 0.0;
    public static double f = 0.0;
    public static double multiMovementAdmissibleError = 100;
    public static PIDController pidController = new PIDController(kP, kI, kD);

    public static double grabbyYawLeftPos = 0.3;
    public static double grabbyYawCenterPos = 0.49;
    public static double grabbyYawRightPos = 0.7;

    public static double grabbyOpenClaw = 0.6;
    public static double grabbyClosedClaw1Px = 0.64;
    public static double grabbyClosedClaw2Px = 0.75;

    public static double grabbyPickUpPitch = .52;
    public static double grabbyHoverPitch = .48;
    public static double grabbyDropPitch = 0.22;
    public static double grabbyAutoInitPitch = 0;

    public static int armPreDropAngle = 4500;
    public static int armDropAngle = 5000;
    public static int armPickUpAngle = 0;
    public static int armHoverAngle = 400;

    public static int zeroPosition = 0;
    public static int adjustedMaxPosition = MAX_ROT_TICKS;
    public static int armTargetPositionTicks = 0;
    public static int armTargetPositionTicksAdjusted = 0;
    public static int armStartingAngleToZero = 45; // unit = ticks
    public static double manualMovePower = 1.0;

    private int armCurrentPositionTicks = 0;
    private boolean armPowerDisabled = true;
    private double pid;
    private double ff;
    private double power;
    private volatile ArmAngle targetKnownArmAngle = null;
    private volatile ArmMode currentArmMode = ArmMode.PID;
    private volatile double grabbyTargetPitch = grabbyHoverPitch;
    private volatile double grabbyTargetYaw = grabbyYawCenterPos;
    private volatile double grabbyTargetGrip = grabbyOpenClaw;

    public enum GrabbyYaw {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum GrabbyPitch {
        AUTO_INIT,
        HOVER,
        PICK_UP,
        DROP_X
    }

    public enum GrabbyGrip {
        OPEN,
        CLOSED1PX,
        CLOSED2PX
    }

    public enum ArmAngle {
        PICK_UP,
        HOVER,
        PRE_DROP,
        DROP_X
    }

    public enum ArmMode {
        PID,
        MANUAL
    }

    public PUD(HardwareMap hardwareMap, Telemetry telemetry, Hashtable<Integer, CenterstageCompBot.Level> levelHashtable) {
        this.telemetry = telemetry;
        this.levelHashtable = levelHashtable;
        armMotor = hardwareMap.get(DcMotorEx.class, "arm_motor");
        armMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        final TouchSensor zeroTouchSensor = hardwareMap.get(TouchSensor.class, "zero_limit_switch");
        //final TouchSensor endTouchSensor = hardwareMap.get(TouchSensor.class, "end_limit_switch");
        zeroLimitSwitch = new LimitSwitchEx(zeroTouchSensor);
        //endLimitSwitch = new LimitSwitchEx(endTouchSensor);

        grabbyGrabberServo = hardwareMap.get(Servo.class, "grabby_grabber_servo");
        grabbyPitchServo = hardwareMap.get(Servo.class, "grabby_pitch_servo");
        grabbyYawServo = hardwareMap.get(Servo.class, "grabby_yaw_servo");
    }

    public void autoInit() {
        setGrabbyTargetPitch(grabbyAutoInitPitch);
        setGrabbyTargetGrip(GrabbyGrip.CLOSED1PX);
        init();
    }

    public void teleOpInit() {
        setGrabbyTargetPitch(grabbyAutoInitPitch);
        setGrabbyTargetGrip(GrabbyGrip.OPEN);
        init();
    }

    public void init() {
        armCurrentPositionTicks = armMotor.getCurrentPosition();
        zeroLimitSwitch.update();
        grabbyGrabberServo.setPosition(grabbyTargetGrip);
        grabbyYawServo.setPosition(grabbyTargetYaw);
        grabbyPitchServo.setPosition(grabbyTargetPitch);
        addTelemetry();
    }

    public void initUpdate(double deltaTime) {
        armCurrentPositionTicks = armMotor.getCurrentPosition();
        zeroLimitSwitch.update();
        //endLimitSwitch.update();
        grabbyGrabberServo.setPosition(grabbyTargetGrip);
        grabbyYawServo.setPosition(grabbyTargetYaw);
        grabbyPitchServo.setPosition(grabbyTargetPitch);

        if (!zeroLimitSwitch.isDown()) {
            armMotor.setPower(-0.35);
            telemetry.addLine("Zeroing arm...");
        } else {
            armMotor.setPower(0);
            telemetry.addLine("Arm is ready");
        }
        addTelemetry();
    }

    public void firstUpdate() {
        armCurrentPositionTicks = armMotor.getCurrentPosition();
        zeroLimitSwitch.update();
        zeroArmFromMinAngle();

        targetKnownArmAngle = ArmAngle.HOVER;
        armTargetPositionTicks = armHoverAngle;
        armTargetPositionTicksAdjusted = armHoverAngle + zeroPosition;
        setGrabbyTargetPitch(GrabbyPitch.HOVER);
        addTelemetry();
    }

    public void update(double deltaTime) {
        zeroLimitSwitch.update();
        //endLimitSwitch.update();

        grabbyGrabberServo.setPosition(grabbyTargetGrip);
        grabbyYawServo.setPosition(grabbyTargetYaw);
        grabbyPitchServo.setPosition(grabbyTargetPitch);

        armCurrentPositionTicks = armMotor.getCurrentPosition();

        if (zeroLimitSwitch.isDown() && armTargetPositionTicks <= 0) {
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
            power = 0;
        } else if (currentArmMode == ArmMode.PID) {
            pidController.setPIDCoefficents(kP, kI, kD);
            pid = pidController.calculate(armTargetPositionTicksAdjusted - armCurrentPositionTicks, deltaTime);
            ff = Math.cos(Math.toRadians((armTargetPositionTicksAdjusted - zeroPosition + armStartingAngleToZero) / MOTOR_TICKS_PER_DEGREE)) * f;
            power = pid + ff;

        } else {
            power = manualMovePower;
        }
        armMotor.setPower(power);
        addTelemetry();
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
        armTargetPositionTicks = angleTicks;
        armTargetPositionTicksAdjusted = angleTicks + zeroPosition;
    }

    public void moveArmToKnownAngle(ArmAngle targetAngle) {
        setCurrentArmMode(ArmMode.PID);
        targetKnownArmAngle = targetAngle;
        if (targetAngle == ArmAngle.PRE_DROP) {moveArmToAngleTicksAsync(armPreDropAngle);}
        else if (targetAngle == ArmAngle.PICK_UP) {
            moveArmToAngleTicksAsync(armPickUpAngle);
        } else if (targetAngle == ArmAngle.DROP_X) {
            //moveArmToAngleTicksAsync(armDropAngle);
        } else {
            moveArmToAngleTicksAsync(armHoverAngle);
        }
    }

    public void setGrabbyTargetPitch(GrabbyPitch targetPitch) {
        if (targetPitch == GrabbyPitch.HOVER) {
            grabbyTargetPitch = grabbyHoverPitch;
        }
        else if (targetPitch == GrabbyPitch.PICK_UP) {
            grabbyTargetPitch = grabbyPickUpPitch;
        }
        else {
            grabbyTargetPitch = grabbyDropPitch;
        }
    }

    public void incrementGrabbyTargetPitch(double increment) {
        double save = grabbyTargetPitch;
        grabbyTargetPitch = save + increment;
    }

    public void setGrabbyTargetPitch(double targetPitch) {
        grabbyTargetPitch = targetPitch;
    }

    public void setGrabbyTargetYaw(GrabbyYaw targetYaw) {
        if (targetYaw == GrabbyYaw.LEFT) {
            grabbyTargetYaw = grabbyYawLeftPos;
        }
        else if (targetYaw == GrabbyYaw.CENTER) {
            grabbyTargetYaw = grabbyYawCenterPos;
        }
        else {
            grabbyTargetYaw = grabbyYawRightPos;
        }
    }

    public void setGrabbyTargetGrip(GrabbyGrip targetGrip) {
        if (targetGrip == GrabbyGrip.OPEN) {
            grabbyTargetGrip = grabbyOpenClaw;
        }
        else if (targetGrip == GrabbyGrip.CLOSED1PX) {
            grabbyTargetGrip = grabbyClosedClaw1Px;
        }
        else {
            grabbyTargetGrip = grabbyClosedClaw2Px;
        }
    }

    public void toggleGrabbyPitchBtHoverPickUp() {
        if (grabbyTargetPitch == grabbyPickUpPitch) {
            grabbyTargetPitch = grabbyHoverPitch;
        } else {
            grabbyTargetPitch = grabbyPickUpPitch;
        }
    }

    public Servo getGrabbyGrabberServo() {
        return grabbyGrabberServo;
    }

    public Servo getGrabbyYawServo() {
        return grabbyYawServo;
    }

    public ArmAngle getTargetKnownArmAngle() {return targetKnownArmAngle;}

    public void setManualMovePower(double direction) {
        manualMovePower = direction;
    }

    public void setCurrentArmMode(ArmMode armMode) {
        currentArmMode = armMode;
    }

    public boolean isLimitSwitchDown() {
        return zeroLimitSwitch.isDown();
    }

    public Hashtable<Integer, CenterstageCompBot.Level> getLevelHashtable() {return levelHashtable;}

    private void addTelemetry() {
        telemetry.addData("Grabby target pitch", grabbyTargetPitch);
        telemetry.addData("power", power);
        telemetry.addData("Power", armMotor.getPower());
        telemetry.addData("Current", armMotor.getCurrent(CurrentUnit.AMPS));
        //telemetry.addData("target angle deg", (armTargetPositionTicksAdjusted - armStartingAngleToZero) / MOTOR_TICKS_PER_DEGREE);
        telemetry.addData("adjusted target position", armTargetPositionTicksAdjusted);
        telemetry.addData("target position", armTargetPositionTicks);
        telemetry.addData("current position", armCurrentPositionTicks);
        telemetry.addData("power disabled?", armPowerDisabled);
        telemetry.addData("zero limit switch is down", zeroLimitSwitch.isDown());
        //telemetry.addData("end limit switch is down", endLimitSwitch.isDown());
        telemetry.addData("zeroPos", zeroPosition);
    }
}
/*
Psuedo code:

target arm PID to pre-pick up position
 */