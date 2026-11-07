package org.firstinspires.ftc.teamcode.Subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.teamcode.Mechanisms.C2;

@Config
public class Mechanisms2027 {
    public HardwareMap hwMap;

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
        ShooterRight.setVeloCoefficients(C2.FLYWHEEL_KP, C2.FLYWHEEL_KI, C2.FLYWHEEL_KD);     //coefficients are. kp, ki, and kd

        ShooterLeft.setRunMode(Motor.RunMode.VelocityControl);
        ShooterLeft.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        ShooterLeft.setInverted(true);
        ShooterLeft.setVeloCoefficients(C2.FLYWHEEL_KP, C2.FLYWHEEL_KI, C2.FLYWHEEL_KD);

        Feeder.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        Feeder.setInverted(false);

        Intake.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        Intake.setInverted(true);


    }// end of init()

    public void start() {

    }

    public void loop() {

    }

    public boolean canLaunch(double launchSpeed, double FudgeFactor) {
        return ShooterRight.getCorrectedVelocity() >= (launchSpeed * C2.FLYWHEEL_MAX) + FudgeFactor;
    }

    public boolean canLaunch(double launchSpeed) {
        return ShooterRight.getCorrectedVelocity() >= (launchSpeed * C2.FLYWHEEL_MAX);
    }


    public enum ShooterMode {
        shooterSHOOTINGfarBlue, shooterSHOOTINGfarRedWithCorrection, shooterSHOOTINGfarRedNOcorrection, shooterSHOOTINGnear, shooterSHOOTINGnearNOcorrection, shooterPICKUP, shooterCORRECTING, shooterHOLDING, shooterOFF
    }

    public void shooterMotors(double flywheelSpeed, double intakeSpeed, double feederSpeed, double feed1Speed) {
        Feeder.set(feederSpeed);
        Intake.set(intakeSpeed);
        ShooterRight.set(flywheelSpeed);
        ShooterLeft.set(flywheelSpeed);
    }




}