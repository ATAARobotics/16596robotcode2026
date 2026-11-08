package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants2027;
@TeleOp(name="PredicamentTesting", group = "test")

public class PredicamentTesting extends OpMode {
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
    private Servo FlyWheelGate;
    double MotorSpeedThreshold = 0.05;
    double ColorThreshold = 0.05;
    boolean BallDetected;
    DistanceSensor distanceSensor;

    boolean BallOutDistance;


    @Override
    public void init() {
// ----------------------------- Motors ----------------------------------------------

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
// ----------------------------- Servos ----------------------------------------------
        MotorAdjuster = hardwareMap.get(Servo.class, "Motor Adjuster");
        FlyWheelGate = hardwareMap.get(Servo.class, "Fly Wheel Door");

// ----------------------------- Sensors ----------------------------------------------

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "Color Sensor");
        colorSensor.setGain(1);
        distanceSensor = hardwareMap.get(DistanceSensor.class, "Distance Sensor");

    }
    public double getDistance (){
        return distanceSensor.getDistance(DistanceUnit.CM);
    }

        @Override
        public void loop () {
            PollenShooter.setPower(PollenShooterSpeed);
            if (gamepad2.a) {
                PollenShooter.setPower(-1.0);
                NectarShooter.setPower(1.0);
            } else {
                PollenShooter.setPower(0.0);
                NectarShooter.setPower(0.0);
            }

//            NectarShooter.setPower(NectarShooterSpeed);
//            if (gamepad2.b) {
//                NectarShooter.setPower(-1.0);
//            } else {
//                NectarShooter.setPower(0.0);
//            }
// ----------------------------- Color Sensor Reader ----------------------------------------------

            telemetry.addData("Pollen Shooter Speed", PollenShooterSpeed);
            telemetry.addData("Nectar Shooter Speed", NectarShooterSpeed);

                NormalizedRGBA colors = colorSensor.getNormalizedColors(); //return 4 values

                float normRed, normBlue, normGreen;
                normRed = colors.red / colors.alpha;
                normBlue = colors.blue / colors.alpha;
                normGreen = colors.green / colors.alpha;

                telemetry.addData("normred", normRed);
                telemetry.addData("normgreen", normGreen);
                telemetry.addData("normblue", normBlue);

            telemetry.addData("red", colors.red);
            telemetry.addData("green", colors.green);
            telemetry.addData("blue", colors.blue);


// ----------------------------- Motor Adjuster ----------------------------------------------

             if (colors.red > ColorThreshold || colors.blue > ColorThreshold || colors.green > ColorThreshold){
                 BallDetected = true;
             }
             if ((colors.red > colors.green && colors.red > colors.blue && BallDetected) ||( colors.blue > colors.green && colors.blue > colors.red  && BallDetected)) {
                 MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_NECTAR);
            } else {
                 MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_POLLEN);
             }
            if (getDistance() < 0.0) {
                nectarDetected = false;
            }


// ----------------------------- Servo Gate ----------------------------------------------

            if (PollenShooterSpeed >= PollenShooterSpeed - MotorSpeedThreshold || PollenShooterSpeed >= PollenShooterSpeed + MotorSpeedThreshold){
                FlyWheelGate.setPosition(Constants2027.POLLEN_GATE_OPEN);
            } else {
                FlyWheelGate.setPosition(Constants2027.POLLEN_GATE_CLOSE);
            }

        }
    }
 /*else if (colors.red > 0.04 ) {

        MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_NECTAR);
                timer.reset();
nectarDetected = true;
        } else if (colors.blue > 0.03 ) {

        MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_NECTAR);
                timer.reset();
nectarDetected = true;
        } */

          /*  if ( colors.red < 0.0495 && colors.red > 0.015 &&
                    colors.green < 0.076 && colors.green > 0.295 &&
                    colors.blue < 0.026 && colors.blue > 0.01 ) {
                MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_POLLEN); // nothing valued
            }
            else if (colors.green <= 0.005 && colors.blue  <= 0.0035 && colors.red <= 0.0026){ //pollen values
                MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_POLLEN);// WAS POLLEN
            }

            else if(timer.milliseconds() <= 100 && nectarDetected){
                MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_NECTAR);//

            }else if(timer.milliseconds() >= 100 && nectarDetected) {
                MotorAdjuster.setPosition(Constants2027.MOTOR_ADJUSTER_NECTAR);//WAS POLLEN
                nectarDetected = false;

            }

            else {
            nectarDetected = true;
            timer.reset();

            } */