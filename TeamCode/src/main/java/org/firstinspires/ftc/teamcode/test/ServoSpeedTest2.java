package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(group="Test")
public final class ServoSpeedTest2 extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Servo myServo = hardwareMap.get(Servo.class, "servo");
        waitForStart();
        myServo.setPosition(1);
        sleep(9999999);
    }
}
