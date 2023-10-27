package com.badnewsbots.hardware.robots;

import com.badnewsbots.hardware.drivetrains.Drive;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.perception.FloorClassifier;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class AutonomousTestingBot implements Robot {
    private MecanumDrive drive;
    private WebcamName frontCamera;
    private ModernRoboticsI2cColorSensor colorSensor;

    public AutonomousTestingBot(HardwareMap hardwareMap) {
        drive = new MecanumDrive(hardwareMap);
        frontCamera = hardwareMap.get(WebcamName.class, "Webcam 1");
        colorSensor = hardwareMap.get(ModernRoboticsI2cColorSensor.class, "sensor_color");
    }

    public WebcamName getFrontCamera() {return frontCamera;}

    @Override
    public MecanumDrive getDrive() {return drive;}

    public ModernRoboticsI2cColorSensor getColorSensor() {return colorSensor;}
}
