package com.badnewsbots.hardware.robots;

import android.os.MessageQueue;

import com.acmerobotics.dashboard.config.Config;
import com.badnewsbots.hardware.DroneLauncher;
import com.badnewsbots.hardware.PUD;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import java.util.Hashtable;

@Config
public final class CenterstageCompBot implements Robot {
    public static final class Level {
        public int armAngle;
        public double grabbyPitch;
        Level(int armAngle, double grabbyPitch) {
            this.armAngle = armAngle;
            this.grabbyPitch = grabbyPitch;
        }
    }
    // Be sure to disable dashboard using the "Enable/Disable Dashboard" OpMode during a match
    public static double kPStrafe = 0.025;
    public static double kIStrafe = 0;
    public static double kDStrafe = 0;

    public static double kPSpeed = 0.1;
    public static double kISpeed = 0;
    public static double kDSpeed = 0;

    public static double kPTurn = 0.025;
    public static double kITurn = 0;
    public static double kDTurn = 0;

    public static double maxAutoSpeed = 0.5;
    public static double maxAutoTurn = 0.5;

    public static int exposureTimeMs = 3;
    public static int gain = 250;

    private MecanumDrive teleOpDrive;
    private MecanumDrive autoDrive;
    private PUD pud;
    private WebcamName frontCamera;
    private WebcamName backCamera;
    private DroneLauncher droneLauncher;
    private Hashtable<Integer, Level> levelHashtable = new Hashtable<>();
    public CenterstageCompBot(HardwareMap hardwareMap, Telemetry telemetry) {
        teleOpDrive = new MecanumDrive(hardwareMap, 1, -1, 1, 1, MecanumDrive.DriveMode.FORWARD);
        autoDrive = new MecanumDrive(hardwareMap, -1, -1, 1, -1, MecanumDrive.DriveMode.REVERSE);
        frontCamera = hardwareMap.get(WebcamName.class, "frontWebcam");
        backCamera = hardwareMap.get(WebcamName.class, "backCamera");
        droneLauncher = new DroneLauncher(hardwareMap, telemetry);

        levelHashtable.put(1, new CenterstageCompBot.Level(5797, 0.24));
        levelHashtable.put(2, new CenterstageCompBot.Level(5645, 0.24));
        levelHashtable.put(3, new CenterstageCompBot.Level(5421, 0.24));
        levelHashtable.put(4, new CenterstageCompBot.Level(5162, 0.17));
levelHashtable.put(5, new CenterstageCompBot.Level(4706, 0.10));
        levelHashtable.put(6, new CenterstageCompBot.Level(4528, 0));
        pud = new PUD(hardwareMap, telemetry, levelHashtable);
    }

    @Override
    public MecanumDrive getDrive() {return teleOpDrive;}
    public MecanumDrive getAutoDrive() {return autoDrive;}
    public PUD getPud() {return pud;}
    public WebcamName getFrontCamera() {return frontCamera;}
    public WebcamName getBackCamera() {return backCamera;}
    public DroneLauncher getDroneLauncher() {return droneLauncher;}
    public Hashtable<Integer, Level> getLevelHashtable() {return levelHashtable;}
}
