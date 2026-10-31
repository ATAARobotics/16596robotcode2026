package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ColorSensor {

    NormalizedColorSensor colorSensor;

    public enum DetectedColor {
        PINK,
        BLUE,
        GREEN,
        PURPLE,
        YELLOW,
        UNKNOWN,
    }

    public void init(HardwareMap hwMap){
        colorSensor = hwMap.get(NormalizedColorSensor.class, "sensor_color_distance");
        colorSensor.setGain(4);
    }

    public DetectedColor getDetectedColor(Telemetry telemetry){
        NormalizedRGBA colors = colorSensor.getNormalizedColors(); //return 4 values

        float normRed, normBlue, normGreen;
        normRed = colors.red / colors.alpha;
        normBlue = colors.blue / colors.alpha;
        normGreen = colors.green / colors.alpha;

        //telemetry.addData("red", normRed);
        //telemetry.addData("green", normGreen);
        // telemetry.addData("blue", normBlue);

        //todo add if statment for spesific colors
        /*
        red,green,blue
        RED = 0.8, 0.3, 0.3
        GREEN = 0.2, 0.6, 0.2
        BLUE =0.1, 0.4, 0.6
        PURPLE =245
        YELLOW =671

         */

        if (normRed > 0.8 && normGreen > 0.3 && normBlue > 0.3 ){
            return  DetectedColor.PINK;

        }

        else if (normRed > 0.2 && normGreen > 0.4 && normBlue > 0.5 ){
            return  DetectedColor.PURPLE;
        }

        else if (normRed > 0.6 && normGreen > 0.7 && normBlue > 0.1 ){
            return  DetectedColor.YELLOW;
        }

        else if (normRed > 0.2 && normGreen > 0.6 && normBlue > 0.2 ){
            return  DetectedColor.GREEN;
        }

        else if (normRed > 0.1 && normGreen > 0.4 && normBlue > 0.6 ){
            return  DetectedColor.BLUE;
        }

        else {
            return  DetectedColor.UNKNOWN;
        }

    }
}


