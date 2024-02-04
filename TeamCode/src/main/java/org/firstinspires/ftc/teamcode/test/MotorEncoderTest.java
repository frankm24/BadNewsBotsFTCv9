package org.firstinspires.ftc.teamcode.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.badnewsbots.auto.AutonomousTask;
import com.badnewsbots.auto.AutonomousTaskSequenceRunner;
import com.badnewsbots.auto.SetDrivePowerForTimeWithEncoder;
import com.badnewsbots.auto.WaitSecondsTask;
import com.badnewsbots.hardware.drivetrains.MecanumDrive;
import com.badnewsbots.hardware.robots.AutonomousTestingBot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.ArrayList;
import java.util.List;

@Autonomous
public final class MotorEncoderTest extends LinearOpMode {

    private final List<AutonomousTask> taskList = new ArrayList<>();

    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard ftcDashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, ftcDashboard.getTelemetry());
        telemetry.setMsTransmissionInterval(17); // smooooooth data

        AutonomousTestingBot robot = new AutonomousTestingBot(hardwareMap);
        MecanumDrive drive = robot.getDrive();
        AutonomousTaskSequenceRunner autonomousTaskSequenceRunner = new AutonomousTaskSequenceRunner(this);
        // Needed for opMode context to call opModeIsActive() in loop

        // do tasks here

        taskList.add(new SetDrivePowerForTimeWithEncoder(drive, telemetry, 1, 0, 0, 1, 10));
        taskList.add(new WaitSecondsTask(20));

        autonomousTaskSequenceRunner.runTasks(taskList);
    }
}
