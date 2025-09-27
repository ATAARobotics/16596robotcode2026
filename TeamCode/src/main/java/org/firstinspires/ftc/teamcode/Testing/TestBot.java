/* Copyright (c) 2022 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.CAITelemetry;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.GoBildaPinpointDriver;

import java.util.Locale;

@Config // need to use dashboard to change PID gains; comment out for competition
@TeleOp(name = "TestBot")
public class TestBot extends OpMode {
//test comment

    private final ElapsedTime runtime = new ElapsedTime();
    private DriveTrainBasic driveTrain;
    public GamepadEx driver = null;
    public GamepadEx operator = null;

    @Override
    public void init() {
        telemetry = new CAITelemetry(telemetry);
        ((CAITelemetry) telemetry).setDashboardEnabled(false);
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        driveTrain = new DriveTrainBasic(hardwareMap);

        driveTrain.init();  // commented out ,done in Auto
        // comment this out for competition and ensure it happens in auto code
        driveTrain.odometer.resetPosAndIMU(); // comment out with Auto


        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void start() {
        driveTrain.start();//reset encoders

        driver = new GamepadEx(gamepad1); // This controls the movement of the robot
        operator = new GamepadEx(gamepad2); // This controls the movement of items on the robot
        runtime.reset();
    }

    @Override
    public void loop() {
        driver.readButtons();  // enable 'was just pressed' methods
        operator.readButtons();
        driveTrain.loop(); // Current elbow position

        //======= get human inputs for drive=============

        double strafeSpeed = -driver.getLeftX() * Constants.SPEED_RATIO;
        double forwardSpeed = driver.getLeftY() * Constants.SPEED_RATIO;

        //===== DRIVETRAIN CONTROLS =====
        driveTrain.drive(forwardSpeed, strafeSpeed);

//Select N, S, E, W
        if (driver.getRightX() <= -Constants.JOYSTICK_TOLERANCE) {
            driveTrain.setDirection(Constants.WEST); // west
        } else if (driver.getRightX() >= Constants.JOYSTICK_TOLERANCE) {
            driveTrain.setDirection(Constants.EAST); // east
        } else if (driver.getRightY() >= Constants.JOYSTICK_TOLERANCE) {
            driveTrain.setDirection(Constants.SOUTH); // south
        } else if (driver.getRightY() <= -Constants.JOYSTICK_TOLERANCE) {
            driveTrain.setDirection(Constants.NORTH); // north
        }

// Select Diagonal Directions (NE,SE,SW,NW)
        if (driver.getRightX() > Constants.JOYSTICK_TOLERANCE & driver.getRightY() < -Constants.JOYSTICK_TOLERANCE) {
            driveTrain.setDirection(Constants.NORTH_EAST); // north east
        } else if (driver.getRightX() < -Constants.JOYSTICK_TOLERANCE & driver.getRightY() < -Constants.JOYSTICK_TOLERANCE) {
            driveTrain.setDirection(Constants.NORTH_WEST); // north west
        } else if (driver.getRightX() < -Constants.JOYSTICK_TOLERANCE & driver.getRightY() > Constants.JOYSTICK_TOLERANCE) {
            driveTrain.setDirection(Constants.SOUTH_WEST); // south west
        } else if (driver.getRightX() > Constants.JOYSTICK_TOLERANCE & driver.getRightY() > Constants.JOYSTICK_TOLERANCE) {
            driveTrain.setDirection(Constants.SOUTH_EAST); // south east
        }


    // Emergency Reset
    if((driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0.3) && (driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0.3))
    {
        driveTrain.odometer.resetPosAndIMU();
    }
    // Telemetry Output
    telemetry.addData("Direction Stick X:","%5.2f",driver.getRightX());
    telemetry.addData("Direction Stick Y:","%5.2f",driver.getRightY());
    telemetry.addData("Strafe Stick X:","%5.2f",driver.getLeftX());
    telemetry.addData("Strafe Stick Y:","%5.2f",driver.getLeftY());
    telemetry.addData("Heading Correction",driveTrain.headingCorrection);
    // Odometer
    Pose2D pos = driveTrain.odometer.getPosition();
    String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
    telemetry.addData("Position", data);
    driveTrain.printTelemetry(telemetry);// correct usage??
}
}









