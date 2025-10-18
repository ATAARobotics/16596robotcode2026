package org.firstinspires.ftc.teamcode.Subsystem;

import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants;

public class LightIndicator {
private final SimpleServo indicator;
private double Color;

public LightIndicator(HardwareMap hwMap) {
    this.indicator = new SimpleServo(hwMap, "indicator", 0.0, 1.0);
    this.init();

}
public void init(){
this.indicator.setPosition(Constants.RGB_Light.OFF);
this.Color = Constants.RGB_Light.OFF;

}

public void setColor(double Color){
    this.indicator.setPosition(Color);
    this.Color = Color;

}
public double getColor(){return this.Color;}


}

