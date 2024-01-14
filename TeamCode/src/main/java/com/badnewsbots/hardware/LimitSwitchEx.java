package com.badnewsbots.hardware;

import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

// Class which wraps a TouchSensor, providing keyboard-like functionality (methods that fire on initial press and on release)
public class LimitSwitchEx {
    private final TouchSensor touchSensor;
    private boolean switchDownPrev = false;
    private boolean switchDown = false;
    private boolean switchPressed = false;
    private boolean switchReleased = false;

    public LimitSwitchEx(TouchSensor touchSensor) {
        this.touchSensor = touchSensor;
    }
    // Update must be called in your OpMode's control loop
    public void update() {
        switchDown = touchSensor.isPressed();
        switchPressed = !switchDownPrev && switchDown;
        switchReleased = switchDownPrev && !switchDown;
        switchDownPrev = switchDown;
    }
    // wasPressed() returns true if the switched was just pressed (last frame it was not pressed and this frame it is)
    public boolean wasPressed() {return switchPressed;}
    // wasReleased() returns true if the switched was just release (last frame it was pressed and this frame it is not)
    public boolean wasReleased() {return switchReleased;}
    // isDown() returns true whenever the switch is in the down position. Functions identically to TouchSensor.isPressed() method,
    // but only updates it update() is called in the control loop
    public boolean isDown() {return switchDown;}
}
