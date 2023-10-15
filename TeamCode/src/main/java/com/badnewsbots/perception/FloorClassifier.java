package com.badnewsbots.perception;

import android.graphics.Color;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;

public final class FloorClassifier {
    public enum FloorType {
        RED_TAPE,
        BLUE_TAPE,
        WHITE_TAPE,
        FOAM_TILE,
        //OUT_OF_RANGE
    }
    // Need to test with other color sensors to see, just using MR sensor for now
    ModernRoboticsI2cColorSensor colorSensor;

    public FloorClassifier(ModernRoboticsI2cColorSensor colorSensor, boolean useLED) {
        this.colorSensor = colorSensor;
        colorSensor.enableLed(useLED);
    }

    public FloorType update() {
        return classifySimple();
    }
    // Classify using results from algorithm built-in to sensor, which determines the "color number"
    // see chart: https://modernroboticsinc.com/product/color-sensor/
    private FloorType classifySimple() {
        // Color numbers and floor types based on testing with LED on:
        // white: 16, 14, 6
        // foam tile: 11, 9
        // blue: 3
        // red: 10
        //int androidColor = colorSensor.argb();
        //if (androidColor == 0) return FloorType.OUT_OF_RANGE;
        int mrColorNumber = colorSensor.readUnsignedByte(ModernRoboticsI2cColorSensor.Register.COLOR_NUMBER);
        if (mrColorNumber == 16 || mrColorNumber == 14 || mrColorNumber == 6) return FloorType.WHITE_TAPE;
        if (mrColorNumber == 3) return FloorType.BLUE_TAPE;
        if (mrColorNumber == 10) return FloorType.RED_TAPE;
        return FloorType.FOAM_TILE;
    }

    // doesn't work :(
    private FloorType classifyUsingCoolAlgorithm() {
        // Get latest sensor readings
        int androidColor = colorSensor.argb();
        float[] hsv = new float[3];
        Color.colorToHSV(androidColor, hsv); // Hue: [0, 360] Sat: [0, 1] Val: [0, 1]. Alpha channel is ignored in the calculation.
        float hue = hsv[0];
        float sat = hsv[1];
        float val = hsv[2];
        if (sat < 1) {
            if (val < 0.5) return FloorType.FOAM_TILE;
            return FloorType.WHITE_TAPE;
        }
        float distBlue = Math.abs(hue - 240);
        float distRed = Math.abs(hue);
        if (distRed > distBlue) return FloorType.BLUE_TAPE;
        return FloorType.RED_TAPE;
        // Determine whether sensor sees red tape, blue tape, white tape, or foam tile
    }
}
