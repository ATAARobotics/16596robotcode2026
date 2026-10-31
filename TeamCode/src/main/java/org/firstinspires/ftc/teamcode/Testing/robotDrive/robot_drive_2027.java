package org.firstinspires.ftc.teamcode.Testing.robotDrive;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants2027;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrain2027;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;


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

    private DcMotor PollenShooter; // expansion hub motoe port 0
    double PollenShooterSpeed;
    double PollenShooterReverseSpeed;
    private DcMotor NectarShooter;// expansion hub motor port 1
    double NectarShooterSpeed;
    double NectarShooterReverseSpeed;
    private Servo MotorAdjuster; //expansion hub servo port 0
    NormalizedColorSensor colorSensor;//control hub I2C Bus 1
    ElapsedTime timer = new ElapsedTime();
    boolean nectarDetected = false;

    public enum DetectedColor {
        PINK,
        BLUE,
        GREEN,
        PURPLE,
        YELLOW,
        UNKNOWN,
    }

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

        PollenShooter = hardwareMap.get(DcMotor.class, "Pollen Shooter");
        PollenShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        PollenShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PollenShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        PollenShooter.setPower(1.0);

        NectarShooter = hardwareMap.get(DcMotor.class, "Nectar Shooter");
        NectarShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        NectarShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        NectarShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        NectarShooter.setPower(1.0);

        MotorAdjuster = hardwareMap.get(Servo.class, "Motor Adjuster");

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "Color Sensor");
        colorSensor.setGain(4);

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

        NormalizedRGBA colors = colorSensor.getNormalizedColors(); //return 4 values

        float normRed, normBlue, normGreen;
        normRed = colors.red / colors.alpha;
        normBlue = colors.blue / colors.alpha;
        normGreen = colors.green / colors.alpha;

        if(timer.milliseconds() <= 1000 && nectarDetected){
            MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_NECTAR);
        }else if(timer.milliseconds() >= 1000 && nectarDetected) {
            MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_POLLEN);
            nectarDetected = false;
        } else if (normRed > 0.04 && normGreen < 0.07 || normBlue > 0.05 && normGreen < 0.07) {
            MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_NECTAR);
            timer.reset();
            nectarDetected = true;
        }else {
            MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_POLLEN);
        }

        telemetry.addData("red", normRed);
        telemetry.addData("green", normGreen);
        telemetry.addData("blue", normBlue);

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
        speed = driveTrain.PollenShooter.getCorrectedVelocity();
        speed = driveTrain.NectarShooter.getCorrectedVelocity();
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
    }
    private void OperatorControls() {

       // could also be in loop, but idk
        PollenShooter.setPower(PollenShooterSpeed);
        if (gamepad2.a) {
            PollenShooter.setPower(1.0);
            NectarShooter.setPower(-1.0);
        } else {
            PollenShooter.setPower(0.0);
            NectarShooter.setPower(0.0);
        }
        // =========  Operator controls ===========
        // Set shooting mode

        if (gamepad2.a){
            driveTrain.CurrentShooterMode = DriveTrain2027.ShooterMode.shooterSHOOTINGnearNOcorrection;
        } else if (gamepad2.b) {

        } else if (gamepad2.x){

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
        telemetry.addData("Pollen Shooter Speed", PollenShooterSpeed);
        telemetry.addData("Nectar Shooter Speed", NectarShooterSpeed);

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