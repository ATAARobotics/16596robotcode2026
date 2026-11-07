package org.firstinspires.ftc.teamcode.Subsystem;

import static org.firstinspires.ftc.teamcode.Subsystem.Mechanisms2027.Mode.MODE_2;

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
    public HardwareMap hwMap;
    public static Mode ShooterMode = Mode.MODE_3;
    //================================ Motors ============================================
    public final MotorEx ShooterRight; //port 1 -expansion hub
    public final MotorEx ShooterLeft;
    public final MotorEx Feeder; //port 0-expansion hub
    public final Motor Intake; //port 1-expansion hub

    //================================ Servos ============================================
    public Servo ShooterNectarLift;
    public Servo ShooterRamp;
    public Servo ShooterStopper;


    //================================ Sensors ============================================
    public NormalizedColorSensor ShooterColorSensor;
    public DistanceSensor ShooterProximity;

    boolean BallDetected;
    Double ColorThreshold = 0.05;
    private double now = 0.0;
    private double shooterModeStartTime = 0.0;



    public Mechanisms2027(HardwareMap hwMap) {
        //================================ Motors ============================================

        ShooterRight = new MotorEx(hwMap, "ShooterRight");// see https://docs.ftclib.org/ftclib/features/hardware/motors -- may have to add gobilda type
        ShooterLeft = new MotorEx(hwMap, "ShooterLeft");// see https://docs.ftclib.org/ftclib/features/hardware/motors -- may have to add gobilda type
        Feeder = new MotorEx(hwMap, "Feeder");
        Intake = new MotorEx(hwMap, "Intake");

        //================================ Servos ============================================


        ShooterNectarLift = hwMap.get(Servo.class, "ShooterNectarLift");
        ShooterRamp = hwMap.get(Servo.class, "ShooterRamp");
        ShooterStopper = hwMap.get(Servo.class, "ShooterStopper");

        //================================ Sensors ============================================

        ShooterColorSensor = hwMap.get(NormalizedColorSensor.class, "ShooterColorSensor");
        ShooterProximity = hwMap.get(DistanceSensor.class, "ShooterProximity");



    }

    public void init() {

        ShooterRight.setRunMode(Motor.RunMode.VelocityControl);
        ShooterRight.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        ShooterRight.setInverted(true);
        ShooterRight.setVeloCoefficients(Constant.SHOOTER_KP, Constant.SHOOTER_KI, Constant.SHOOTER_KD);     //coefficients are. kp, ki, and kd

        ShooterLeft.setRunMode(Motor.RunMode.VelocityControl);
        ShooterLeft.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        ShooterLeft.setInverted(true);
        ShooterLeft.setVeloCoefficients(Constant.SHOOTER_KP, Constant.SHOOTER_KI, Constant.SHOOTER_KD);

        Feeder.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        Feeder.setInverted(false);

        Intake.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        Intake.setInverted(true);


    }// end of init()

    public void start() {

    }

    public void loop() {
        switch (ShooterMode){
            case MODE_1: //Intaking
                operatorMotors(Constant.SHOOTER_SPEED_HOLDING, Constant.INTAKE_SPEED_PICKUP, Constant.FEEDER_SPEED_PICKUP);
                break;


            case MODE_2: //Shooting
                if (!ShooterModeActiveForMoreThan(Constant.CORRECTION_DELAY)) { // input delay seconds
                    operatorMotors(Constant.SHOOTER_SPEED_HOLDING, Constant.INTAKE_SPEED_HOLDING, Constant.FEEDER_SPEED_CORRECTING);
                } else {
                    operatorMotors(Constant.SHOOTER_SPEED_SHOOTING, Constant.INTAKE_SPEED_HOLDING, Constant.FEEDER_SPEED_CORRECTING);
                    if (canLaunch(Constant.SHOOTER_SPEED_SHOOTING)) {
                        operatorMotors(Constant.SHOOTER_SPEED_SHOOTING, Constant.INTAKE_SPEED_PICKUP, Constant.FEEDER_SPEED_PICKUP);
                    }
                }
                break;

            case MODE_3://Off
            default:
                operatorMotors(0,0,0);
                break;
        }
    }

    public boolean canLaunch(double launchSpeed, double FudgeFactor) {
        return ShooterRight.getCorrectedVelocity() >= (launchSpeed * Constant.SHOOTER_MAX) + FudgeFactor;
    }

    public boolean canLaunch(double launchSpeed) {
        return this.canLaunch(launchSpeed,0.0);
    }

    //================================ Sensor Functions ============================================

    public boolean detectNectar () {
        NormalizedRGBA colors = ShooterColorSensor.getNormalizedColors();
        return (((colors.red > colors.green && colors.red > colors.blue) || (colors.blue > colors.green && colors.blue > colors.red)) && (colors.red > ColorThreshold || colors.blue > ColorThreshold || colors.green > ColorThreshold));
    }

    public double getDistance (){
        return ShooterProximity.getDistance(DistanceUnit.CM);
    }

    public Mode getMode(){return ShooterMode;}

    public void setMode(Mode mode){
        if (mode != ShooterMode) {
            ShooterMode = mode;
            onShooterModeChange();
            }
        }

    // Class Types
    public enum Mode {
        MODE_1,
        MODE_2,
        MODE_3
    }
    public void setNow(double nowSeconds) {
        now = nowSeconds;
    }
    private void onShooterModeChange() {
        shooterModeStartTime = now;

    }
    public boolean ShooterModeActiveForMoreThan(double seconds) {
        return (now - shooterModeStartTime) >= seconds;
    }


    public void operatorMotors(double flywheelSpeed, double intakeSpeed, double feederSpeed){
        Feeder.set(feederSpeed);
        Intake.set(intakeSpeed);
        ShooterRight.set(flywheelSpeed);
        ShooterLeft.set(flywheelSpeed);
    }
}