package org.firstinspires.ftc.teamcode.Utils;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import java.io.FileNotFoundException;
import java.io.PrintStream;


@TeleOp(name="ColorCalibrate",group="b_config")
public class ColorCalibrate extends OpMode {

    private PrintStream DataOutput;
    double red_max = 0.0;
    double red_min = 1000.0;
    boolean red_done = false;
    boolean blue_done = false;

    double blue_max = 0.0;
    double blue_min = 1000.0;
    int counter = 0;
    int gain = 0;
    boolean filefound = false;
    NormalizedColorSensor colorSensor;//control hub I2C Bus 1
    NormalizedRGBA colors;
    public String filename="rgb_calibrate.csv";

    @Override
    public void init() {
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "Color Sensor");
        filename = String.format("%s/FIRST/data/%s",Environment.getExternalStorageDirectory().getPath(), filename);
        try{
            DataOutput = new PrintStream(filename);
            filefound = true;
        }
        catch (FileNotFoundException e) {
            filefound = false;
        }
    }


    @Override
    public void loop() {
        telemetry.addData("for red ball press","a");
        telemetry.addData("for blue ball press","b");
        telemetry.addData("for yellow ball press","x");
        telemetry.addData("for nothing press","y");
        telemetry.addData("RED max Value", red_max);
        telemetry.addData("RED MIN Value", red_min);
        telemetry.addData("BLUE max Value", blue_min);
        telemetry.addData("BLUE MIN Value", blue_max);



        if (gamepad1.a && !red_done){
                telemetry.addData("RED Ball testing ", "In progress keep holding button");
                colors = colorSensor.getNormalizedColors();
                if (colors.red > red_max) {
                    red_max = colors.red;
                }
                if (colors.red < red_min) {
                    red_min = colors.red;
                }
                if (colors.blue > red_max) {
                    red_max = colors.red;
                }
                if (colors.red < red_min) {
                    red_min = colors.red;
                }
                if (colors.red > red_max) {
                    red_max = colors.red;
                }
                if (colors.red < red_min) {
                    red_min = colors.red;

        }
        }
if (gamepad1.b ) {
            telemetry.addData("BLUE Ball testing ", "In progress keep holding button");
            colors = colorSensor.getNormalizedColors();
            if (colors.blue > blue_max) {
                blue_max = colors.blue;
            }
            if (colors.blue < blue_min) {
                blue_min = colors.blue;
            }
            if (colors.blue > blue_max) {
                blue_max = colors.blue;
            }
            if (colors.blue < blue_min) {
                blue_min = colors.blue;
            }
            if (colors.blue > blue_max) {
                blue_max = colors.blue;
            }
            if (colors.blue < blue_min) {
                blue_min = colors.blue;
            }
}

    }
    public void stopLog() {
        if (filefound) {
            DataOutput.flush();
            DataOutput.close();
        }
    }

}
/// Filedate format
/// color_gain,color,min,max