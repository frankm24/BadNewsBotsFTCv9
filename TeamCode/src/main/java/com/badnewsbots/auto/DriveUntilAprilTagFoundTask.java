package com.badnewsbots.auto;

import com.badnewsbots.hardware.drivetrains.MecanumDrive;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class DriveUntilAprilTagFoundTask implements AutonomousTask {
    private final AprilTagProcessor aprilTagProcessor;
    private final MecanumDrive drive;
    private final int targetTagId;
    private final double leftX, leftY, rightX, speedMultiplier;

    private AprilTagDetection tagDetection = null;

    @Override
    public boolean isTaskCompleted() {return tagDetection != null;}

    @Override
    public void init() {
        drive.setMotorPowerFromGamepadVector(leftX, leftY, rightX, speedMultiplier);
    }

    @Override
    public void updateTask(double deltaTime) {
        tagDetection = findTargetTagInFrame();
    }

    public DriveUntilAprilTagFoundTask(AprilTagProcessor aprilTagProcessor, MecanumDrive drive, int targetTagId, double leftX, double leftY, double rightX, double speedMultiplier) {
        this.aprilTagProcessor = aprilTagProcessor;
        this.drive = drive;
        this.targetTagId = targetTagId;
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.speedMultiplier = speedMultiplier;
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
