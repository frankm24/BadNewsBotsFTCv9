package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.auto.AutonomousTask;
import com.badnewsbots.auto.AutonomousTaskSequenceRunner;
import com.badnewsbots.auto.SetMotorPowersFromGamepadVectorTask;
import com.badnewsbots.auto.WaitSecondsTask;
import com.badnewsbots.hardware.robots.CenterstageCompBot;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.ArrayList;
import java.util.List;

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
        taskList.add(new SetMotorPowersFromGamepadVectorTask(drive, 0, -1, 0,1));
        taskList.add(new WaitSecondsTask(waitTime));
        // Needed for opMode context to call opModeIsActive() in loop

        while (!isStarted() && !isStopRequested()) {
            telemetry.update();
        }

        autonomousTaskSequenceRunner.runTasks(taskList);
    }
}