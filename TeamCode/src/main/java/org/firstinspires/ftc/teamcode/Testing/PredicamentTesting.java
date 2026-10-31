package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants2027;

@TeleOp
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
        colorSensor.setGain(4);
    }

        @Override
        public void loop () {
            PollenShooter.setPower(PollenShooterSpeed);
            if (gamepad2.a) {
                PollenShooter.setPower(1.0);
                NectarShooter.setPower(-1.0);
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

            telemetry.addData("Pollen Shooter Speed", PollenShooterSpeed);
            telemetry.addData("Nectar Shooter Speed", NectarShooterSpeed);

                NormalizedRGBA colors = colorSensor.getNormalizedColors(); //return 4 values

                float normRed, normBlue, normGreen;
                normRed = colors.red / colors.alpha;
                normBlue = colors.blue / colors.alpha;
                normGreen = colors.green / colors.alpha;

                telemetry.addData("red", normRed);
                telemetry.addData("green", normGreen);
                telemetry.addData("blue", normBlue);


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

            if (PollenShooterSpeed >= PollenShooterSpeed - MotorSpeedThreshold || PollenShooterSpeed >= PollenShooterSpeed + MotorSpeedThreshold){
                FlyWheelGate.setPosition(Constants2027.POLLEN_GATE_OPEN);
            } else {
                FlyWheelGate.setPosition(Constants2027.POLLEN_GATE_CLOSE);
            }
        }
    }