package com.badnewsbots.perception.vision.processors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.badnewsbots.perception.vision.CameraOrientation;
import com.badnewsbots.util.AndroidGraphicsHelper;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

// Frank: I am implementing the old SignalSleevePipeline from Power Play to force myself to learn the new VisionPortal API from first principles.
public final class SignalSleeveProcessor implements VisionProcessor {
    private final boolean saveImage = false;
    public enum ConeOrientation {
        ONE,
        TWO,
        THREE,
        NONE
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

    private int frameWidth;
    private int frameHeight;

    private org.opencv.core.Point point1Right = new org.opencv.core.Point(300, 440);
    private org.opencv.core.Point point2Right = new org.opencv.core.Point(430, 350);
    private final org.opencv.core.Rect roiRight = new org.opencv.core.Rect(point1Right, point2Right);
    private RectF roiRightCanvas = new RectF(300, 350, 430, 440);

    private org.opencv.core.Point point1Left = new org.opencv.core.Point(365, 350);
    private org.opencv.core.Point point2Left = new Point(480, 240);
    private final org.opencv.core.Rect roiLeft = new org.opencv.core.Rect(point1Left, point2Left);
    private RectF roiLeftCanvas = new RectF(365, 240,480,350);

    private final org.opencv.core.Rect roiToUse;
    private RectF roiToUseCanvas;

    private int greenCount;
    private int magentaCount;
    private int orangeCount;

    private Mat hsvImage;
    private Mat greenFiltered;
    private Mat orangeFiltered;
    private Mat magentaFiltered;

    public SignalSleeveProcessor(int width, int height, CameraOrientation cameraOrientation) {
        this.frameWidth = width;
        this.frameHeight = height;
        this.cameraOrientation = cameraOrientation;
        if (cameraOrientation == CameraOrientation.RIGHT) {
            roiToUse = roiRight;
            roiToUseCanvas = roiRightCanvas;
        }
        else {
            roiToUse = roiLeft;
            roiToUseCanvas = roiLeftCanvas;
        }
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        hsvImage = new Mat();

        greenFiltered = new Mat();
        magentaFiltered = new Mat();
        orangeFiltered = new Mat();
    }

    // UNLIKE EOCV, this method should not return the image to send to camera stream.
    // Instead, it can return userContext to be used in onDrawFrame
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
        return frame;
    }

    // This method uses the canvas API to draw the results instead of the EasyOpenCV implementation of OpenCV functions like HMS boxes, setTo, etc.
    // This function makes it easier to draw the result on the image because that code can be organized in a separate method and doesn't need to be
    // performed in processFrame. This is really cool and useful.
    // https://www.reddit.com/r/FTC/comments/16lb7wd/vision_pipeline_help_for_a_meche_coach/k12qow4/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
    // To use this method, use OPENCV coords for everything except
    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        Bitmap greenFilteredBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.RGB_565);
        Bitmap orangeFilteredBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.RGB_565);
        Bitmap magentaFilteredBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.RGB_565);

        Utils.matToBitmap(greenFiltered, greenFilteredBitmap);
        Utils.matToBitmap(orangeFiltered, orangeFilteredBitmap);
        Utils.matToBitmap(magentaFiltered, magentaFilteredBitmap);

        AndroidGraphicsHelper.applyMaskToCanvas(canvas, greenFilteredBitmap, scaleBmpPxToCanvasPx, Color.GREEN);
        AndroidGraphicsHelper.applyMaskToCanvas(canvas, orangeFilteredBitmap, scaleBmpPxToCanvasPx, Color.rgb(255, 127, 0));
        AndroidGraphicsHelper.applyMaskToCanvas(canvas, magentaFilteredBitmap, scaleBmpPxToCanvasPx, Color.MAGENTA);

        Paint myPaint = new Paint();
        myPaint.setColor(Color.RED);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(scaleCanvasDensity*4);

        RectF roiToUseCanvasAdjusted = new RectF(roiToUseCanvas.left*scaleBmpPxToCanvasPx,
                roiToUseCanvas.top*scaleBmpPxToCanvasPx,
                roiToUseCanvas.right*scaleBmpPxToCanvasPx,
                roiToUseCanvas.bottom*scaleBmpPxToCanvasPx);
        canvas.drawRect(roiToUseCanvasAdjusted, myPaint);
        //canvas.drawPoint(639*scaleBmpPxToCanvasPx, 479*scaleBmpPxToCanvasPx, myPaint);
        /*
        I need the equivalent to this OpenCV code:
        input.setTo(green, greenFiltered);
        input.setTo(magenta, magentaFiltered);
        input.setTo(orange, orangeFiltered);
        Imgproc.rectangle(input, roiToUse, roiOutlineColor, 3);
         */
    }
    // same as methods in old signal sleeve pipeline
    public ConeOrientation getConeOrientation() {return coneOrientation;}

    public double[] getFilterAverages() {
        return new double[] {greenCount, magentaCount, orangeCount};
    }
}
