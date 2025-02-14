package com.badnewsbots.perception.vision.processors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.acmerobotics.dashboard.config.Config;
import com.badnewsbots.util.AndroidGraphicsHelper;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

// Based on SignalSleeveProcessor port
@Config
public final class TeamPropProcessor implements VisionProcessor {
    public enum Alliance {
        RED,
        BLUE
    }

    public enum TeamPropLocation {
        LEFT,
        CENTER,
        RIGHT,
        NONE
    }

    public enum DetectionMode {
        THREE,
        RIGHT_TWO
    }
    public static int redtwoModeDetectionThresh = 2000;
    public static int blueTwoModeDetectionThresh = 2500;

    private final Alliance alliance;
    private final DetectionMode detectionMode;

    private final Scalar redMin = new Scalar(0, 50, 50);
    private final Scalar redMax = new Scalar(10, 255, 255);

    private final Scalar blueMin = new Scalar(110, 30, 30);
    private final Scalar blueMax = new Scalar(130, 255, 255);

    private final int frameWidth;
    private final int frameHeight;

    private final org.opencv.core.Point point1Left = new org.opencv.core.Point(100, 100);
    private final org.opencv.core.Point point2Left = new Point(200, 200);
    private final org.opencv.core.Rect leftROI = new org.opencv.core.Rect(point1Left, point2Left);
    private final RectF roiLeftCanvas = new RectF(100, 100,200,200);
    // center: 160, 220
    private final org.opencv.core.Point point1Center = new org.opencv.core.Point(100, 200);
    private final org.opencv.core.Point point2Center = new org.opencv.core.Point(200, 300);
    private final org.opencv.core.Rect centerROI = new org.opencv.core.Rect(point1Center, point2Center);
    private final RectF roiCenterCanvas = new RectF(100, 200, 200, 300);
    // right: 513, 257
    private final org.opencv.core.Point point1Right = new org.opencv.core.Point(481, 217);
    private final org.opencv.core.Point point2Right = new org.opencv.core.Point(581, 317);
    private final org.opencv.core.Rect rightROI = new org.opencv.core.Rect(point1Right, point2Right);
    private final RectF roiRightCanvas = new RectF(481, 217, 581, 317);

    private int leftCount;
    private int centerCount;
    private int rightCount;
    private TeamPropLocation teamPropLocation = TeamPropLocation.NONE;

    private Mat hsvImage;
    private Mat filtered;

    public TeamPropProcessor(int width, int height, Alliance alliance, DetectionMode detectionMode) {
        this.alliance = alliance;
        this.frameWidth = width;
        this.frameHeight = height;
        this.detectionMode = detectionMode;
        if (alliance == Alliance.BLUE) {
            redtwoModeDetectionThresh = blueTwoModeDetectionThresh;
        }
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        hsvImage = new Mat();
        filtered = new Mat();
    }

    // UNLIKE EOCV, this method should not return the image to send to camera stream.
    // Instead, it can return userContext to be used in onDrawFrame
    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_RGB2HSV);
        if (alliance == Alliance.RED) {
            Core.inRange(hsvImage, redMin, redMax, filtered);
        } else {
            Core.inRange(hsvImage, blueMin, blueMax, filtered);
        }
        if (detectionMode == DetectionMode.THREE) {
            Mat leftMat = filtered.submat(leftROI);
            Mat centerMat = filtered.submat(centerROI);
            Mat rightMat = filtered.submat(rightROI);

            leftCount = Core.countNonZero(leftMat);
            centerCount = Core.countNonZero(centerMat);
            rightCount = Core.countNonZero(rightMat);

            double maxPixels = Math.max(centerCount, Math.max(leftCount, rightCount));

            if (maxPixels == leftCount) {
                teamPropLocation = TeamPropLocation.LEFT;
            } else if (maxPixels == centerCount) {
                teamPropLocation = TeamPropLocation.CENTER;
            } else if (maxPixels == rightCount) {
                teamPropLocation = TeamPropLocation.RIGHT;
            } else {
                teamPropLocation = TeamPropLocation.NONE;
            }
            return frame;
        } else {
            Mat centerMat = filtered.submat(centerROI);
            Mat rightMat = filtered.submat(rightROI);
            centerCount = Core.countNonZero(centerMat);
            rightCount = Core.countNonZero(rightMat);

            if (centerCount < redtwoModeDetectionThresh) {
                if (rightCount < redtwoModeDetectionThresh) {
                    teamPropLocation = TeamPropLocation.LEFT;
                } else {
                    teamPropLocation = TeamPropLocation.RIGHT;
                }
            } else {
                teamPropLocation = TeamPropLocation.CENTER;
            }
            return frame;
        }
    }

    // This method uses the canvas API to draw the results instead of the EasyOpenCV implementation of OpenCV functions like HMS boxes, setTo, etc.
    // This function makes it easier to draw the result on the image because that code can be organized in a separate method and doesn't need to be
    // performed in processFrame. This is really cool and useful.
    // https://www.reddit.com/r/FTC/comments/16lb7wd/vision_pipeline_help_for_a_meche_coach/k12qow4/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
    // To use this method, use OPENCV coords for everything except
    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        Bitmap filteredBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.RGB_565);
        Utils.matToBitmap(filtered, filteredBitmap);

        if (alliance == Alliance.RED) {
            AndroidGraphicsHelper.applyMaskToCanvas(canvas, filteredBitmap, scaleBmpPxToCanvasPx, Color.RED);
        } else {
            AndroidGraphicsHelper.applyMaskToCanvas(canvas, filteredBitmap, scaleBmpPxToCanvasPx, Color.BLUE);
        }

        Paint myPaint = new Paint();
        myPaint.setColor(Color.RED);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(scaleCanvasDensity*4);
        if (detectionMode == DetectionMode.THREE) {
            RectF leftROIAdjusted = new RectF(roiLeftCanvas.left * scaleBmpPxToCanvasPx,
                    roiLeftCanvas.top * scaleBmpPxToCanvasPx,
                    roiLeftCanvas.right * scaleBmpPxToCanvasPx,
                    roiLeftCanvas.bottom * scaleBmpPxToCanvasPx);
            canvas.drawRect(leftROIAdjusted, myPaint);
        }
        RectF centerROIAdjusted = new RectF(roiCenterCanvas.left*scaleBmpPxToCanvasPx,
                roiCenterCanvas.top*scaleBmpPxToCanvasPx,
                roiCenterCanvas.right*scaleBmpPxToCanvasPx,
                roiCenterCanvas.bottom*scaleBmpPxToCanvasPx);
        canvas.drawRect(centerROIAdjusted, myPaint);

        RectF rightROIAdjusted = new RectF(roiRightCanvas.left*scaleBmpPxToCanvasPx,
                roiRightCanvas.top*scaleBmpPxToCanvasPx,
                roiRightCanvas.right*scaleBmpPxToCanvasPx,
                roiRightCanvas.bottom*scaleBmpPxToCanvasPx);
        canvas.drawRect(rightROIAdjusted, myPaint);
    }
    // Returns one of the three possible locations of the team prop, left, right, or cente, or returns NONE if the agorithm has not been run yet.
    public TeamPropLocation getTeamPropLocation() {return teamPropLocation;}
    // Returns the amount of pixels that did not pass through the active color filter when applied on the latest frame
    // Format: Left, Center, Right
    public double[] getFilterCounts() {return new double[] {leftCount, centerCount, rightCount};}
}
