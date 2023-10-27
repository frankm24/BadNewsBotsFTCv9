package org.firstinspires.ftc.teamcode.test;

import android.graphics.Color;

import com.badnewsbots.perception.FloorClassifier;
import com.badnewsbots.util.DataSaver;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TeleOp(group = "Test")
public final class ColorSensorTest extends LinearOpMode {
    // Goal: To configure the Color Sensor for the best performance at detecting the red, white and blue tape, and
    // differentiating them from the floor.

    private String convertColorSensorHSVDataToString(List<float[]> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (float[] dataPoint : data) {
            stringBuilder.append(dataPoint[0]);
            stringBuilder.append(',');
            stringBuilder.append(dataPoint[1]);
            stringBuilder.append(',');
            stringBuilder.append(dataPoint[2]);
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    private String convertColorSensorARGBDataToString(List<int[]> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] dataPoint : data) {
            stringBuilder.append(dataPoint[0]);
            stringBuilder.append(',');
            stringBuilder.append(dataPoint[1]);
            stringBuilder.append(',');
            stringBuilder.append(dataPoint[2]);
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    List<float[]> dataPoints = new ArrayList<>();

    @Override
    public void runOpMode() {
        ModernRoboticsI2cColorSensor colorSensor = hardwareMap.get(ModernRoboticsI2cColorSensor.class, "sensor_color");
        FloorClassifier floorClassifier = new FloorClassifier(colorSensor, true);
        waitForStart();

        while (opModeIsActive()) {
            // Show sensor state on DS
            telemetry.addData("FloorClassifierPrediction", floorClassifier.update());
            telemetry.addData("Software Gain", colorSensor.getGain());
            telemetry.addData("Is light on?", colorSensor.isLightOn());

            // Raw values are direct readings of the corresponding I2C registers for each channels. These are not software processed, and
            // represent the color channels how the sensor was designed to show it. These values are 8-bit.

            /*
            int rawA = colorSensor.alpha();
            int rawR = colorSensor.red();
            int rawG = colorSensor.green();
            int rawB = colorSensor.blue();
            String values = rawA + " " + rawR + " " + rawG + " " + rawB;
            telemetry.addData("Color number", colorSensor.readUnsignedByte(ModernRoboticsI2cColorSensor.Register.COLOR_NUMBER));
            telemetry.addData("8-bit ARGB", values);
            */


            // The normalized colors are 16-bit values between 0 and 1, inclusive. The software gain affects these values.
            //telemetry.addData("Normalized ARGB [0-1] colors", colorSensor.getNormalizedColors().toString());

            // The argb() function returns the normalized colors converted to a single Android API color which is represented by a single int.
            // The Android color info can be extracted from the int representation using methods like Color.red() or Color.colorToHSV, etc.
            int androidColor = colorSensor.argb();
            float[] hsv = new float[3];
            Color.colorToHSV(androidColor, hsv); // Hue: [0, 360] Sat: [0, 1] Val: [0, 1]. Alpha channel is ignored in the calculation.
            float[] rgb = new float[] {Color.red(androidColor), Color.green(androidColor), Color.blue(androidColor)}; // RGB values

            //dataPoints.add(hsv);
            telemetry.addData("Android Color from normalized colors", androidColor);
            telemetry.addData("HSV", Arrays.toString(hsv));
            telemetry.addData("RGB", Arrays.toString(rgb));
            telemetry.update();
        }
        //String stringToSave = convertColorSensorHSVDataToString(dataPoints);
        //DataSaver.saveDataStringToTxt(stringToSave, "white_tape");
        // optimal dist: ~1 in off the ground
    }
}
