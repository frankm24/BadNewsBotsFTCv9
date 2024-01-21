package com.badnewsbots.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.badnewsbots.PUDController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Hashtable;

// The Pud (pick up, drop)
@Config
public final class PUD {
    private final Telemetry telemetry;
    private final DcMotorEx armMotor;
    //private final TimeBasedServoController grabbyController;
    private final Servo grabbyServo;
    private final Servo turnServo;
    private final LimitSwitchEx zeroLimitSwitch;
    private final LimitSwitchEx endLimitSwitch;
    private final Hashtable<TurnPosition, Double> grabbyPositionsTable = new Hashtable<>();
    private final Hashtable<PudPosition, Integer> pudPositionsTable = new Hashtable<>();

    public static int MAX_ROT_TICKS = 454; // TODO: measure
    public static double MOTOR_TICKS_PER_DEGREE = 751.8 / 360;
    public static double kP = 0.03;
    public static double kI = 0;
    public static double kD = 0.0002;
    public static double kV = -0.0001;
    public static PUDController pidController = new PUDController(kP, kI, kD);
    public static double f = 0.45;

    public static int adjustedZeroPosition = 0;
    public static int adjustedMaxPosition = MAX_ROT_TICKS;
    public static int armTargetPositionTicks = 0;
    public static int armStartingAngleToZero = 45;
    public static double powerCap = 1.0;
    private int armCurrentPositionTicks = 0;
    private int armAdjustedCurrentPositionTicks = 0;
    private boolean armPowerDisabled = true;
    private double pid;
    private double ff;
    private double power;

    public enum TurnPosition {
        LEFT,
        CENTERED,
        RIGHT
    }

    public enum PudPosition {
        INTAKE,
        MOVING_TO_OUTPUT,
        OUTPUT,
        MOVING_TO_INTAKE
        // 310 lowest
        // 4th row: 250
    }

    private TurnPosition currentTurnPosition = TurnPosition.CENTERED;

    public PUD(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        armMotor = hardwareMap.get(DcMotorEx.class, "arm_motor");
        final TouchSensor zeroTouchSensor = hardwareMap.get(TouchSensor.class, "zero_limit_switch");
        final TouchSensor endTouchSensor = hardwareMap.get(TouchSensor.class, "end_limit_switch");
        zeroLimitSwitch = new LimitSwitchEx(zeroTouchSensor);
        endLimitSwitch = new LimitSwitchEx(endTouchSensor);

        grabbyServo = hardwareMap.get(Servo.class, "grabby_servo");
        turnServo = hardwareMap.get(Servo.class, "turn_servo");
        //grabbyController = new TimeBasedServoController(grabbyServo, 0.1, 300,0);
        grabbyPositionsTable.put(TurnPosition.LEFT, 0d);
        grabbyPositionsTable.put(TurnPosition.CENTERED, 150d);
        grabbyPositionsTable.put(TurnPosition.RIGHT, 300d);
        pudPositionsTable.put(PudPosition.INTAKE, 0);
        pudPositionsTable.put(PudPosition.OUTPUT, 2000);
    }
    public void init() {
        //grabbyController.init();
    }

    public TurnPosition getCurrentTurnPosition() {return currentTurnPosition;}
    /*
    public void setGrabbyPositionAsync(TurnPosition desiredPosition) {
        grabbyController.goToPositionDegreesAsync(grabbyPositionsTable.get(desiredPosition));
        final Thread thread = new Thread(() -> {
            while (grabbyController.getCurrentState() == TimeBasedServoController.ServoState.MOVING_TO_POSITION) {
                Thread.yield();
            }
            currentTurnPosition = desiredPosition;
        });
        thread.start();
    }

    public void setGrabbyPositionSync(TurnPosition desiredPosition) {
        grabbyController.goToPositionDegreesSync(grabbyPositionsTable.get(desiredPosition));
        currentTurnPosition = desiredPosition;
    }
    */
    public void update(double deltaTime) {
        zeroLimitSwitch.update();
        endLimitSwitch.update();
        armCurrentPositionTicks = armMotor.getCurrentPosition();
        armAdjustedCurrentPositionTicks = armCurrentPositionTicks - adjustedZeroPosition;

        if (zeroLimitSwitch.isDown() && armTargetPositionTicks == 0) {
            armPowerDisabled = true;
            zeroArmFromMinAngle();
        } else if (endLimitSwitch.isDown() && armTargetPositionTicks >= MAX_ROT_TICKS) {
            armPowerDisabled = true;
            zeroArmFromMaxAngle();
        } else {
            armPowerDisabled = false;
        }

        if (armPowerDisabled) {
            armMotor.setPower(0);
        } else {
            pidController.setPIDCoefficents(kP, kI, kD);
            pid = pidController.calculate(armTargetPositionTicks - armAdjustedCurrentPositionTicks, deltaTime);
            ff = Math.cos(Math.toRadians((armTargetPositionTicks + armStartingAngleToZero) / MOTOR_TICKS_PER_DEGREE)) * f;
            power = pid + ff;
            armMotor.setPower(Math.min(power, powerCap));
        }
        telemetry.addData("ff", ff); // to see if feedforward peaks in appropriate place
        telemetry.addData("power", power);
        telemetry.addData("target angle deg", (armTargetPositionTicks + armStartingAngleToZero) / MOTOR_TICKS_PER_DEGREE);
        telemetry.addData("target position", armTargetPositionTicks);
        telemetry.addData("current position", armCurrentPositionTicks); // remove after experiment)
        telemetry.addData("adjusted current position", armAdjustedCurrentPositionTicks);
        telemetry.addData("zero limit switch is down", zeroLimitSwitch.isDown());
        telemetry.addData("end limit switch is down", endLimitSwitch.isDown());
        telemetry.addData("adjZeroPos", adjustedZeroPosition);
        telemetry.addData("adjMaxPos", adjustedMaxPosition);
    }

    public void zeroArmFromMinAngle() {
        adjustedZeroPosition = armCurrentPositionTicks;
        adjustedMaxPosition = MAX_ROT_TICKS + adjustedZeroPosition;
    }

    public void zeroArmFromMaxAngle() {
        adjustedMaxPosition = armCurrentPositionTicks;
        adjustedZeroPosition = adjustedMaxPosition - MAX_ROT_TICKS;
    }

    public void moveArmToAngleTicksAsync(int angleTicks) {
        armTargetPositionTicks = angleTicks + adjustedZeroPosition;
    }

    public void moveArmToIntakeAngleAsync() {
    }

    public Servo getGrabbyServo() {
        return grabbyServo;
    }

    public Servo getTurnServo() {
        return turnServo;
    }

}
