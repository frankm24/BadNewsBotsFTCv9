package org.firstinspires.ftc.teamcode.test;

import android.util.Size;

import com.badnewsbots.perception.vision.processors.TeamPropProcessor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Arrays;

@TeleOp(group = "Test")
public final class PipelineTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        //SignalSleeveProcessor signalSleeveProcessor = new SignalSleeveProcessor(640, 480, SignalSleeveProcessor.CameraOrientation.LEFT);
        TeamPropProcessor teamPropProcessor = new TeamPropProcessor(640, 480, TeamPropProcessor.Alliance.BLUE);
        AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                //.setLensIntrinsics() // Uses one from builtinwebcamcalibrations.xml if available if you do not specify your own
                .build();

        // Live view = on Robot Controller via HDMI, Camera Stream = DS
        VisionPortal visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .setCameraResolution(new Size(640, 480))
                .enableLiveView(true) // Live view = on Robot Controller via HDMI, Camera Stream = DS
                .setAutoStopLiveView(false)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .addProcessor(teamPropProcessor)
                //.addProcessor(signalSleeveProcessor) // processors added are enabled by default
                //.addProcessor(aprilTagProcessor)
                .build();

        waitForStart();
        while (opModeIsActive()) {
            if (visionPortal.getProcessorEnabled(teamPropProcessor)) {
                telemetry.addData("Team Prop location: ", teamPropProcessor.getTeamPropLocation());
                telemetry.addData("Team Prop filter averages: ", Arrays.toString(teamPropProcessor.getFilterCounts()));
                telemetry.addData("FPS: ", visionPortal.getFps());
                telemetry.update();
            }
        }
        visionPortal.close();
    }
}
