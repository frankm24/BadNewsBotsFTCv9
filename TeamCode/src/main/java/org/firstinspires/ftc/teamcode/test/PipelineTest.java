package org.firstinspires.ftc.teamcode.test;

import android.util.Size;

import com.badnewsbots.vision.processors.SignalSleeveProcessor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(group = "Test")
public class PipelineTest extends LinearOpMode {
    private VisionPortal visionPortal;
    private SignalSleeveProcessor signalSleeveProcessor;

    @Override
    public void runOpMode() {
        WebcamName webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");
        signalSleeveProcessor = new SignalSleeveProcessor(640, 480, SignalSleeveProcessor.CameraOrientation.LEFT);

        visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .setCameraResolution(new Size(640, 480))
                .enableLiveView(true)
                .setAutoStopLiveView(false)

                .build();
    }
}
