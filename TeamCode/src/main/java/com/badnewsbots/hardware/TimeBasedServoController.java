package com.badnewsbots.hardware;

import com.qualcomm.robotcore.hardware.Servo;

// Class to keep track of a servo's state by estimating the position based on the time it takes to move a given distance.
// Use this when there is no encoder feedback built in to the system the servo drives.
// (Servos have no encoders, so feedback has to be implemented separately.)
// This assumes the servo is not running in continous mode (goBilda servos only)
// Needs to be tested with a servo to ensure it works correctly.
// Degrees behavior: The position the servo is set to during the init phase will
// Todo: THIS CLASS HAS NEVER BEEN TESTED EVER ONCE
public class TimeBasedServoController {
    private final Servo servo;
    private final double rotationSpeedSecondsPerDegree; // needs to be a highly accurate measured value, not human guesstimate
    private final double maxRotationDegrees; // How far the servo can rotate from 0 degrees.
    private final double referencePositionDegrees;
    private volatile double currentPositionDegrees; // volatile because could be modified sync or async (another thread)
    private volatile ServoState currentState; // volatile because could be modified sync or async (another thread)
    public enum ServoState {
        HOLDING_POSITION,
        MOVING_TO_POSITION
    }

    public TimeBasedServoController(Servo servo, double rotationSpeedSecondsPerDegree, double maxRotationDegrees, double referencePositionDegrees) {
        this.servo = servo;
        this.rotationSpeedSecondsPerDegree = rotationSpeedSecondsPerDegree;
        this.maxRotationDegrees = maxRotationDegrees;
        this.referencePositionDegrees = referencePositionDegrees;
        this.currentPositionDegrees = 0; // Unknown if init is not called, ignoring for now and set to 0.
    }

    public Servo getServo() {return servo;}
    public ServoState getCurrentState() {return currentState;}

    public void init() {
        goToPositionDegreesSync(referencePositionDegrees);
    }

    public void goToPositionDegreesAsync(double desiredPositionDegrees) {
        Thread thread = new Thread(() -> goToPositionDegreesSync(desiredPositionDegrees));
        thread.start();
    }

    public void goToPositionDegreesSync(double desiredPositionDegrees) {
        currentState = ServoState.MOVING_TO_POSITION;
        desiredPositionDegrees += referencePositionDegrees;
        servo.setPosition(desiredPositionDegrees / maxRotationDegrees);
        double absChangeInDegrees = Math.abs(desiredPositionDegrees - currentPositionDegrees);
        try {
            Thread.sleep( (long) (absChangeInDegrees * (1 * 1E3) * rotationSpeedSecondsPerDegree) );
            // Wait until we estimate servo has reached desired position. (ms)
            // estimated time for servo to rotate a number of degrees:
            // x degrees * ( time servo takes to rotate fully / servo range of rotation in degrees )
        } catch (Exception e) {
            throw new RuntimeException(e); // If the wait was interrupted, we have a coding error...
        }
        currentState = ServoState.HOLDING_POSITION;
        currentPositionDegrees = desiredPositionDegrees;
    }

    // public void incrementServoPosition()
}
