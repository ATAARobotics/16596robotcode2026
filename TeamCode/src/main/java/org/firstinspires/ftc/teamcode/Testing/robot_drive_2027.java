package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants2027;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrain2027;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrain2027.ShooterMode;


@Configurable
@Config
@TeleOp(name = "robot_drive_2027",group = "_0A")
public class
robot_drive_2027 extends OpMode {
    private boolean testMode = true;  // Set to false during comp to disable dashboard and setpoints/config buttons
    private final ElapsedTime runtime = new ElapsedTime();
    private DriveTrain2027 driveTrain;
    public GamepadEx driver = null;
    public GamepadEx operator = null;
    private JoinedTelemetry joinedTelemetry;

    public double speed = 0.0;

    public LightIndicatorSubsystem indicator; //port 0 control hub

    public double currentTime = 0.0;
    public boolean operatorDriving = false;
    public String spMode = "FlyWheel Reverse";
    private double driverSpeedRatio = Constants2027.DRIVER_SPEED_RATIO;

    @Override
    public void init() {
        driveTrain = new DriveTrain2027(hardwareMap);
        driveTrain.init();  // commented out ,done in Auto
        //driveTrain.odometer.resetPosAndIMU(); // comment out with Auto
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Update Telemetry

        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        joinedTelemetry = new JoinedTelemetry(telemetry, panelsTelemetry.getTelemetry().getWrapper(), dashboard.getTelemetry());
        joinedTelemetry.update();

        // Initializing indicator
        indicator = new LightIndicatorSubsystem(hardwareMap);
        driveTrain.setDrivemoderobotcentric();
    }

    @Override
    public void start() {
        //driveTrain.start();//reset encoders
        driver = new GamepadEx(gamepad1); // This controls the movement of the robot
        operator = new GamepadEx(gamepad2); // This controls the movement of items on the robot
        runtime.reset();
    }

    @Override
    public void loop() {
        readInputs();
        DriverControls();
        if (driveTrain.drivemodefieldcentric) {
            DriverFieldCentricPresets();
        }
        OperatorControls();
        if (testMode) {
            MotorSetpointsAdjust();
        }
        driveTrain.loop();
        driveTrain.ShooterControlLoop();
        updateIndicator();
        updateTelemetry();

    }
    private void readInputs() {
        currentTime = getRuntime();
        driveTrain.setNow(currentTime);
        driver.readButtons();
        operator.readButtons();
        speed = driveTrain.Flywheel.getCorrectedVelocity();
    }
    private void DriverControls() {
        // ===== Driver Controls =====
        if (driveTrain.manual_FC_turning) {
            driveTrain.headingCorrection = driver.getRightX();
        }
        // - Allow Driver to toggle between robot/fieldcentric controls
        if(driver.wasJustPressed((GamepadKeys.Button.Y))) {
            if (driveTrain.drivemodefieldcentric) {
                driveTrain.setDrivemoderobotcentric();
            } else {
                driveTrain.setDrivemodefieldcentric();
                driveTrain.manual_FC_turning = true;
            }
        }
        if (driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 1.0) {
            driverSpeedRatio = 0.8;
        } else {
            driverSpeedRatio = Constants2027.DRIVER_SPEED_RATIO;
        }
        // ENABLE OPERATOR DRIVING CONTROLS
        if (operator.wasJustReleased(GamepadKeys.Button.BACK) && testMode){
            operatorDriving = !operatorDriving;
        }

        // Driver/Operator driving Controls
        if (operatorDriving) {
            double softLY = operator.getLeftY() * Math.abs(operator.getLeftY()) * Constants2027.OPER_SPEED_RATIO;
            double softLX = operator.getLeftX() * Math.abs(operator.getLeftX()) * Constants2027.OPER_SPEED_RATIO;
            driveTrain.drive(softLY, -softLX);
            double softRX = operator.getRightX() * Math.abs(operator.getRightX()) * Constants2027.OPER_SPEED_RATIO;
            driveTrain.turn(softRX);
        } else {
            driveTrain.drive(driver.getLeftY() * driverSpeedRatio, -driver.getLeftX() * driverSpeedRatio);
            driveTrain.turn(driver.getRightX() * driverSpeedRatio);
        }
        if (driver.wasJustPressed(GamepadKeys.Button.DPAD_UP) || driveTrain.autoEnabled) {
            driveTrain.autoEnabled = true;
            driveTrain.setDrivemodefieldcentric();
            if (driveTrain.goto_xy(driveTrain.Location_Close_Shot,driveTrain.dmRough) ||
                    Math.abs(driver.getLeftX()) > 0 ||
                    Math.abs(driver.getLeftY()) > 0 ||
                    Math.abs(driver.getRightX()) > 0) {
                driveTrain.setDrivemoderobotcentric();
                driveTrain.drive(0,0);
                driveTrain.autoEnabled = false;
            }
        }
    }
    private void DriverFieldCentricPresets() {
        //===============driving presets======================

        //small  red shooting position
        if(driver.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            driveTrain.setFacing(Constants2027.REDSMALL_SHOOT);
        }

        //small blue shooting position
//      dont have a constant angle yet
        if(driver.isDown(GamepadKeys.Button.LEFT_BUMPER)){
            driveTrain.setFacing(Constants2027.BLUESMALL_SHOOT);
        }

//        //big red shooting position
        if(driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)>= Constants2027.TRIGGER_TOLERANCE)  {
            driveTrain.setFacing(Constants2027.REDBIG_SHOOT);
        }
        //big blue shooting position
        if(driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)>= Constants2027.TRIGGER_TOLERANCE) {
            driveTrain.setFacing(Constants2027.BLUEBIG_SHOOT);
        }
        //Select N, S, E, W
//        if (driver.getRightX() <= -Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.WEST); // west
//        } else if (driver.getRightX() >= Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.EAST); // east
//        } else if (driver.getRightY() >= Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.SOUTH); // south
//        } else if (driver.getRightY() <= -Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.NORTH); // north
//        }
//
//// Select Diagonal Directions (NE,SE,SW,NW)
//        if (driver.getRightX() > Constants2.JOYSTICK_TOLERANCE & driver.getRightY() < -Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.NORTH_EAST); // north east
//        } else if (driver.getRightX() < -Constants2.JOYSTICK_TOLERANCE & driver.getRightY() < -Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.NORTH_WEST); // north west
//        } else if (driver.getRightX() < -Constants2.JOYSTICK_TOLERANCE & driver.getRightY() > Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.SOUTH_WEST); // south west
//        } else if (driver.getRightX() > Constants2.JOYSTICK_TOLERANCE & driver.getRightY() > Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.SOUTH_EAST); // south east
//        }


    }
    private void OperatorControls() {

        // =========  Operator controls ===========
        // Set shooting mode
        if (operator.isDown(GamepadKeys.Button.A)) {

        } else if (operator.isDown(GamepadKeys.Button.DPAD_DOWN)) {

        } else if (operator.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {

        } else if (operator.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)>= Constants2027.TRIGGER_TOLERANCE) {

        } else if (operator.isDown(GamepadKeys.Button.LEFT_BUMPER)) {

        } else if (operator.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)>= Constants2027.TRIGGER_TOLERANCE) {

        }else if (operator.isDown(GamepadKeys.Button.B)) {

        }else if (operator.isDown(GamepadKeys.Button.DPAD_LEFT)) {

        }else if (operator.isDown(GamepadKeys.Button.Y)) {

        }else {

        }

    }
    private void MotorSetpointsAdjust() {
        // - SP CONFIG FOR TESTING
        if (operator.isDown(GamepadKeys.Button.DPAD_RIGHT) && testMode){
            spMode = "Intake FWD";
            if (operator.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                Constants2027.INTAKE_SPD_PICKUP += 0.1;
            } else if (operator.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                Constants2027.INTAKE_SPD_PICKUP -= 0.1;
            }
        } else if (operator.isDown((GamepadKeys.Button.DPAD_UP)) && testMode){
            spMode = "Feeder REV";
            if (operator.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                Constants2027.FEEDER_SPD_CORRECTING += 0.1;
            } else if (operator.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                Constants2027.FEEDER_SPD_CORRECTING -= 0.1;
            }
        } else if (operator.isDown((GamepadKeys.Button.DPAD_LEFT)) && testMode){
            spMode = "FlyWheel REV";
            if (operator.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER) && testMode) {
                Constants2027.FLYWHEEL_SPD_REVERSE += 0.1;
            } else if (operator.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                Constants2027.FLYWHEEL_SPD_REVERSE -= 0.1;
            }
        }
    }
    private void updateTelemetry() {
        // Send data to telemetry
        joinedTelemetry.addData("Shooter Speed", speed); //telemetry shooter speed
        joinedTelemetry.addData("Xcor",driveTrain.getXPosition());
        joinedTelemetry.addData("Ycor",driveTrain.getYPosition());
        joinedTelemetry.addData("turnspeed", driveTrain.turnSpeed);
        joinedTelemetry.addData("heading", driveTrain.heading);
        joinedTelemetry.addData("-MOTOR-", "-POWER-");
        joinedTelemetry.addData("Setpoint Mode", spMode);
        joinedTelemetry.addData("Intake Forward", Constants2027.INTAKE_SPD_PICKUP);
        joinedTelemetry.addData("Feeder Reverse", Constants2027.FEEDER_SPD_CORRECTING);
        joinedTelemetry.addData("FlyWheel Reverse", Constants2027.FLYWHEEL_SPD_REVERSE);
        joinedTelemetry.update();
    }
    private void updateIndicator() {
        // INDICATOR CASES
        switch (driveTrain.CurrentShooterMode) {
            case shooterCORRECTING:
                indicator.setColor(Constants2027.RGB_Light.VIOLET);
                break;
            case shooterPICKUP:
                indicator.setColor(Constants2027.RGB_Light.YELLOW);
                break;
            case shooterSHOOTINGnear:
            case shooterSHOOTINGfarRedWithCorrection:
            case shooterSHOOTINGfarBlue:
            case shooterSHOOTINGfarRedNOcorrection:
            case shooterSHOOTINGnearNOcorrection:
                if (driveTrain.canLaunch(Constants2027.FLYWHEEL_SPD_FAR_BLUE)) {
                    indicator.setColor(Constants2027.RGB_Light.GREEN);
                } else {
                    indicator.setColor(Constants2027.RGB_Light.RED);
                }
                break;
            case shooterOFF:
            default:
                indicator.setColor(Constants2027.RGB_Light.WHITE);
        }
    }


}