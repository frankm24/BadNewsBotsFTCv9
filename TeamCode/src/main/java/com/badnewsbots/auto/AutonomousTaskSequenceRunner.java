package com.badnewsbots.auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Iterator;
import java.util.List;

public final class AutonomousTaskSequenceRunner {
    private final LinearOpMode opMode;
    private final Telemetry telemetry;
    private List<AutonomousTask> taskList;
    private ElapsedTime elapsedTime;
    private Iterator<AutonomousTask> taskIterator;
    private AutonomousTask currentTask;
    private double prevTime;

    public AutonomousTaskSequenceRunner(LinearOpMode opMode) {
        this.opMode = opMode;
        this.telemetry = opMode.telemetry;
    }

    public void addTasks(List<AutonomousTask> taskList) {
        this.taskList = taskList;
    }

    public void init() {
        taskIterator = taskList.iterator();
        elapsedTime = new ElapsedTime();
        prevTime = elapsedTime.seconds();
        currentTask = taskIterator.next();
    }
    public void runTasks(List<AutonomousTask> taskList) {
        ElapsedTime elapsedTime = new ElapsedTime();
        double prevTime = elapsedTime.seconds();
        for (AutonomousTask task : taskList) {
            task.init();
            while (!task.isCompleted() && opMode.opModeIsActive()) {
                double currentTime = elapsedTime.seconds();
                double deltaTime = currentTime - prevTime;
                task.updateTask(deltaTime);
                telemetry.update(); // Call here so you don't have a redundant update() in each task update function.
                prevTime = currentTime;
            }
        }
    }
    public void update() {
        telemetry.addData("Current task:", currentTask.toString());
        double currentTime = elapsedTime.seconds();
        double deltaTime = currentTime - prevTime;

        if (!currentTask.isInitialized()) {
            currentTask.init();
        }
        if (currentTask.isCompleted()) {
            if (taskIterator.hasNext()) {
                currentTask = taskIterator.next();
            } else {
                opMode.requestOpModeStop();
            }
            prevTime = currentTime;
            return;
        }
        currentTask.updateTask(deltaTime);
        prevTime = currentTime;
    }
}
