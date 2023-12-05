package com.badnewsbots.auto;

import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.MotionDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public final class DriveToAprilTagTask implements AutonomousTask {
    private final AprilTagProcessor aprilTagProcessor;
    private final MecanumDrive drive;
    private final Telemetry telemetry;

    private final int targetTagId;

    private final double targetRange;
    private final double rangeMarginOfError;

    private final double targetHeading;
    private final double headingMarginOfError;

    private final double targetYaw;
    private final double yawMarginOfError;

    private final double STRAFE_GAIN =  0.01 ;
    private final double SPEED_GAIN =   0.02 ;   //  Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    private final double TURN_GAIN  =   0.01 ;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    private final double MAX_AUTO_STRAFE = 0.5;
    private final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    private final double MAX_AUTO_TURN  = 0.25;  //  Clip the turn speed to this max value (adjust for your robot)

    private AprilTagDetection tagDetection = null;
    private double rangeError;
    private double headingError;
    private double yawError;

    @Override
    public boolean isTaskCompleted() {
        if (tagDetection == null) return false;
        return (rangeError <= rangeMarginOfError) && (headingError <= headingMarginOfError) && (yawError <= yawMarginOfError);
    }

    @Override
    public void init() {

    }

    @Override
    public void updateTask() {
        tagDetection = findTargetTagInFrame();
        if (tagDetection != null) {
            rangeError = tagDetection.ftcPose.range - targetRange;
            headingError = tagDetection.ftcPose.bearing - targetHeading;
            yawError = tagDetection.ftcPose.yaw - targetYaw;

            double leftX = Range.clip(yawError * STRAFE_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED) / 2;
            double leftY = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED) / 2;
            double rightX = -Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
            drive.setMotorPowerFromControllerVector(leftX, leftY, rightX, 1);

            telemetry.addData("Range error", rangeError);
            telemetry.addData("Heading error", headingError);
            telemetry.addData("Yaw error", yawError);
            telemetry.addData("Left X", leftX);
            telemetry.addData("Left Y", leftY);
            telemetry.addData("Right X", rightX);
        }
    }

    public DriveToAprilTagTask(AprilTagProcessor aprilTagProcessor, MecanumDrive drive, Telemetry telemetry, int targetTagId,
                               double targetRange, double rangeMarginOfError, double targetHeading, double headingMarginOfError,
                               double targetYaw, double yawMarginOfError) {
        this.aprilTagProcessor = aprilTagProcessor;
        this.drive = drive;
        this.telemetry = telemetry;
        this.targetTagId = targetTagId;
        this.targetRange = targetRange;
        this.rangeMarginOfError = rangeMarginOfError;
        this.targetHeading = targetHeading;
        this.headingMarginOfError = headingMarginOfError;
        this.targetYaw = targetYaw;
        this.yawMarginOfError = yawMarginOfError;
    }

    private AprilTagDetection findTargetTagInFrame() {
        // Step through the list of detected tags and look for a matching tag
        List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                //  Check to see if we want to track towards this tag.
                if (detection.id == targetTagId) {
                    // Yes, we want to use this tag.
                    return detection; // don't look any further
                } else {
                    // This tag is in the library, but we do not want to track it right now.
                }
            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
            }
        }
        return null;
    }
}
