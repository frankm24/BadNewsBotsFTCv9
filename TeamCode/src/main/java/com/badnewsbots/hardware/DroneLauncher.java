package com.badnewsbots.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class DroneLauncher {
    public static double readyPosition = 0.3;
    public static double shootPosition = 0;

    private final Servo droneServo;
    private Telemetry telemetry;
    public DroneLauncher(HardwareMap hardwareMap, Telemetry telemetry) {
        droneServo = hardwareMap.get(Servo.class, "drone_launcher_servo");
    }

    public void init() {
        droneServo.setPosition(readyPosition);
    }

    public void launch() {
        droneServo.setPosition(shootPosition);
    }
}
