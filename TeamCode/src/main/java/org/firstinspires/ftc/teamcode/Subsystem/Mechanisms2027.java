package org.firstinspires.ftc.teamcode.Subsystem;

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
    private Mode mode;
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
        switch (mode){
            case MODE_1:
                break;
            case MODE_2:
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

    public Mode getMode(){return this.mode;}
    public void setMode(Mode mode){this.mode = mode;}

    // Class Types
    public enum Mode {
        MODE_1,
        MODE_2
    }
}