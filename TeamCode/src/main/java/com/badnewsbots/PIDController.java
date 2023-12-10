package com.badnewsbots;

public class PIDController {
    private final double kp;
    private final double ki;
    private final double kd;

    private double integral;
    private double previousError;

    public PIDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    public double calculate(double error, double deltaTime) {
        integral += error * deltaTime;
        double derivative = (error - previousError) / deltaTime;
        double output = kp * error + ki * integral + ki * derivative;

        previousError = error;
        return output;
    }

}
