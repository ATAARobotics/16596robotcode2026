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

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;
//import com.bylazar.graph.PanelsGraph;
//import com.bylazar.graph.GraphManager;

// need to use dashboard to change PID gains; comment out for competition
@Configurable
@TeleOp(name = "RobotDrive")
public class
robot_drive extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private DriveTrainBasic driveTrain;
    public GamepadEx driver = null;
    public GamepadEx operator = null;
    private JoinedTelemetry joinedTelemetry;

    public double speed = 0.0;
    public double speed2 = 0.0;
    public double flywheelspeed = 0.0;
    public LightIndicatorSubsystem indicator; //port 0 control hub

    public double shootingspeed = 0.0;

    public boolean operator_d_down;
    public boolean operator_a_button;
    public boolean operator_right_bumper;
    public boolean operator_left_bumper;
    public double targetSpeed = 0.0;

    /* private double heading; */
    @Override
    public void init() {
        driveTrain = new DriveTrainBasic(hardwareMap);
        driveTrain.init();  // commented out ,done in Auto
        driveTrain.odometer.resetPosAndIMU(); // comment out with Auto
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Update Telemetry
        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        // Initializing indicator
        indicator = new LightIndicatorSubsystem(hardwareMap);
        // Join them together
        joinedTelemetry = new JoinedTelemetry(telemetry,panelsTelemetry.getTelemetry().getWrapper(),dashboard.getTelemetry());
        joinedTelemetry.update();
        driveTrain.setDrivemoderobotcentric();
    }

    @Override
    public void start() {
        driveTrain.start();//reset encoders
        driver = new GamepadEx(gamepad1); // This controls the movement of the robot
        operator = new GamepadEx(gamepad2); // This controls the movement of items on the robot
        runtime.reset();
        // Uncomment the line below to test idle speed
        driveTrain.shooter.set(Constants.FLYWHEEL_IDLE_SPEED);
    }

    @Override
    public void loop() {
        driver.readButtons();  // enable 'was just pressed' methods
        operator.readButtons();
        driveTrain.loop(); // Current elbow
        //======= get human inputs for drive=============

        double strafeSpeed = -driver.getLeftX() * Constants.SPEED_RATIO;
        double forwardSpeed = driver.getLeftY() * Constants.SPEED_RATIO;
        joinedTelemetry.addData("strafe Speed",strafeSpeed);
        joinedTelemetry.addData("forward Speed",forwardSpeed);

        // ===== DRIVETRAIN CONTROLS =====
        driveTrain.drive(forwardSpeed, strafeSpeed);
        // =========  Flywheel control ===========
        // Set shooting speed
        operator_d_down = operator.isDown(GamepadKeys.Button.DPAD_DOWN);
        operator_a_button = operator.isDown(GamepadKeys.Button.A);
        operator_right_bumper = operator.isDown(GamepadKeys.Button.RIGHT_BUMPER);
        speed = driveTrain.shooter.getCorrectedVelocity();
        if(operator.isDown(GamepadKeys.Button.DPAD_DOWN)){
            shootingspeed = Constants.FLYWHEEL_FAR;
//            targetSpeed = Constants.FLYWHEEL_FAR_TARGET;
        }
        else {
            shootingspeed = Constants.FLYWHEEL_NEAR;
  //          targetSpeed = Constants.FLYWHEEL_NEAR_TARGET;
        }

        if(operator.isDown(GamepadKeys.Button.A)) {
            driveTrain.shooter.set(shootingspeed);
        }
       else if (operator_right_bumper) {
            driveTrain.shooter.set(Constants.REVERSE_FLYWHEEL);
       }
       else {
           driveTrain.shooter.set(Constants.FLYWHEEL_IDLE_SPEED);
       }
        // =================  Servo Control ===========================================
        if (operator_left_bumper || driveTrain.canLaunch(shootingspeed))
        //if(operator.isDown(GamepadKeys.Button.LEFT_BUMPER) )//&& driveTrain.canLaunch(shootingspeed))
        {
            driveTrain.feed1.setPower(Constants.FEED_SPEED);
            driveTrain.feed2.setPower(Constants.FEED_SPEED);
        }
        else if (operator_right_bumper) {
            driveTrain.feed1.setPower(-Constants.FEED_SPEED);
            driveTrain.feed2.setPower(-Constants.FEED_SPEED);
        }
        else {
            driveTrain.feed1.setPower(0);
            driveTrain.feed2.setPower(0);
        }

        // ===== Indicator control =====
        if (driveTrain.canLaunch(shootingspeed)){
        //if (speed > targetSpeed) {
            indicator.setColor(Constants.RGB_Light.GREEN);
        } else {
            indicator.setColor(Constants.RGB_Light.RED);
        }
        // ===============  Intake controls
        if(operator.isDown(GamepadKeys.Button.B)) {
        driveTrain.intake.set(Constants.INTAKE_SPEED);
        }
       else {
           driveTrain.intake.set(0);
        }

      //===============driving presets======================
        //small  red shooting position
        if(driver.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            driveTrain.setFacing(Constants.REDSMALL_SHOOT);
        }

        //small blue shooting position
//      dont have a constant angle yet
        if(driver.isDown(GamepadKeys.Button.LEFT_BUMPER)){
            driveTrain.setFacing(Constants.BLUESMALL_SHOOT);
        }

//        //big red shooting position
        if(driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)>= Constants.TRIGGER_TOLERANCE)  {
            driveTrain.setFacing(Constants.REDBIG_SHOOT);
        }
       //big blue shooting position
        if(driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)>= Constants.TRIGGER_TOLERANCE) {
            driveTrain.setFacing(Constants.BLUEBIG_SHOOT);
        }


       //Set small shooting area angel
//        if(driver.isDown(GamepadKeys.Button.DPAD_DOWN)){
//            set.headingpos
//        }
//        else {
//            shootingspeed = Constants.FLYWHEEL_NEAR;
//        }
//        if(operator.isDown(GamepadKeys.Button.A)) {
//            driveTrain.flywheel.set(shootingspeed);
//        }
//        else  {
//            driveTrain.flywheel.set(0);
//        }

        driveTrain.turn(driver.getRightX());
//Select N, S, E, W
//        if (driver.getRightX() <= -Constants.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants.WEST); // west
//        } else if (driver.getRightX() >= Constants.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants.EAST); // east
//        } else if (driver.getRightY() >= Constants.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants.SOUTH); // south
//        } else if (driver.getRightY() <= -Constants.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants.NORTH); // north
//        }
//
//// Select Diagonal Directions (NE,SE,SW,NW)
//        if (driver.getRightX() > Constants.JOYSTICK_TOLERANCE & driver.getRightY() < -Constants.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants.NORTH_EAST); // north east
//        } else if (driver.getRightX() < -Constants.JOYSTICK_TOLERANCE & driver.getRightY() < -Constants.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants.NORTH_WEST); // north west
//        } else if (driver.getRightX() < -Constants.JOYSTICK_TOLERANCE & driver.getRightY() > Constants.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants.SOUTH_WEST); // south west
//        } else if (driver.getRightX() > Constants.JOYSTICK_TOLERANCE & driver.getRightY() > Constants.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants.SOUTH_EAST); // south east
//        }

//        if(flywheelspeed < Constants.FLYWHEEL_RECOVERY) {
//            indicator.setColor(Constants.RGB_Light.RED);
//        }
//       else{
//           indicator.setColor(Constants.RGB_Light.GREEN);
//        }


        // Send data to telemetry
        joinedTelemetry.addData("Shooter Speed", speed); //telemetry shooter speed
        joinedTelemetry.addData("Xcor",driveTrain.getXPosition());
        joinedTelemetry.addData("Ycor",driveTrain.getYPosition());
        joinedTelemetry.addData("turnspeed", driveTrain.turnSpeed);
        joinedTelemetry.update();
    }


}














