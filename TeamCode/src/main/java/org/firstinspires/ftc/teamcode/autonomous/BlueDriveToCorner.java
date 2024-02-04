package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.auto.AutonomousTask;
import com.badnewsbots.auto.AutonomousTaskSequenceRunner;
import com.badnewsbots.auto.SetMotorPowersTask;
import com.badnewsbots.auto.WaitSecondsTask;
import com.badnewsbots.hardware.drivetrains.Drive;
import com.badnewsbots.hardware.robots.CenterstageCompBot;
import com.badnewsbots.auto.DriveToAprilTagTask;
import com.badnewsbots.auto.StopVisionPortalStreaming;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.hardware.robots.AutonomousTestingBot;
import com.badnewsbots.perception.vision.CameraOrientation;
import com.badnewsbots.perception.vision.processors.FTCDashboardCameraStreamProcessor;
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

@Config
@Autonomous
public final class BlueDriveToCorner extends LinearOpMode {
    private final List<AutonomousTask> taskList = new ArrayList<>();
    public static double waitTime = 3;

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17); // smooooooth data

        CenterstageCompBot robot = new CenterstageCompBot(hardwareMap, telemetry);
        MecanumDrive drive = robot.getDrive();
        AutonomousTaskSequenceRunner autonomousTaskSequenceRunner = new AutonomousTaskSequenceRunner(this);
        taskList.add(new SetMotorPowersTask(drive, 0, -1, 0,0,1));
        taskList.add(new WaitSecondsTask(waitTime));
        // Needed for opMode context to call opModeIsActive() in loop

        while (!isStarted() && !isStopRequested()) {
            telemetry.update();
        }

        autonomousTaskSequenceRunner.runTasks(taskList);
    }
}