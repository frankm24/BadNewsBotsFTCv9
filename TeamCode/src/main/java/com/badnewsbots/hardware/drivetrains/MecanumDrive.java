package com.badnewsbots.hardware.drivetrains;

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
        // Enables automatic "bulk reads" from robot hardware, so multiple .get()'s on motors become one call
        // Should improve performance significantly, since hardwareMap read calls take 2ms each
        for (LynxModule module : hardwareMap.getAll(LynxModule.class))
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must match the names assigned during the robot configuration.
        // step (using the FTC Robot Controller app on the phone).
        backLeft = hardwareMap.get(DcMotorEx.class, "back_left");
        frontLeft = hardwareMap.get(DcMotorEx.class, "front_left");
        backRight = hardwareMap.get(DcMotorEx.class, "back_right");
        frontRight = hardwareMap.get(DcMotorEx.class, "front_right");

        // To drive forward, most robots need the motor on one side to be reversed because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Single Gear Reduction or 90 Deg drives may require direction flips
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
    }

    public void setMotorPowerFromControllerVector(double leftX, double leftY, double rightX, double speedMultiplier) {
        leftX *= speedMultiplier;
        leftY *= speedMultiplier;
        rightX *= speedMultiplier;
        double denominator = Math.max(Math.abs(leftY) + Math.abs(leftX) + Math.abs(rightX), 1);
        double front_leftPower = (leftY + leftX + rightX) / denominator;
        double back_leftPower = (leftY - leftX + rightX) / denominator;
        double front_rightPower = (leftY - leftX - rightX) / denominator;
        double back_rightPower = (leftY + leftX - rightX) / denominator;
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
