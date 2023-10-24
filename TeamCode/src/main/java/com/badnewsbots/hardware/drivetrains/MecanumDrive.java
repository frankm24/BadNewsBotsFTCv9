package com.badnewsbots.hardware.drivetrains;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumDrive implements Drive {
    private DcMotorEx backLeft;
    private DcMotorEx frontLeft;
    private DcMotorEx backRight;
    private DcMotorEx frontRight;

    public MecanumDrive(HardwareMap hardwareMap) {
        // Enables automatic "bulk reads" from robot hardware, so multiple .get()'s on hardware
        // Should improve performance significantly, since hardwareMap read calls take 2ms each
        for (LynxModule module : hardwareMap.getAll( LynxModule.class ) )
            module.setBulkCachingMode( LynxModule.BulkCachingMode.AUTO );

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must match the names assigned during the robot configuration.
        // step (using the FTC Robot Controller app on the phone).
        backLeft = hardwareMap.get(DcMotorEx.class, "leftBack_drive");
        frontLeft = hardwareMap.get(DcMotorEx.class, "leftFront_drive");
        backRight = hardwareMap.get(DcMotorEx.class, "rightBack_drive");
        frontRight = hardwareMap.get(DcMotorEx.class, "rightFront_drive");

        // To drive forward, most robots need the motor on one side to be reversed because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Single Gear Reduction or 90 Deg drives may require direction flips
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void setMotorPowerFromControllerVector(double LeftX, double LeftY, double RightX, double speedMultiplier) {

        LeftX *= speedMultiplier; //LeftX is rotation, clockwise is + and counterclockwise is -
        LeftY *= speedMultiplier; //LeftY is forward/back, forward is + and back is -
        RightX *= speedMultiplier; //RightX is right/left, right is + and back is -
        double denominator = Math.max(Math.abs(LeftY) + Math.abs(LeftX) + Math.abs(RightX), 1);
        double front_leftPower = (LeftY - RightX - LeftX) / denominator;
        double back_leftPower = (LeftY + RightX - LeftX) / denominator;
        double front_rightPower = (LeftY + RightX + LeftX) / denominator;
        double back_rightPower = (LeftY - RightX + LeftX) / denominator;
        frontLeft.setPower(front_leftPower);
        backLeft.setPower(back_leftPower);
        frontRight.setPower(front_rightPower);
        backRight.setPower(back_rightPower);
    }
    public void setAllDriveMotorsPower(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }
}
