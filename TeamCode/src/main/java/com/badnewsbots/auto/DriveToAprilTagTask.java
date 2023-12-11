package com.badnewsbots.auto;

import com.badnewsbots.PIDController;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.perception.vision.CameraOrientation;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.MotionDetection;
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

    private final PIDController strafePID = new PIDController(0.025, 0.001, 0.005);
    private final PIDController speedPID = new PIDController(0.05, 0.0025, 0.01);
    private final PIDController turnPID = new PIDController(0.025, 0.001, 0.005);

    private final double STRAFE_GAIN =  0.01;
    private final double SPEED_GAIN =   0.02 ;   //  Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    private final double TURN_GAIN  =   0.01 ;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    private final double MAX_AUTO_STRAFE = 1;
    private final double MAX_AUTO_SPEED = 1;   //  Clip the approach speed to this max value (adjust for your robot)
    private final double MAX_AUTO_TURN  = 1;  //  Clip the turn speed to this max value (adjust for your robot)

    private AprilTagDetection tagDetection = null;
    private double rangeError;
    private double headingError;
    private double yawError;

    @Override
    public boolean isTaskCompleted() {
        if (tagDetection == null) return false;
        return (Math.abs(rangeError) <= rangeMarginOfError) && (Math.abs(headingError) <= headingMarginOfError) && (Math.abs(yawError)<= yawMarginOfError);
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
                leftX = Range.clip(strafePID.calculate(yawError, deltaTime), -MAX_AUTO_SPEED, MAX_AUTO_SPEED); // strafe left and right
                leftY = Range.clip(speedPID.calculate(rangeError, deltaTime) * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED); // forwards and backwards
                rightX = -Range.clip(strafePID.calculate(headingError, deltaTime) * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
            } else if (cameraOrientation == CameraOrientation.RIGHT) {
                leftX = Range.clip(speedPID.calculate(rangeError, deltaTime), -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                leftY = Range.clip(strafePID.calculate(-yawError, deltaTime), -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                rightX = -Range.clip(turnPID.calculate(headingError, deltaTime), -MAX_AUTO_TURN, MAX_AUTO_TURN);
            } else if (cameraOrientation == CameraOrientation.LEFT) {
                leftX = Range.clip(speedPID.calculate(-rangeError, deltaTime), -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                leftY = Range.clip(strafePID.calculate(yawError, deltaTime), -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                rightX = -Range.clip(turnPID.calculate(headingError, deltaTime), -MAX_AUTO_TURN, MAX_AUTO_TURN);
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
