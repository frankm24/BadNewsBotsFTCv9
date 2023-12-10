package com.badnewsbots.auto;

import android.graphics.Paint;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

public final class AutonomousTaskSequenceRunner {
    private final LinearOpMode opMode;
    private final Telemetry telemetry;
    public AutonomousTaskSequenceRunner(LinearOpMode opMode) {
        this.opMode = opMode; // Need opMode context to call opModeIsActive to prevent the opMode from being stuck
        // when a stop is requested (stop button on DS hit)
        this.telemetry = opMode.telemetry;
    }
    public void runTasks(List<AutonomousTask> taskList) {
        ElapsedTime elapsedTime = new ElapsedTime();
        double prevTime = elapsedTime.seconds();
        for (AutonomousTask task : taskList) {
            task.init();
            while (!task.isTaskCompleted() && opMode.opModeIsActive()) {
                double currentTime = elapsedTime.seconds();
                double deltaTime = currentTime - prevTime;
                task.updateTask(deltaTime);
                telemetry.update(); // Call here so you don't have a redundant update() in each task update function.
                prevTime = currentTime;
            }
        }
    }
}
