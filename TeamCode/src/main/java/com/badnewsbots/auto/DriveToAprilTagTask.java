package com.badnewsbots.auto;

import com.badnewsbots.PIDController;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.perception.vision.CameraOrientation;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

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

    private final PIDController strafePID = new PIDController(DashboardViewableConstants.kPStrafe, DashboardViewableConstants.kIStrafe, DashboardViewableConstants.kDStrafe);
    private final PIDController speedPID = new PIDController(DashboardViewableConstants.kPSpeed, DashboardViewableConstants.kISpeed, DashboardViewableConstants.kDSpeed);
    private final PIDController turnPID = new PIDController(DashboardViewableConstants.kPTurn, DashboardViewableConstants.kITurn, DashboardViewableConstants.kDTurn);

    private AprilTagDetection tagDetection = null;
    private double rangeError;
    private double headingError;
    private double yawError;

    @Override
    public boolean isTaskCompleted() {
        if (tagDetection == null) return false;
        if ((Math.abs(rangeError) <= rangeMarginOfError) && (Math.abs(headingError) <= headingMarginOfError) && (Math.abs(yawError)<= yawMarginOfError)) {
            drive.setMotorPowerFromControllerVector(0,0,0,1);
            return true;
        }
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void updateTask(double deltaTime) {
        tagDetection = findTargetTagInFrame();
        if (tagDetection != null) {
            rangeError = tagDetection.ftcPose.range - targetRange;
            headingError = tagDetection.ftcPose.bearing - targetHeading;
            yawError = tagDetection.ftcPose.yaw - targetYaw;

            double leftX = 0, leftY = 0, rightX = 0;
            if (cameraOrientation == CameraOrientation.FRONT) {
                leftX = Range.clip(strafePID.calculate(yawError, deltaTime), -DashboardViewableConstants.maxAutoSpeed, DashboardViewableConstants.maxAutoSpeed); // strafe left and right
                leftY = Range.clip(speedPID.calculate(rangeError, deltaTime), -DashboardViewableConstants.maxAutoSpeed, DashboardViewableConstants.maxAutoSpeed); // forwards and backwards
                rightX = -Range.clip(strafePID.calculate(headingError, deltaTime), -DashboardViewableConstants.maxAutoTurn, DashboardViewableConstants.maxAutoTurn);
            } else if (cameraOrientation == CameraOrientation.RIGHT) {
                leftX = Range.clip(speedPID.calculate(rangeError, deltaTime), -DashboardViewableConstants.maxAutoSpeed, DashboardViewableConstants.maxAutoSpeed);
                leftY = Range.clip(strafePID.calculate(-yawError, deltaTime), -DashboardViewableConstants.maxAutoSpeed, DashboardViewableConstants.maxAutoSpeed);
                rightX = -Range.clip(turnPID.calculate(headingError, deltaTime), -DashboardViewableConstants.maxAutoTurn, DashboardViewableConstants.maxAutoTurn);
            } else if (cameraOrientation == CameraOrientation.LEFT) {
                leftX = Range.clip(speedPID.calculate(-rangeError, deltaTime), -DashboardViewableConstants.maxAutoSpeed, DashboardViewableConstants.maxAutoSpeed);
                leftY = Range.clip(strafePID.calculate(yawError, deltaTime), -DashboardViewableConstants.maxAutoSpeed, DashboardViewableConstants.maxAutoSpeed);
                rightX = -Range.clip(turnPID.calculate(headingError, deltaTime), -DashboardViewableConstants.maxAutoTurn, DashboardViewableConstants.maxAutoTurn);
            }
            drive.setMotorPowerFromControllerVector(leftX, leftY, rightX, 1);
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
