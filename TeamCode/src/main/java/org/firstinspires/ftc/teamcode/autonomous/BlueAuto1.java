package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.auto.AutonomousTask;
import com.badnewsbots.auto.AutonomousTaskSequenceRunner;
import com.badnewsbots.auto.SetMotorPowersFromGamepadVectorTask;
import com.badnewsbots.auto.SetMotorPowersTask;
import com.badnewsbots.auto.WaitSecondsTask;
import com.badnewsbots.hardware.DroneLauncher;
import com.badnewsbots.hardware.PUD;
import com.badnewsbots.hardware.robots.CenterstageCompBot;
import com.badnewsbots.auto.StopVisionPortalStreaming;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.perception.vision.processors.FTCDashboardCameraStreamProcessor;
import com.badnewsbots.perception.vision.processors.TeamPropProcessor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Config
@Autonomous
public final class BlueAuto1 extends LinearOpMode {
    public static TeamPropProcessor.TeamPropLocation testLocation = TeamPropProcessor.TeamPropLocation.CENTER; // hard coded for now
    public static double centerForwardWaitTime = 0.85;
    public static double rightTurnWaitTime = 0.6;
    public static double rightForwardWaitTime = 0.6;
    public static double leftTurnWaitTime = 0.8;
    public static double leftForwardWaitTime = 0.5;
    public static double targetRange = 17;

    private final List<AutonomousTask> taskList = new ArrayList<>();
    private PUD pud;
    private Hashtable<Integer, CenterstageCompBot.Level> levelHashtable;
    private AutonomousTaskSequenceRunner autonomousTaskSequenceRunner;
    private DroneLauncher droneLauncher;

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17); // smooooooth data
        CenterstageCompBot robot = new CenterstageCompBot(hardwareMap, telemetry);
        pud = robot.getPud();
        droneLauncher = robot.getDroneLauncher();
        MecanumDrive autoDrive = robot.getAutoDrive();
        MecanumDrive teleOpDrive = robot.getDrive();
        autonomousTaskSequenceRunner = new AutonomousTaskSequenceRunner(this);
        // Needed for opMode context to call opModeIsActive() in loop

        int[] multiPortalViewIds = VisionPortal.makeMultiPortalView(2, VisionPortal.MultiPortalLayout.HORIZONTAL);

        FTCDashboardCameraStreamProcessor frontCameraStreamProcessor = new FTCDashboardCameraStreamProcessor();
        FTCDashboardCameraStreamProcessor backCameraStreamPrcessor = new FTCDashboardCameraStreamProcessor();
        TeamPropProcessor teamPropProcessor = new TeamPropProcessor(640, 480, TeamPropProcessor.Alliance.BLUE, TeamPropProcessor.DetectionMode.RIGHT_TWO);
        AprilTagProcessor backAprilTagProcessor = new AprilTagProcessor.Builder().build();
        backAprilTagProcessor.setDecimation(2);

        AprilTagProcessor frontAprilTagProcessor = new AprilTagProcessor.Builder().build();
        frontAprilTagProcessor.setDecimation(2);

        // Live view = on Robot Controller via HDMI, Camera Stream = DS
        VisionPortal frontVisionPortal = new VisionPortal.Builder()
                .setCamera(robot.getFrontCamera())
                .addProcessor(teamPropProcessor)
                .addProcessor(frontAprilTagProcessor)
                .addProcessor(frontCameraStreamProcessor)
                .setCameraResolution(new Size(640, 480))
                .enableLiveView(true) // Live view = on Robot Controller via HDMI, Camera Stream = DS
                .setAutoStopLiveView(true)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .setLiveViewContainerId(multiPortalViewIds[0])
                .build();

        // Default resolution but just being explicit
        // Live view = on Robot Controller via HDMI, Camera Stream = DS
        VisionPortal backVisionPortal = new VisionPortal.Builder()
                .setCamera(robot.getBackCamera())
                .addProcessor(backAprilTagProcessor)
                .addProcessor(backCameraStreamPrcessor)
                .setCameraResolution(new Size(640, 480)) // Default resolution but just being explicit
                .enableLiveView(true) // Live view = on Robot Controller via HDMI, Camera Stream = DS
                .setAutoStopLiveView(true)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .setLiveViewContainerId(multiPortalViewIds[1])
                .build();

        setManualExposure(backVisionPortal, CenterstageCompBot.exposureTimeMs, CenterstageCompBot.gain);
        backVisionPortal.stopStreaming();

        frontVisionPortal.setProcessorEnabled(frontAprilTagProcessor, false);
        ftcDashboard.startCameraStream(frontCameraStreamProcessor, 30);

        taskList.add(new StopVisionPortalStreaming(frontVisionPortal));

        firstInit();
        TeamPropProcessor.TeamPropLocation location = TeamPropProcessor.TeamPropLocation.NONE;

        ElapsedTime elapsedTime = new ElapsedTime();
        double prevTime = elapsedTime.seconds();
        while (!isStarted() && !isStopRequested()) {
            double currentTime = elapsedTime.seconds();
            double deltaTime = currentTime - prevTime;
            initLoop(deltaTime);
            location = teamPropProcessor.getTeamPropLocation();

            telemetry.addData("Status", "Initialized");
            telemetry.addData("Front Camera State", frontVisionPortal.getCameraState());
            telemetry.addData("Values", Arrays.toString(teamPropProcessor.getFilterCounts()));
            telemetry.addData("Team Prop Location", location);
            /* To trick FTC Dashboard to let us start graph sooner
            telemetry.addData("Range error", 0);
            telemetry.addData("Heading error", 0);
            telemetry.addData("Yaw error", 0);
            telemetry.addData("Left X", 0);
            telemetry.addData("Left Y", 0);
            telemetry.addData("Right X", 0);*/
            telemetry.update();
            prevTime = currentTime;
        }
        switch (location) {
            case RIGHT:
                taskList.add(new SetMotorPowersFromGamepadVectorTask(autoDrive, 0, 1, 0, 1));
                taskList.add(new WaitSecondsTask(rightForwardWaitTime));
                taskList.add(new SetMotorPowersTask(autoDrive, 1, -0.1, 1, -0.1));
                taskList.add(new WaitSecondsTask(rightTurnWaitTime));
                taskList.add(new SetMotorPowersTask(autoDrive, 0, 0, 0, 0));
                break;
            case CENTER:
                taskList.add(new SetMotorPowersFromGamepadVectorTask(autoDrive, 0, 1, 0, 1));
                taskList.add(new WaitSecondsTask(centerForwardWaitTime));
                taskList.add(new SetMotorPowersTask(autoDrive, 0, 0, 0, 0));
                taskList.add(new SetMotorPowersFromGamepadVectorTask(autoDrive,0, -0.5, 0, 1));
                taskList.add(new WaitSecondsTask(0.3));
                taskList.add(new SetMotorPowersFromGamepadVectorTask(autoDrive, 0, 0, 0, 0));
                break;
            case LEFT:
                taskList.add(new SetMotorPowersFromGamepadVectorTask(autoDrive, 0, 1, 0, 1));
                taskList.add(new WaitSecondsTask(leftForwardWaitTime));
                taskList.add(new SetMotorPowersTask(autoDrive, -0.1, 1, -0.1, 1));
                taskList.add(new WaitSecondsTask(leftTurnWaitTime));
                taskList.add(new SetMotorPowersTask(autoDrive, 0, 0, 0, 0));
                break;
        }
        ftcDashboard.stopCameraStream();
        frontVisionPortal.stopStreaming();
        backVisionPortal.resumeStreaming();
        ftcDashboard.startCameraStream(backCameraStreamPrcessor, 30);

        autonomousTaskSequenceRunner.addTasks(taskList);
        autonomousTaskSequenceRunner.init();

        elapsedTime = new ElapsedTime();
        prevTime = elapsedTime.seconds();
        firstUpdate();
        while (opModeIsActive()) {
            double currentTime = elapsedTime.seconds();
            double deltaTime = currentTime - prevTime;
            autonomousTaskSequenceRunner.update();
            pud.update(deltaTime);
            telemetry.update();
            prevTime = currentTime;
        }
    }

    private void initLoop(double deltaTime) {
        pud.initUpdate(deltaTime);
    }

    private void firstUpdate() {
        pud.firstUpdate();
    }

    private void firstInit() {
        pud.autoInit();
        levelHashtable = pud.getLevelHashtable();
        droneLauncher.init();
    }
    /*
     Manually set the camera gain and exposure of a webcam through its vision portal object.
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
            exposureControl.setExposure(exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            sleep(20);
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }
    }
}
