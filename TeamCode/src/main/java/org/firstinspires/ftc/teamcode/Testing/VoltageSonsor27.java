package org.firstinspires.ftc.teamcode.Testing;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.Telemetry;


@Config
@TeleOp(name = "VoltageSensor27", group = "Testing")
public class VoltageSonsor27 extends OpMode{

    HardwareMap hwMap;
    public VoltageSensor controlHubVoltageSensor;
    double currentVoltage;
    private Telemetry telemetry;
    @Override
    public void init() {
        VoltageSensor controlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
        controlHubVoltageSensor = hwMap.voltageSensor.iterator().next();

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
