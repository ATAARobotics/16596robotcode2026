package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants;

// The following libraries are special for LimeLight
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

// The following libraries are for FTControl
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.JoinedTelemetry;

@Config // need to use dashboard to change PID gains; comment out for competition
@TeleOp(name = "LimeLightTest")
public class LimeLightTest extends OpMode {

    private Limelight3A limelight;


    @Override
    public void init() {
//        telemetry = new CAITelemetry(telemetry);
//        ((CAITelemetry) telemetry).setDashboardEnabled(false);
//        telemetry.addData("Status", "Initializing");
//        telemetry.update();
        // Limelight Stuff
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        // This tells Limelight to start looking!
        // Update Telemetry
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry,dashboard.getTelemetry());
        telemetry.addData("Status", "LimeLight Initialized");
        telemetry.update();
    }
    public void start() {

        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(Constants.LIMELIGHT_APRIL_TAG_BLUE);

    }
    @Override
    public void loop() {
// Process Limelight data
        LLResult limeLightResults = limelight.getLatestResult();

        // Send data to telemetry
        if (limeLightResults != null && limeLightResults.isValid()) {
            telemetry.addData("Target X", limeLightResults.getTx()); // How far left or right the target is (degrees)
            telemetry.addData("Target Y", limeLightResults.getTy()); // How far up or down the target is (degrees)
            telemetry.addData("Target Area", limeLightResults.getTa()); // How big the target looks (0%-100% of the image)
            telemetry.addData("Limelight Pipeline Index", limeLightResults.getPipelineIndex());
        } else {
            telemetry.addData("Limelight", "No Targets");
            telemetry.addData("Limelight Pipeline Index", limeLightResults.getPipelineIndex());
        }
        telemetry.update();

    }
}
