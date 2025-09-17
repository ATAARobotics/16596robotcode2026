package org.firstinspires.ftc.teamcode;

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
// The following libaries are special for LimeLight
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;

@Config // need to use dashboard to change PID gains; comment out for competition
@TeleOp(name = "LimeLightTest")
public class LimeLightTest extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private DriveTrainBasic driveTrain;
    public GamepadEx driver = null;
    private Limelight3A limelight;
    private double strafeSpeed = 0.0;
    private double forwardSpeed = 0.0;
    private LLResult limeLightResults = null;

    @Override
    public void init() {
        telemetry = new CAITelemetry(telemetry);
        ((CAITelemetry) telemetry).setDashboardEnabled(false);
        telemetry.addData("Status", "Initializing");
        telemetry.update();
        driveTrain = new DriveTrainBasic(hardwareMap);
        driveTrain.init();
        driveTrain.odometer.resetPosAndIMU();
        // Limelight Stuff
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
         // This tells Limelight to start looking!
        // Update Telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }
    public void start() {
        limelight.start(); // This tells Limelight to start looking!
    }
    @Override
    public void loop() {
        driver.readButtons();
        // Driver Input Processing for Loop
        //======= get human inputs for drive=============

        strafeSpeed = -driver.getLeftX() * Constants.SPEED_RATIO;
        forwardSpeed = driver.getLeftY() * Constants.SPEED_RATIO;
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
// Process Limelight data
        limeLightResults = limelight.getLatestResult();
        // Send data to telemetry
        if (limeLightResults != null && limeLightResults.isValid()) {
            telemetry.addData("Target X", limeLightResults.getTx()); // How far left or right the target is (degrees)
            telemetry.addData("Target Y", limeLightResults.getTy()); // How far up or down the target is (degrees)
            telemetry.addData("Target Area", limeLightResults.getTa()); // How big the target looks (0%-100% of the image)
        } else {
            telemetry.addData("Limelight", "No Targets");
        }


    }
}