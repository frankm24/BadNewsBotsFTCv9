package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.auto.AutonomousTask;
import com.badnewsbots.auto.AutonomousTaskSequenceRunner;
import com.badnewsbots.auto.DriveToAprilTagTask;
import com.badnewsbots.auto.StopVisionPortalStreaming;
import com.badnewsbots.hardware.drivetrains.Drive;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.hardware.robots.AutonomousTestingBot;
import com.badnewsbots.perception.vision.CameraOrientation;
import com.badnewsbots.perception.vision.processors.TeamPropProcessor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Autonomous
public final class RedAuto2 extends LinearOpMode {

    private final List<AutonomousTask> taskList = new ArrayList<>();
    private VisionPortal frontVisionPortal;
    private VisionPortal leftVisionPortal;
    private int[] multiPortalViewIds;

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17); // smooooooth data

        AutonomousTestingBot robot = new AutonomousTestingBot(hardwareMap);
        MecanumDrive drive = robot.getDrive();
        AutonomousTaskSequenceRunner autonomousTaskSequenceRunner = new AutonomousTaskSequenceRunner(this);
        // Needed for opMode context to call opModeIsActive() in loop

        multiPortalViewIds = VisionPortal.makeMultiPortalView(2, VisionPortal.MultiPortalLayout.HORIZONTAL);

        TeamPropProcessor teamPropProcessor = new TeamPropProcessor(640, 480, TeamPropProcessor.Alliance.RED);
        AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder().build();
        aprilTagProcessor.setDecimation(2);

        frontVisionPortal = new VisionPortal.Builder()
                .setCamera(robot.getFrontCamera())
                .addProcessor(teamPropProcessor)
                .setCameraResolution(new Size(640, 480)) // Default resolution but just being explicit
                .enableLiveView(true) // Live view = on Robot Controller via HDMI, Camera Stream = DS
                .setAutoStopLiveView(true)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .setLiveViewContainerId(multiPortalViewIds[0])
                .build();
        leftVisionPortal = new VisionPortal.Builder()
                .setCamera(robot.getLeftCamera())
                .addProcessor(aprilTagProcessor)
                .setCameraResolution(new Size(640, 480)) // Default resolution but just being explicit
                .enableLiveView(true) // Live view = on Robot Controller via HDMI, Camera Stream = DS
                .setAutoStopLiveView(true)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .setLiveViewContainerId(multiPortalViewIds[1])
                .build();

        setManualExposure(leftVisionPortal,6, 250);
        leftVisionPortal.stopStreaming();

        TeamPropProcessor.TeamPropLocation location = TeamPropProcessor.TeamPropLocation.NONE;
        taskList.add(new StopVisionPortalStreaming(frontVisionPortal));

        while (!isStarted() && !isStopRequested()) {
            location = teamPropProcessor.getTeamPropLocation();
            telemetry.addData("Status", "Initialized");
            telemetry.addData("Front Camera State", frontVisionPortal.getCameraState());
            telemetry.addData("Left Camera State", leftVisionPortal.getCameraState());
            telemetry.addData("Team Prop Location", location);
            telemetry.update();
        }

        frontVisionPortal.stopStreaming();
        leftVisionPortal.resumeStreaming();

        location = TeamPropProcessor.TeamPropLocation.LEFT;

        switch (location) {
            case LEFT:
                taskList.add(new DriveToAprilTagTask(CameraOrientation.RIGHT, aprilTagProcessor, drive, telemetry, 6, 35.7, 0.1, -5.86, 0.05, 0, 0.1));
                break;
            case CENTER:
                break;
            case RIGHT:
                break;
        }
        autonomousTaskSequenceRunner.runTasks(taskList);
    }
    /*
     Manually set the camera gain and exposure.
     This can only be called AFTER calling initAprilTag(), and only works for Webcams;
    */
    private void setManualExposure(VisionPortal visionPortal, int exposureMS, int gain) {
        // Wait for the camera to be open, then use the controls
        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            telemetry.update();
            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                sleep(20);
            }
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }

        // Set camera controls unless we are stopping.
        if (!isStopRequested())
        {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                sleep(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            sleep(20);
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }
    }
}
