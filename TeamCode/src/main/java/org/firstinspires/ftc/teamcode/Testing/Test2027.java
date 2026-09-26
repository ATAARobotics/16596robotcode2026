package org.firstinspires.ftc.teamcode.Testing;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
@TeleOp(name = "Test2027", group = "Testing")

public class Test2027 extends OpMode {
    // Inside your OpMode initialization:
// Inside your OpMode initialization:
    HardwareMap hwMap;
    public VoltageSensor controlHubVoltageSensor;
    double currentVoltage;
    private Telemetry telemetry;
    @Override
    public void init() {
        VoltageSensor controlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "VoltageSensor");
        controlHubVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        // To read the voltage value during execution:
        this.telemetry = FtcDashboard.getInstance().getTelemetry();
    }

    @Override
    public void loop() {

        // To read the voltage value during execution:
        currentVoltage = controlHubVoltageSensor.getVoltage();
        telemetry.addData("currentVoltage", currentVoltage);
        this.telemetry.update();
    }

}
