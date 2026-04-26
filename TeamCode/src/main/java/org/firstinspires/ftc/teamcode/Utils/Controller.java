package org.firstinspires.ftc.teamcode.Utils;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

public class Controller {
    // Triggers
    public double triggerLeft = 0.0;
    public double triggerRight = 0.0;
    // Buttons
    public boolean buttonA = false;
    public boolean buttonB = false;
    public boolean buttonX = false;
    public boolean buttonY = false;
    // D-Pad
    public boolean dpadUp = false;
    public boolean dpadDown = false;
    public boolean dpadLeft = false;
    public boolean dpadRight = false;
    // Controller Sticks
    public double stickRightX = 0.0;
    public double stickRightY = 0.0;
    public double stickLeftX = 0.0;
    public double stickLeftY = 0.0;
    // Bummers
    public boolean bummerLeft = false;
    public boolean bummerRight = false;
    // Tolerances
    private double toleranceRightX = 0.0;
    private double toleranceRightY = 0.0;
    private double toleranceLeftX = 0.0;
    private double toleranceLeftY = 0.0;
    private double toleranceTriggerLeft = 0.0;
    private double toleranceTriggerRight = 0.0;

    // Method Section
    public void updateController(GamepadEx gamepad) {
        gamepad.readButtons();
        // Update Bummers
        this.bummerLeft = gamepad.isDown(GamepadKeys.Button.LEFT_BUMPER);
        this.bummerRight = gamepad.isDown(GamepadKeys.Button.RIGHT_BUMPER);
        // Update D-Pad
        this.dpadUp = gamepad.isDown(GamepadKeys.Button.DPAD_UP);
        this.dpadDown = gamepad.isDown(GamepadKeys.Button.DPAD_DOWN);
        this.dpadLeft = gamepad.isDown(GamepadKeys.Button.DPAD_LEFT);
        this.dpadRight = gamepad.isDown(GamepadKeys.Button.DPAD_RIGHT);
        // Update Buttons
        this.buttonA = gamepad.isDown(GamepadKeys.Button.A);
        this.buttonB = gamepad.isDown(GamepadKeys.Button.B);
        this.buttonX = gamepad.isDown(GamepadKeys.Button.X);
        this.buttonY = gamepad.isDown(GamepadKeys.Button.Y);
        // Update Triggers
        if (Math.abs(gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)) >= this.toleranceTriggerLeft) {
            this.triggerLeft = gamepad.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER);
        }
        else {
            this.triggerLeft = 0.0;
        }
        if (Math.abs(gamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)) >= this.toleranceTriggerRight) {
            this.triggerRight = gamepad.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER);
        }
        else {
            this.triggerRight = 0.0;
        }
        // Update Right Stick
        if (Math.abs(gamepad.getRightX()) >= this.toleranceRightX) {
            this.stickRightX = gamepad.getRightX();
        }
        else {
            this.stickRightX = 0.0;
        }
        if (Math.abs(gamepad.getRightY()) >= this.toleranceRightY) {
            this.stickRightY = gamepad.getRightY();
        }
        else {
            this.stickRightY = 0.0;
        }
        // Update Left Stick
        if (Math.abs(gamepad.getLeftX()) >= this.toleranceLeftX) {
            this.stickLeftX = gamepad.getLeftX();
        }
        else {
            this.stickLeftX = 0.0;
        }
        if (Math.abs(gamepad.getLeftY()) >= this.toleranceLeftY) {
            this.stickLeftY = gamepad.getLeftY();
        }
        else {
            this.stickLeftY = 0.0;
        }
    }

    // Get Section
    public double getToleranceRightX() { return this.toleranceRightX;}
    public double getToleranceRightY() { return this.toleranceRightY;}
    public double getToleranceLeftX() { return this.toleranceLeftX;}
    public double getToleranceLeftY() { return this.toleranceLeftY;}
    public double getToleranceLeftTrigger() { return this.toleranceTriggerLeft;}
    public double getToleranceRightTrigger() { return this.toleranceTriggerRight;}

    // Set Section
    public void setToleranceRightX(float value) { this.toleranceRightX = value;}
    public void setToleranceRightY(float value) { this.toleranceRightY = value;}
    public void setToleranceLeftX(float value) { this.toleranceLeftX = value;}
    public void setToleranceLeftY(float value) { this.toleranceLeftY = value;}
    public void setToleranceLeftTrigger(float value) { this.toleranceTriggerLeft = value;}
    public void setToleranceRightTrigger(float value) { this.toleranceTriggerRight = value;}
}
