package com.badnewsbots.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

// The Pud (pick up, drop)
public final class Pud {
    private final DcMotorEx pudMotor;
    private final int intakeTicks = 100;
    private final int outputTicks = 500;

    public enum GrabbyPosition {
        LEFT,
        CENTERED,
        RIGHT
    }

    public enum PudPosition {
        INTAKE,
        OUTPUT
    }

    public Pud(HardwareMap hardwareMap) {
        pudMotor = hardwareMap.get(DcMotorEx.class, "pud_motor");
        pudMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void movePudToOutputPosition() {

    }

    public void movePudToIntakePosition() {

    }

   // public void rotateGrabbyToPosition(GrabbyPosition )
}
