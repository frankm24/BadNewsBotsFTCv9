package com.badnewsbots;

public class PUDController {
    private double kp;
    private double ki;
    private double kd;

    private double integral;
    private double previousError;

    public PUDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public void setPIDCoefficents(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    // Handle the error calculation outside of this class so it ca nwasily be altered (swapped actual and target values)
    public double calculate(double error, double deltaTime) {
        integral += error * deltaTime;
        double derivative = (error - previousError) / deltaTime;
        double output = kp * error + ki * integral + kd * derivative;

        previousError = error;
        return output;
    }
}
