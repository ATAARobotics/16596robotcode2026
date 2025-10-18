package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

// The following libraries are special for LimeLight
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

// The following libraries are for FTControl
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.JoinedTelemetry;

@Config // need to use dashboard to change PID gains; comment out for competition
@TeleOp(name = "LimeLightTest")
public class LimeLightTest extends OpMode {
//test comment
    private Limelight3A limelight;
    private JoinedTelemetry joinedTelemetry;
    // Testing Indicator
    private LightIndicatorSubsystem indicator;

    @Override
    public void init() {

        // Limelight Stuff
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(50); // This sets how often we ask Limelight for data (100 times per second)
        // This tells Limelight to start looking!
        // Update Telemetry
        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        // Join them together
        joinedTelemetry = new JoinedTelemetry(telemetry,panelsTelemetry.getTelemetry().getWrapper(),dashboard.getTelemetry());
        joinedTelemetry.addLine("LimeLight Initialized");
        joinedTelemetry.update();
        // Indicator light
        indicator = new LightIndicatorSubsystem(hardwareMap);
    }
    @Override
    public void start() {

        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(Constants.LIMELIGHT_APRIL_TESTING);

    }
    @Override
    public void loop() {
        // Process Limelight data
        LLResult limeLightResults = limelight.getLatestResult();
        // Send data to telemetry
        if (limeLightResults != null && limeLightResults.isValid()) {
            indicator.setColor(Constants.RGB_Light.ON);
            joinedTelemetry.addData("Indicator Light", indicator.getColor());
            joinedTelemetry.addData("Target X", limeLightResults.getTx()); // How far left or right the target is (degrees)
            joinedTelemetry.addData("Target Y", limeLightResults.getTy()); // How far up or down the target is (degrees)
            joinedTelemetry.addData("Target Area", limeLightResults.getTa()); // How big the target looks (0%-100% of the image)
            joinedTelemetry.addData("Limelight Pipeline Index", limeLightResults.getPipelineIndex());
            joinedTelemetry.addData("Distance to Target inches", target_distance(limeLightResults));
        } else {
            indicator.setColor(Constants.RGB_Light.OFF);
            joinedTelemetry.addData("Limelight", "No Targets");
            joinedTelemetry.addData("Limelight Pipeline Index", "No Index");
        }
        // Update telemetry
        joinedTelemetry.update();

    }
    public double target_distance(LLResult limeLightResults) {

        double targetOffsetAngle_Vertical = limeLightResults.getTy();
        double angleToGoalDegree = Constants.LIMELIGHT_MOUNT_ANGLE_DEGREE + targetOffsetAngle_Vertical;
        double angleToGoalRadian = Math.toRadians(angleToGoalDegree);

        // https://docs.limelightvision.io/docs/docs-limelight/tutorials/tutorial-estimating-distance

        return (Constants.LIMELIGHT_GOAL_HEIGHT_INCHES - Constants.LIMELIGHT_LENS_HEIGHT_INCHES) / Math.tan(angleToGoalRadian);
    }

}
