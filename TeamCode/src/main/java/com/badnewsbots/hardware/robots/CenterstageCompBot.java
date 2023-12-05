package com.badnewsbots.hardware.robots;

import com.badnewsbots.hardware.drivetrains.Drive;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class CenterstageCompBot implements Robot {
    private MecanumDrive drive;
    public CenterstageCompBot(HardwareMap hardwareMap) {
        drive = new MecanumDrive(hardwareMap);
    }

    @Override
    public MecanumDrive getDrive() {return drive;}
}
