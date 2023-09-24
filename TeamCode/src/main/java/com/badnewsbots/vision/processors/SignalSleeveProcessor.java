package com.badnewsbots.vision.processors;

import android.graphics.Canvas;
import android.os.Environment;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

// Frank: I am implementing the old SignalSleevePipeline from Power Play to force myself to learn the new VisionPortal API from first principles.
public class SignalSleeveProcessor implements VisionProcessor {
    private final boolean saveImage = false;
    public enum ConeOrientation {
        ONE,
        TWO,
        THREE,
        NONE
    }

    public enum CameraOrientation {
        LEFT,
        RIGHT
    }

    private ConeOrientation coneOrientation = ConeOrientation.NONE;
    private CameraOrientation cameraOrientation;

    private final Scalar greenMin = new Scalar(45, 50, 50);
    private final Scalar greenMax = new Scalar(70, 255, 255);

    private final Scalar magentaMin = new Scalar(150, 50, 50);
    private final Scalar magentaMax = new Scalar(170, 200, 200);

    private final Scalar orangeMin = new Scalar(13, 50, 50);
    private final Scalar orangeMax = new Scalar(20, 255, 255);

    private final Scalar roiOutlineColor = new Scalar(255, 0, 0);
    private final Scalar green = new Scalar(0, 255, 0);
    private final Scalar magenta = new Scalar(255, 0, 255);
    private final Scalar orange = new Scalar(255, 127, 0);

    private int frameWidth;
    private int frameHeight;

    private Point point1Right = new Point(300, 440);
    private Point point2Right = new Point(430, 350);
    private final Rect roiRight = new Rect(point1Right, point2Right);

    private Point point1Left = new Point(365, 350);
    private Point point2Left = new Point(480, 240);
    private final Rect roiLeft = new Rect(point1Left, point2Left);

    private final Rect roiToUse;

    private int greenCount;
    private int magentaCount;
    private int orangeCount;

    private Size pipelineSize;

    private Mat hsvImage;
    private Mat greenFiltered;
    private Mat orangeFiltered;
    private Mat magentaFiltered;

    public SignalSleeveProcessor(int width, int height, CameraOrientation cameraOrientation) {
        this.frameWidth = width;
        this.frameHeight = height;
        this.cameraOrientation = cameraOrientation;
        if (cameraOrientation == CameraOrientation.RIGHT) roiToUse = roiRight;
        else roiToUse = roiLeft;
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        pipelineSize = new Size(frameWidth, frameHeight);
        hsvImage = new Mat();

        greenFiltered = new Mat();
        magentaFiltered = new Mat();
        orangeFiltered = new Mat();
    }

    // UNLIKE EOCV, this method should not return the image to send to camera stream.
    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_RGB2HSV);
        Core.inRange(hsvImage, greenMin, greenMax, greenFiltered);
        Core.inRange(hsvImage, magentaMin, magentaMax, magentaFiltered);
        Core.inRange(hsvImage, orangeMin, orangeMax, orangeFiltered);

        Mat greenROI = greenFiltered.submat(roiToUse);
        Mat magentaROI = magentaFiltered.submat(roiToUse);
        Mat orangeROI = orangeFiltered.submat(roiToUse);

        greenCount = Core.countNonZero(greenROI);
        magentaCount = Core.countNonZero(magentaROI);
        orangeCount = Core.countNonZero(orangeROI);

        double maxMean = Math.max(magentaCount, Math.max(greenCount, orangeCount));

        if (maxMean == greenCount) {
            coneOrientation = ConeOrientation.ONE;
        } else if (maxMean == magentaCount) {
            coneOrientation = ConeOrientation.TWO;
        } else if (maxMean == orangeCount) {
            coneOrientation = ConeOrientation.THREE;
        } else {
            coneOrientation = ConeOrientation.NONE;
        }
        return null;
    }

    // This method uses the canvas API to draw the results instead of the EasyOpenCV implementation of OpenCV functions like HMS boxes, setTo, etc.
    // This function makes it easier to draw the result on the image because that code can be organized in a separate method and doesn't need to be
    // performed in processFrame. This is really cool and useful.
    // Must multiply coords by scaleBmpPxToCanvasPx to use like openCV api
    // https://www.reddit.com/r/FTC/comments/16lb7wd/vision_pipeline_help_for_a_meche_coach/k12qow4/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        /*
        I need the equivalent to this OpenCV code:
        input.setTo(green, greenFiltered);
        input.setTo(magenta, magentaFiltered);
        input.setTo(orange, orangeFiltered);
        Imgproc.rectangle(input, roiToUse, roiOutlineColor, 3);
         */

    }
}
