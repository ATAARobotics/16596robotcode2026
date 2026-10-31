package org.firstinspires.ftc.teamcode.Testing.SensorTesting;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;


import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
@TeleOp
public class BallIntakeCountingBlue extends OpMode {
    private DistanceSensor distance;

    double ballcount;
    // Keep track of the historical distance and time
    private double lastDistance = 0.0;
    private long lastTimestamp = 0L;
    DcMotor intake;




    public enum DetectedColor {
        PINK,
        BLUE,
        GREEN,
        PURPLE,
        YELLOW,
        UNKNOWN,
    }

    NormalizedColorSensor colorSensor;


    @Override
    public void init() {
        distance = hardwareMap.get(DistanceSensor.class, "sensor_distance");
        intake = hardwareMap.get(DcMotor.class, "intake");
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "color");
        colorSensor.setGain(8);

    }

    public double getDistance() {
        return distance.getDistance(DistanceUnit.CM);


    }



    @Override
    public void loop() {

        NormalizedRGBA colors = colorSensor.getNormalizedColors(); //return 4 values
        float normRed, normBlue, normGreen;
        normRed = colors.red / colors.alpha;
        normBlue = colors.blue / colors.alpha;
        normGreen = colors.green / colors.alpha;

        // red 0.071

        telemetry.addData("Distance", getDistance());
        telemetry.addData("Ball Count", ballcount);
//intake.setPower(1.0);

        double currentDistance = distance.getDistance(DistanceUnit.CM);
        long currentTimestamp = System.currentTimeMillis();

// 2. Calculate the time gap since the last loop reading
        long timePassed = currentTimestamp - lastTimestamp; // Time in milliseconds
        long shortTimeLimit = 1000; // 1000 milliseconds = 1-second window

// 3. The IF statement checking time and the two distance ranges
        if (timePassed <= shortTimeLimit &&
                (lastDistance >= 22.0 && lastDistance <= 100.0) &&   // Starting range (e.g. Far away)
                (currentDistance >= 1.0 && currentDistance <= 21.9)  // Ending range (e.g. Suddenly very close)
        ) {
            ballcount = ballcount + 1;
            telemetry.addLine("QUICK MOTION DETECTED!");
        }

        lastDistance = currentDistance;
        lastTimestamp = currentTimestamp;


        telemetry.addData("red", normRed);
        telemetry.addData("green", normGreen);
        telemetry.addData("blue", normBlue);

        if (normRed > 0.085){
            intake.setPower(1.0);
        }

        else {
            intake.setPower(-0.3);
        }

    }
}