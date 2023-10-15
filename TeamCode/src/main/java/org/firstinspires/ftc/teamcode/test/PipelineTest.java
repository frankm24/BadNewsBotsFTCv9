package org.firstinspires.ftc.teamcode.test;

import android.util.Size;

import com.badnewsbots.perception.vision.processors.SignalSleeveProcessor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.Arrays;

@TeleOp(group = "Test")
public final class PipelineTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        SignalSleeveProcessor signalSleeveProcessor = new SignalSleeveProcessor(640, 480, SignalSleeveProcessor.CameraOrientation.LEFT);

        // Live view = on Robot Controller via HDMI, Camera Stream = DS
        VisionPortal visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .setCameraResolution(new Size(640, 480))
                .enableLiveView(true) // Live view = on Robot Controller via HDMI, Camera Stream = DS
                .setAutoStopLiveView(false)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                //.addProcessor(signalSleeveProcessor) // processors added are enabled by default
                .build();

        waitForStart();
        while (opModeIsActive()) {
            if (visionPortal.getProcessorEnabled(signalSleeveProcessor)) {
                telemetry.addData("Cone orientation: ", signalSleeveProcessor.getConeOrientation());
                telemetry.addData("Color filter averages: ", Arrays.toString(signalSleeveProcessor.getFilterAverages()));
                telemetry.addData("FPS: ", visionPortal.getFps());
                telemetry.update();
            }
        }
        visionPortal.close();
    }
}
