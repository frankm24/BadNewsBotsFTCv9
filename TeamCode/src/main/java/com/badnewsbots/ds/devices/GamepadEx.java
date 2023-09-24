package com.badnewsbots.ds.devices;

import com.qualcomm.robotcore.hardware.Gamepad;

/*
Written by Frank Murphy, 6/13/2022

The purpose of this WRAPPER class is to expand the functionality of the robotcore Gamepad class to automatically
handle button press and release detection rather than only detecting if a button is down. This works by storing the
values of each controller button from the previous frame, rather than using a debounce timer, which we used previously.
This class is designed to run synchronously with the main loop of an OpMode. Additional functionality will be added
as needed.

NOTE 2: Not currently implementing the PS4-only values, such as touchpad because we do not use PS4 controllers.
NOTE 3: (2023) I deleted note 1 as it is no longer relevant, I fixed the var scope issue.
 */
public class GamepadEx {
    // These values represent the current state of each control on the gamepad
    private boolean a;
    private boolean b;
    private boolean x;
    private boolean y;
    private boolean dpadRight;
    private boolean dpadUp;
    private boolean dpadLeft;
    private boolean dpadDown;
    private boolean start;
    private boolean back;
    private boolean leftBumper;
    private boolean rightBumper;
    private float leftTrigger;
    private boolean leftTriggerBool;
    private float rightTrigger;
    private boolean rightTriggerBool;
    private float leftStickX;
    private float leftStickY;
    private boolean leftStickButton;
    private float rightStickX;
    private float rightStickY;
    private boolean rightStickButton;

    // These values represent the condition of each button in the previous frame
    private boolean aPrev;
    private boolean bPrev;
    private boolean xPrev;
    private boolean yPrev;
    private boolean dpadRightPrev;
    private boolean dpadUpPrev;
    private boolean dpadLeftPrev;
    private boolean dpadDownPrev;
    private boolean startPrev;
    private boolean backPrev;
    private boolean leftBumperPrev;
    private boolean rightBumperPrev;
    private boolean leftTriggerBoolPrev;
    private boolean rightTriggerBoolPrev;
    private boolean leftStickButtonPrev;
    private boolean rightStickButtonPrev;

    // These values represent when a button has just been pressed.
    private boolean aPressed;
    private boolean bPressed;
    private boolean xPressed;
    private boolean yPressed;
    private boolean dpadRightPressed;
    private boolean dpadUpPressed;
    private boolean dpadLeftPressed;
    private boolean dpadDownPressed;
    private boolean startPressed;
    private boolean backPressed;
    private boolean leftBumperPressed;
    private boolean rightBumperPressed;
    private boolean leftTriggerPressed;
    private boolean rightTriggerPressed;
    private boolean leftStickButtonPressed;
    private boolean rightStickButtonPressed;

    // These values represent when a button has just been released.
    private boolean aReleased;
    private boolean bReleased;
    private boolean xReleased;
    private boolean yReleased;
    private boolean dpadRightReleased;
    private boolean dpadUpReleased;
    private boolean dpadLeftReleased;
    private boolean dpadDownReleased;
    private boolean startReleased;
    private boolean backReleased;
    private boolean leftBumperReleased;
    private boolean rightBumperReleased;
    private boolean leftTriggerReleased;
    private boolean rightTriggerReleased;
    private boolean leftStickButtonReleased;
    private boolean rightStickButtonReleased;

    // UNFORTUNATELY, FtcRobotController is programmed so that you CANNOT simply extend the gamepad
    // class, so I have made GamepadEx a "wrapper" class, which means a Gamepad from the OpMode will be
    // passed in when an instance of GamepadEx is constructed and used to get the gamepad's info and do
    // additional functions with.
    private final Gamepad gamepad;
    public GamepadEx(Gamepad gamepad) {
        this.gamepad = gamepad;
    }
    public Gamepad getGamepad() {
        return gamepad;
    }

    // Call this method at the START of the loop.
    public void update() {
        aPrev = a;
        bPrev = b;
        xPrev = x;
        yPrev = y;
        dpadRightPrev = dpadRight;
        dpadUpPrev = dpadUp;
        dpadLeftPrev = dpadLeft;
        dpadDownPrev = dpadDown;
        startPrev = start;
        backPrev = back;
        leftBumperPrev = leftBumper;
        rightBumperPrev = rightBumper;
        leftTriggerBoolPrev = leftTriggerBool;
        rightTriggerBoolPrev = rightTriggerBool;
        leftStickButtonPrev = leftStickButton;
        rightStickButtonPrev = rightStickButton;

        a = gamepad.a;
        b = gamepad.b;
        x = gamepad.x;
        y = gamepad.y;
        dpadRight = gamepad.dpad_right;
        dpadUp = gamepad.dpad_up;
        dpadLeft = gamepad.dpad_left;
        dpadDown = gamepad.dpad_down;
        start = gamepad.start;
        back = gamepad.back;
        leftBumper = gamepad.left_bumper;
        rightBumper = gamepad.right_bumper;
        leftTrigger = gamepad.left_trigger;
        leftTriggerBool = leftTrigger > 0;
        rightTrigger = gamepad.right_trigger;
        rightTriggerBool = rightTrigger > 0;
        leftStickX = gamepad.left_stick_x;
        leftStickY = gamepad.left_stick_y;
        leftStickButton = gamepad.left_stick_button;
        rightStickX = gamepad.right_stick_x;
        rightStickY = gamepad.right_stick_y;
        rightStickButton = gamepad.right_stick_button;

        aPressed = !aPrev && a;
        bPressed = !bPrev && b;
        xPressed = !xPrev && x;
        yPressed = !yPrev && y;
        dpadRightPressed = !dpadRightPrev && dpadRight;
        dpadUpPressed = !dpadUpPrev && dpadUp;
        dpadLeftPressed = !dpadLeftPrev && dpadLeft;
        dpadDownPressed = !dpadDownPrev && dpadDown;
        startPressed = !startPrev && start;
        backPressed = !backPrev && back;
        leftBumperPressed = !leftBumperPrev && leftBumper;
        rightBumperPressed = !rightBumperPrev && rightBumper;
        leftTriggerPressed = !leftTriggerBoolPrev && leftTriggerBool;
        rightTriggerPressed = !rightTriggerBoolPrev && rightTriggerBool;
        leftStickButtonPressed = !leftStickButtonPrev && leftStickButton;
        rightStickButtonPressed = !rightStickButtonPrev && rightStickButton;

        aReleased = aPrev && !a;
        bReleased = bPrev && !b;
        xReleased = xPrev && !x;
        yReleased = yPrev && !y;
        dpadRightReleased = dpadRightPrev && !dpadRight;
        dpadUpReleased = dpadUpPrev && !dpadUp;
        dpadLeftReleased = dpadLeftPrev && !dpadLeft;
        dpadDownReleased = dpadDownPrev && !dpadDown;
        startReleased = startPrev && !start;
        backReleased = backPrev && !back;
        leftBumperReleased = leftBumperPrev && !leftBumper;
        rightBumperReleased = rightBumperPrev && !rightBumper;
        leftTriggerReleased = leftTriggerBoolPrev && !leftTriggerBool;
        rightTriggerReleased = rightTriggerBoolPrev && !rightTriggerBool;
        leftStickButtonReleased = leftStickButtonPrev && !leftStickButton;
        rightStickButtonReleased = rightStickButtonPrev && !rightStickButton;
    }

    public boolean a() {
        return a;
    }

    public boolean b() {
        return b;
    }

    public boolean x() {
        return x;
    }

    public boolean y() {
        return y;
    }

    public boolean dpadRight() {
        return dpadRight;
    }

    public boolean dpadUp() {
        return dpadUp;
    }

    public boolean dpadLeft() {
        return dpadLeft;
    }

    public boolean dpadDown() {
        return dpadDown;
    }

    public boolean start() {
        return start;
    }

    public boolean back() {
        return back;
    }

    public boolean leftBumper() {
        return leftBumper;
    }

    public boolean rightBumper() {
        return rightBumper;
    }

    public float leftTriggerFloat() {
        return leftTrigger;
    }

    public boolean leftTriggerBool() {
        return leftTriggerBool;
    }

    public float rightTriggerFloat() {
        return rightTrigger;
    }

    public boolean rightTriggerBool() {
        return rightTriggerBool;
    }

    public float leftStickX() {
        return leftStickX;
    }

    public float leftStickY() {
        return leftStickY;
    }

    public boolean leftStickButton() {
        return leftStickButton;
    }

    public float rightStickX() {
        return rightStickX;
    }

    public float rightStickY() {
        return rightStickY;
    }

    public boolean rightStickButton() {
        return rightStickButton;
    }

    public boolean aPressed() {
        return aPressed;
    }

    public boolean bPressed() {
        return bPressed;
    }

    public boolean xPressed() {
        return xPressed;
    }

    public boolean yPressed() {
        return yPressed;
    }

    public boolean dpadRightPressed() {
        return dpadRightPressed;
    }

    public boolean dpadUpPressed() {
        return dpadUpPressed;
    }

    public boolean dpadLeftPressed() {
        return dpadLeftPressed;
    }

    public boolean dpadDownPressed() {
        return dpadDownPressed;
    }

    public boolean startPressed() {
        return startPressed;
    }

    public boolean backPressed() {
        return backPressed;
    }

    public boolean leftBumperPressed() {
        return leftBumperPressed;
    }

    public boolean rightBumperPressed() {
        return rightBumperPressed;
    }

    public boolean leftTriggerPressed() {
        return leftTriggerPressed;
    }

    public boolean rightTriggerPressed() {
        return rightTriggerPressed;
    }

    public boolean leftStickButtonPressed() {
        return leftStickButtonPressed;
    }

    public boolean rightStickButtonPressed() {
        return rightStickButtonPressed;
    }

    public boolean aReleased() {
        return aReleased;
    }

    public boolean bReleased() {
        return bReleased;
    }

    public boolean xReleased() {
        return xReleased;
    }

    public boolean yReleased() {
        return yReleased;
    }

    public boolean dpadRightReleased() {
        return dpadRightReleased;
    }

    public boolean dpadUpReleased() {
        return dpadUpReleased;
    }

    public boolean dpadLeftReleased() {
        return dpadLeftReleased;
    }

    public boolean dpadDownReleased() {
        return dpadDownReleased;
    }

    public boolean startReleased() {
        return startReleased;
    }

    public boolean backReleased() {
        return backReleased;
    }

    public boolean leftBumperReleased() {
        return leftBumperReleased;
    }

    public boolean rightBumperReleased() {
        return rightBumperReleased;
    }

    public boolean leftTriggerReleased() {
        return leftTriggerReleased;
    }

    public boolean rightTriggerReleased() {
        return rightTriggerReleased;
    }

    public boolean leftStickButtonReleased() {
        return leftStickButtonReleased;
    }

    public boolean rightStickButtonReleased() {
        return rightStickButtonReleased;
    }

    public boolean aPrev() {
        return aPrev;
    }

    public boolean bPrev() {
        return bPrev;
    }

    public boolean xPrev() {
        return xPrev;
    }

    public boolean yPrev() {
        return yPrev;
    }

    public boolean dpadRightPrev() {
        return dpadRightPrev;
    }

    public boolean dpadUpPrev() {
        return dpadUpPrev;
    }

    public boolean dpadLeftPrev() {
        return dpadLeftPrev;
    }

    public boolean dpadDownPrev() {
        return dpadDownPrev;
    }

    public boolean startPrev() {
        return startPrev;
    }

    public boolean backPrev() {
        return backPrev;
    }

    public boolean leftBumperPrev() {
        return leftBumperPrev;
    }

    public boolean rightBumperPrev() {
        return rightBumperPrev;
    }

    public boolean leftTriggerBoolPrev() {
        return leftTriggerBoolPrev;
    }

    public boolean rightTriggerBoolPrev() {
        return rightTriggerBoolPrev;
    }

    public boolean leftStickButtonPrev() {
        return leftStickButtonPrev;
    }

    public boolean rightStickButtonPrev() {
        return rightStickButtonPrev;
    }
}