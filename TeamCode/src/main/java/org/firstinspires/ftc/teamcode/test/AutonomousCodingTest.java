package org.firstinspires.ftc.teamcode.test;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.auto.AutonomousTask;
import com.badnewsbots.auto.AutonomousTaskSequenceRunner;
import com.badnewsbots.auto.DriveToAprilTagTask;
import com.badnewsbots.auto.WaitSecondsTask;
import com.badnewsbots.hardware.drivetrains.Drive;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.hardware.robots.AutonomousTestingBot;
import com.badnewsbots.perception.vision.CameraOrientation;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Autonomous(group="Test")
public final class AutonomousCodingTest extends LinearOpMode {

    private final List<AutonomousTask> taskList = new ArrayList<>();
    VisionPortal visionPortal;
    FtcDashboard ftcDashboard;

    @Override
    public void runOpMode() throws InterruptedException {
        ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17); // smooooooth data

        AutonomousTestingBot robot = new AutonomousTestingBot(hardwareMap);
        MecanumDrive drive = robot.getDrive();
        AutonomousTaskSequenceRunner autonomousTaskSequenceRunner = new AutonomousTaskSequenceRunner(this);
        // Needed for opMode context to call opModeIsActive() in loop

        AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder().build();
        aprilTagProcessor.setDecimation(2);
        visionPortal = new VisionPortal.Builder()
                .setCamera(robot.getFrontCamera())
                .addProcessor(aprilTagProcessor)
                .setCameraResolution(new Size(640, 480)) // Default resolution but just being explicit
                .enableLiveView(true) // Live view = on Robot Controller via HDMI, Camera Stream = DS
                .setAutoStopLiveView(false)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .build();

        setManualExposure(6, 250);

        taskList.add(new WaitSecondsTask(10));
        taskList.add(new DriveToAprilTagTask(CameraOrientation.FRONT, aprilTagProcessor, drive, telemetry, 583, 12, 0.5,
                        2, 2, 0, 0));
        waitForStart();
        autonomousTaskSequenceRunner.runTasks(taskList);
    }
    /*
     Manually set the camera gain and exposure.
     This can only be called AFTER calling initAprilTag(), and only works for Webcams;
    */
    private void setManualExposure(int exposureMS, int gain) {
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
