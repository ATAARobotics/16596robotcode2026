package org.firstinspires.ftc.teamcode.Subsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants;

public class LightIndicatorSubsystem extends SubsystemBase {
    private final SimpleServo indicator;
    private double Color;

    public LightIndicatorSubsystem(HardwareMap hwMap) {
        // Map the servo
        this.indicator = new SimpleServo(hwMap, "indicator", 0.0, 1.0);
        // Make sure there is a default setting
        this.indicator.setPosition(Constants.RGB_Light.OFF);
        this.Color = Constants.RGB_Light.OFF;
    }

    public void setColor(double Color){
        // Valid color selection
        if (Color >= 0.0 && Color <= 1.0) {
            this.indicator.setPosition(Color);
            this.Color = Color;
            return;
        }
        // Check for invalid color selections
        // > 1.0 default to On or White
        // < 0.0 default to Off or Black
        if (Color > 1.0){
            this.indicator.setPosition(Constants.RGB_Light.ON);
            this.Color = Constants.RGB_Light.ON;
            return;
        }
        this.indicator.setPosition(Constants.RGB_Light.OFF);
        this.Color = Constants.RGB_Light.OFF;
    }
    public double getColor(){return this.Color;}
}

