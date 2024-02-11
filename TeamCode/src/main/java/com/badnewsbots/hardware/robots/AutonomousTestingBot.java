package com.badnewsbots.hardware.robots;

import com.badnewsbots.hardware.drivetrains.Drive;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.perception.FloorClassifier;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class AutonomousTestingBot implements Robot {
    private final MecanumDrive drive;
    private final WebcamName frontCamera;
    private final WebcamName leftCamera;
    private final ModernRoboticsI2cColorSensor colorSensor;

    public AutonomousTestingBot(HardwareMap hardwareMap) {
        drive = new MecanumDrive(hardwareMap, -1, -1, 1, 1);
        frontCamera = hardwareMap.get(WebcamName.class, "frontWebcam");
        leftCamera = hardwareMap.get(WebcamName.class, "leftWebcam");
        colorSensor = hardwareMap.get(ModernRoboticsI2cColorSensor.class, "sensor_color");
    }

    public WebcamName getFrontCamera() {return frontCamera;}
    public WebcamName getLeftCamera() {return leftCamera;}

    @Override
    public MecanumDrive getDrive() {return drive;}

    public ModernRoboticsI2cColorSensor getColorSensor() {return colorSensor;}
}
