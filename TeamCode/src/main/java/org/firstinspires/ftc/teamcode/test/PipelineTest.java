package org.firstinspires.ftc.teamcode.test;

import android.util.Size;

import com.badnewsbots.perception.vision.processors.TeamPropProcessor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@TeleOp(group = "Test")
public final class PipelineTest extends LinearOpMode {
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() throws InterruptedException {
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "leftWebcam");
        //SignalSleeveProcessor signalSleeveProcessor = new SignalSleeveProcessor(640, 480, SignalSleeveProcessor.CameraOrientation.LEFT);
        TeamPropProcessor teamPropProcessor = new TeamPropProcessor(640, 480, TeamPropProcessor.Alliance.BLUE);
        AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                //.setLensIntrinsics() // Uses one from builtinwebcamcalibrations.xml if available if you do not specify your own
                .build();

        // Live view = on Robot Controller via HDMI, Camera Stream = DS ???
        visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .setCameraResolution(new Size(640, 480))
                .enableLiveView(true) // Live view = on Robot Controller via HDMI, Camera Stream = DS
                .setAutoStopLiveView(false)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .addProcessor(aprilTagProcessor)
                //.addProcessor(signalSleeveProcessor) // processors added are enabled by default
                //.addProcessor(aprilTagProcessor)
                .build();
        setManualExposure(3, 250);

        waitForStart();
        while (opModeIsActive()) {
            /*
            if (visionPortal.getProcessorEnabled(teamPropProcessor)) {
                telemetry.addData("Team Prop location: ", teamPropProcessor.getTeamPropLocation());
                telemetry.addData("Team Prop filter averages: ", Arrays.toString(teamPropProcessor.getFilterCounts()));
                telemetry.addData("FPS: ", visionPortal.getFps());
            }

             */
            if (visionPortal.getProcessorEnabled(aprilTagProcessor)) {
                for (AprilTagDetection detection : aprilTagProcessor.getDetections()) {
                    telemetry.addData("id", detection.id);
                    telemetry.addData("Ftc range", detection.ftcPose.range);
                    telemetry.addData("Ftc bearing", detection.ftcPose.bearing);
                    telemetry.addData("Ftc yaw", detection.ftcPose.yaw);
                    telemetry.addLine("========");
                }
            }
            telemetry.update();
        }
        visionPortal.close();
    }

    private void setExposureToAutomatic() {

    }

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
