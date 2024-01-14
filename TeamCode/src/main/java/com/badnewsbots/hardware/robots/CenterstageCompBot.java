package com.badnewsbots.hardware.robots;

import com.badnewsbots.hardware.PUD;
import com.badnewsbots.hardware.drivetrains.Drive;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class CenterstageCompBot implements Robot {
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
