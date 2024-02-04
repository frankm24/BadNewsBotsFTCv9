package com.badnewsbots.hardware.robots;

import com.acmerobotics.dashboard.config.Config;
import com.badnewsbots.hardware.PUD;
import com.badnewsbots.hardware.drivetrains.Drive;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public final class CenterstageCompBot implements Robot {
    // Be sure to disable dashboard using the "Enable/Disable Dashboard" OpMode during a match
    public static double kPStrafe = 0.025;
    public static double kIStrafe = 0.001;
    public static double kDStrafe = 0.005;

    public static double kPSpeed = 0.05;
    public static double kISpeed = 0.0025;
    public static double kDSpeed = 0.01;

    public static double kPTurn = 0.025;
    public static double kITurn = 0.001;
    public static double kDTurn = 0.005;

    public static double maxAutoSpeed = 0.5;
    public static double maxAutoTurn = 0.5;

    public static int exposureTimeMs = 3;
    public static int gain = 250;

    private MecanumDrive drive;
    private PUD pud;
    public CenterstageCompBot(HardwareMap hardwareMap, Telemetry telemetry) {
        drive = new MecanumDrive(hardwareMap);
        pud = new PUD(hardwareMap, telemetry);
    }

    @Override
    public MecanumDrive getDrive() {return drive;}
    public PUD getPud() {return pud;}
}
