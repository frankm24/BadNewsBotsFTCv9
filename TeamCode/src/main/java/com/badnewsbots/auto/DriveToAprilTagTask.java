package com.badnewsbots.auto;

import com.badnewsbots.PIDController;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.hardware.robots.CenterstageCompBot;
import com.badnewsbots.perception.vision.CameraOrientation;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
// Autonomous task for driving towards a desired position relative to an AprilTag of a specific ID.
public final class DriveToAprilTagTask implements AutonomousTask {
    private final CameraOrientation cameraOrientation;
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

    private final PIDController strafePID;
    private final PIDController speedPID;
    private final PIDController turnPID;

    private AprilTagDetection tagDetection = null;
    private double rangeError;
    private double headingError;
    private double yawError;

    @Override
    public boolean isCompleted() {
        // if the tag is not visible, we cannot know if we are at the desired position or not, so return false (not completed).
        if (tagDetection == null) return false;
        // if we are within the margin of error, shut off all power to the motors and return true (completed) so the next task can be performed.
        if ((Math.abs(rangeError) <= rangeMarginOfError) && (Math.abs(headingError) <= headingMarginOfError) && (Math.abs(yawError)<= yawMarginOfError)) {
            drive.setMotorPowerFromGamepadVector(0,0,0,1);
            return true;
        }
        return false;
    }
    // No code needed when the task is initialized.
    @Override
    public void init() {

    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public void updateTask(double deltaTime) {
        tagDetection = findTargetTagInFrame();
        // If the desired tag is visible, we can update the drive motors to minimize our position error.
        if (tagDetection != null) {
            // A position relative to the AprilTag is defined by three numbers: range, heading, and yaw. For an explanation, read
            // the following document: https://ftc-docs.firstinspires.org/en/latest/apriltag/understanding_apriltag_detection_values/understanding-apriltag-detection-values.html
            // Note that range and heading could theoretically be swapped for cartesian field coordinates, but it requires different
            // PID control and movement code and we have never tested it.
            rangeError = tagDetection.ftcPose.range - targetRange;
            headingError = tagDetection.ftcPose.bearing - targetHeading;
            yawError = tagDetection.ftcPose.yaw - targetYaw;

            // Our code to drive on the AprilTag detection depends on the camera's frame of reference. If the camera is facing
            // towards the right or left (90 deg from the robot heading), then we have to rotate the movement to get the correct commands.
            double leftX = 0, leftY = 0, rightX = 0;
            if (cameraOrientation == CameraOrientation.FRONT) {
                leftX = Range.clip(strafePID.calculate(yawError, deltaTime), -CenterstageCompBot.maxAutoSpeed, CenterstageCompBot.maxAutoSpeed); // strafe left and right
                leftY = Range.clip(speedPID.calculate(rangeError, deltaTime), -CenterstageCompBot.maxAutoSpeed, CenterstageCompBot.maxAutoSpeed); // forwards and backwards
                rightX = -Range.clip(strafePID.calculate(headingError, deltaTime), -CenterstageCompBot.maxAutoTurn, CenterstageCompBot.maxAutoTurn);
            } else if (cameraOrientation == CameraOrientation.RIGHT) {
                leftX = Range.clip(speedPID.calculate(rangeError, deltaTime), -CenterstageCompBot.maxAutoSpeed, CenterstageCompBot.maxAutoSpeed);
                leftY = Range.clip(strafePID.calculate(-yawError, deltaTime), -CenterstageCompBot.maxAutoSpeed, CenterstageCompBot.maxAutoSpeed);
                rightX = -Range.clip(turnPID.calculate(headingError, deltaTime), -CenterstageCompBot.maxAutoTurn, CenterstageCompBot.maxAutoTurn);
            } else if (cameraOrientation == CameraOrientation.LEFT) {
                leftX = Range.clip(speedPID.calculate(-rangeError, deltaTime), -CenterstageCompBot.maxAutoSpeed, CenterstageCompBot.maxAutoSpeed);
                leftY = Range.clip(strafePID.calculate(yawError, deltaTime), -CenterstageCompBot.maxAutoSpeed, CenterstageCompBot.maxAutoSpeed);
                rightX = -Range.clip(turnPID.calculate(headingError, deltaTime), -CenterstageCompBot.maxAutoTurn, CenterstageCompBot.maxAutoTurn);
            }
            drive.setMotorPowerFromGamepadVector(leftX, leftY, rightX, 1);
            telemetry.addData("Range error", rangeError);
            telemetry.addData("Heading error", headingError);
            telemetry.addData("Yaw error", yawError);
            telemetry.addData("Left X", leftX);
            telemetry.addData("Left Y", leftY);
            telemetry.addData("Right X", rightX);
            telemetry.addData("Camera orientation", cameraOrientation);
        }
    }

    public DriveToAprilTagTask(CameraOrientation cameraOrientation, AprilTagProcessor aprilTagProcessor, MecanumDrive drive, Telemetry telemetry, int targetTagId,
                               double targetRange, double rangeMarginOfError, double targetBearing, double bearingMarginOfError,
                               double targetYaw, double yawMarginOfError) {
        this.cameraOrientation = cameraOrientation;
        this.aprilTagProcessor = aprilTagProcessor;
        this.drive = drive;
        this.telemetry = telemetry;
        this.targetTagId = targetTagId;
        this.targetRange = targetRange;
        this.rangeMarginOfError = rangeMarginOfError;
        this.targetHeading = targetBearing;
        this.headingMarginOfError = bearingMarginOfError;
        this.targetYaw = targetYaw;
        this.yawMarginOfError = yawMarginOfError;
        this.strafePID = new PIDController(CenterstageCompBot.kPStrafe, CenterstageCompBot.kIStrafe, CenterstageCompBot.kDStrafe);
        this.speedPID = new PIDController(CenterstageCompBot.kPSpeed, CenterstageCompBot.kISpeed, CenterstageCompBot.kDSpeed);
        this.turnPID = new PIDController(CenterstageCompBot.kPTurn, CenterstageCompBot.kITurn, CenterstageCompBot.kDTurn);
    }
    // Look for the AprilTag of the desired tag ID in list of currently detected AprilTags in the latest image.
    // processed by the AprilTagProcessor. If no tag of the right ID was found, returns null.
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
