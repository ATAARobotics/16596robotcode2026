package org.firstinspires.ftc.teamcode.Utils;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import java.io.PrintStream;


@TeleOp
public class ColorCalibrate extends OpMode {

    private PrintStream DataOutput;
    double red_max = 0.0;
    double red_min = 1000.0;
    double blue_max = 0.0;
    double blue_min = 1000.0;
    int counter = 0;
    int gain = 0;
    NormalizedColorSensor colorSensor;//control hub I2C Bus 1
    NormalizedRGBA colors;
    public String filename="rgb_calibrate.csv";

    @Override
    public void init() {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "Color Sensor");
        filename = String.format("%s/FIRST/data/%s",Environment.getExternalStorageDirectory().getPath(), filename);
        DataOutput = new PrintStream(filename);
    }


    @Override
    public void loop() {
        while (gain <= 10) {
            while (counter <= 1000) {
                colors = colorSensor.getNormalizedColors();
                if (colors.red > red_max) {
                    red_max = colors.red
                }
                if (colors.red < red_min) {
                    red_min = colors.red;
                }
                if (colors.blue > blue_max) {
                    red_max = colors.red;
                }
                if (colors.blue < blue_min) {
                    blue_min = colors.blue;
                }
                if (colors.red > red_max) {
                    red_max = colors.red
                }
                if (colors.red < red_min) {
                    red_min = colors.red;
                }
                counter += 1;
            }
            // write data
            // increment gain
            gain += 1;

        }
        // change ball color


    }
    public void stopLog() {
        if (this.logging) {
            this.logWriter.flush();
            this.logWriter.close();
        }
    }

}
/// Filedate format
/// color_gain,color,min,max