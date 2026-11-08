package org.firstinspires.ftc.teamcode.Subsystem;

import static org.firstinspires.ftc.teamcode.Subsystem.Mechanisms2027.Mode.SHOOTING;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Utils.Constant;

@Config
public class Mechanisms2027 {
    private HardwareMap hwMap;
    private static Mode ShooterMode = Mode.IDLEROBOT;
    //========== Motors =============================================
    private MotorEx ShooterRight; //port 1 -expansion hub
    private MotorEx ShooterLeft;
    private MotorEx Feeder; //port 0-expansion hub
    private Motor Intake; //port 1-expansion hub

    //========== Servos =============================================
    private Servo ShooterNectarLift;
    private Servo ShooterRamp;
    private Servo ShooterStopper;
    //========== Sensors ============================================
    private NormalizedColorSensor ShooterColorSensor;
    private DistanceSensor ShooterProximity;
    //========== Time ===============================================
    private double now = 0.0;
    private double shooterModeStartTime = 0.0;



    public Mechanisms2027(HardwareMap hwMap) {
        this.hwMap = hwMap;
        //================================ Motors ============================================
        assignMotors();
        //================================ Servos ============================================
        assignServos();
        //================================ Sensors ============================================
        assignSensors();
    }
    public void init() {
        initializeMotors();
    }// end of init()

    public void start() {

    }

    public void loop() {
        switch (ShooterMode){
            case PICKUP: // Intaking
                operatorMotors(Constant.SHOOTER_SPEED_HOLDING, Constant.INTAKE_SPEED_PICKUP, Constant.FEEDER_SPEED_PICKUP);
                break;
            case SHOOTING: // Shooting
                if (!ShooterModeActiveForMoreThan(Constant.CORRECTION_DELAY)) { // input delay seconds
                    operatorMotors(Constant.SHOOTER_SPEED_HOLDING, Constant.INTAKE_SPEED_HOLDING, Constant.FEEDER_SPEED_CORRECTING);
                } else {
                    operatorMotors(Constant.SHOOTER_SPEED_SHOOTING, Constant.INTAKE_SPEED_HOLDING, Constant.FEEDER_SPEED_CORRECTING);
                    if (canLaunch(Constant.SHOOTER_SPEED_SHOOTING)) {
                        operatorMotors(Constant.SHOOTER_SPEED_SHOOTING, Constant.INTAKE_SPEED_PICKUP, Constant.FEEDER_SPEED_PICKUP);
                    }
                }
                break;
            case IDLEROBOT:// Off
            default:
                operatorMotors(0,0,0);
                break;
        }
    }
    // canLaunch Overloading
    public boolean canLaunch(double launchSpeed) {
        return this.canLaunch(launchSpeed,0.0);
    }
    public boolean canLaunch(double launchSpeed, double FudgeFactor) {
        return this.ShooterRight.getCorrectedVelocity() >= (launchSpeed * Constant.SHOOTER_MAX) + FudgeFactor;
    }

    //========== Sensor Functions ===================================

    public boolean detectNectar () {
        NormalizedRGBA colors = this.ShooterColorSensor.getNormalizedColors();
        return (((colors.red > colors.green
                && colors.red > colors.blue)
                || (colors.blue > colors.green
                && colors.blue > colors.red))
                && (colors.red > Constant.COLOR_THRESHOLD
                || colors.blue > Constant.COLOR_THRESHOLD
                || colors.green > Constant.COLOR_THRESHOLD));
    }

    //========== Class Get Sections =================================
    public double getDistance (){
        return this.ShooterProximity.getDistance(DistanceUnit.CM);
    }
    public Mode getMode(){return this.ShooterMode;}
    //========== Class Set Section ==================================
    public void setMode(Mode mode){
        if (mode != ShooterMode) {
            ShooterMode = mode;
            onShooterModeChange();
            }
        }
    public void setNow(double nowSeconds) {
        this.now = nowSeconds;
    }
    //========== Class Types ========================================
    public enum Mode {
        PICKUP,
        SHOOTING,
        IDLEROBOT
    }
    //========== Class Functions ====================================
    private void onShooterModeChange() {
        this.shooterModeStartTime = this.now;
    }
    private boolean ShooterModeActiveForMoreThan(double seconds) {
        return (this.now - this.shooterModeStartTime) >= seconds;
    }
    private void operatorMotors(double shooterSpeed, double intakeSpeed, double feederSpeed){
        this.Feeder.set(feederSpeed);
        this.Intake.set(intakeSpeed);
        this.ShooterRight.set(shooterSpeed);
        this.ShooterLeft.set(shooterSpeed);
    }
    //========== Assign functions ===================================
    private void assignMotors(){
        // see https://docs.ftclib.org/ftclib/features/hardware/motors -- may have to add gobilda type
        this.ShooterRight = new MotorEx(this.hwMap, "ShooterRight");
        // see https://docs.ftclib.org/ftclib/features/hardware/motors -- may have to add gobilda type
        this.ShooterLeft = new MotorEx(this.hwMap, "ShooterLeft");
        this.Feeder = new MotorEx(this.hwMap, "Feeder");
        this.Intake = new MotorEx(this.hwMap, "Intake");
    }
    private void assignServos() {
        this.ShooterNectarLift = this.hwMap.get(Servo.class, "ShooterNectarLift");
        this.ShooterRamp = this.hwMap.get(Servo.class, "ShooterRamp");
        this.ShooterStopper = this.hwMap.get(Servo.class, "ShooterStopper");
    }
    private void assignSensors(){
        ShooterColorSensor = hwMap.get(NormalizedColorSensor.class, "ShooterColorSensor");
        ShooterProximity = hwMap.get(DistanceSensor.class, "ShooterProximity");
    }
    // Initialize functions
    private void  initializeMotors(){
        // Shooters
        this.ShooterRight.setRunMode(Motor.RunMode.VelocityControl);
        this.ShooterRight.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        this.ShooterRight.setInverted(true);
        //coefficients are: kp, ki, and kd
        this.ShooterRight.setVeloCoefficients(Constant.SHOOTER_KP, Constant.SHOOTER_KI, Constant.SHOOTER_KD);

        this.ShooterLeft.setRunMode(Motor.RunMode.VelocityControl);
        this.ShooterLeft.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        this.ShooterLeft.setInverted(false);
        //coefficients are: kp, ki, and kd
        this.ShooterLeft.setVeloCoefficients(Constant.SHOOTER_KP, Constant.SHOOTER_KI, Constant.SHOOTER_KD);
        // Feeder
        this.Feeder.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        this.Feeder.setInverted(false);
        // Intake
        this.Intake.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        this.Intake.setInverted(true);
    }
}