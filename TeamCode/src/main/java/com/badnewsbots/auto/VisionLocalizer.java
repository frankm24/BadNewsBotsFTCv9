package com.badnewsbots.auto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Pose2dKt;
import com.acmerobotics.roadrunner.localization.Localizer;
import com.badnewsbots.perception.vision.CameraOrientation;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public final class VisionLocalizer implements Localizer {
    private final CameraOrientation cameraOrientation;
    private final AprilTagProcessor aprilTagProcessor;
    private final Telemetry telemetry;

    private final int targetTagId;

    private Pose2d poseEstimate;
    private AprilTagDetection tagDetection = null;

    @Override
    public void update() {
        tagDetection = findTargetTagInFrame();
        if (tagDetection == null) {

        } else {
            //VectorF currentPosition = tagDetection.metadata.fieldPosition.added(new VectorF((float) tagDetection.ftcPose.x, (float) tagDetection.ftcPose.y));
            //double currentHeading =;
            //poseEstimate = new Pose2d(currentPosition.get(0), currentPosition.get(1), currentHeading);
        }
    }

    @NonNull
    @Override
    public Pose2d getPoseEstimate() {
        return null;
    }

    @Override
    public void setPoseEstimate(@NonNull Pose2d pose2d) {
        this.poseEstimate = pose2d;
    }

    @Nullable
    @Override
    public Pose2d getPoseVelocity() {
        return null;
    }

    public VisionLocalizer(CameraOrientation cameraOrientation, AprilTagProcessor aprilTagProcessor, Telemetry telemetry, int targetTagId) {
        this.cameraOrientation = cameraOrientation;
        this.aprilTagProcessor = aprilTagProcessor;
        this.telemetry = telemetry;
        this.targetTagId = targetTagId;
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
