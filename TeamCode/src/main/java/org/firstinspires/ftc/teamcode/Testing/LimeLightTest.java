package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
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
    private int count = 0;
    private double sum = 0.0;
    private GamepadEx controller1 = null;

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
        this.controller1 = new GamepadEx(gamepad1); //init the gamepad now sees the gamepad

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
            count = count + 1;
            sum = sum + limeLightResults.getTa();
            joinedTelemetry.addData("Indicator Light", indicator.getColor());
            joinedTelemetry.addData("Target X", limeLightResults.getTx()); // How far left or right the target is (degrees)
            joinedTelemetry.addData("Target Y", limeLightResults.getTy()); // How far up or down the target is (degrees)
            joinedTelemetry.addData("Target Area", limeLightResults.getTa()); // How big the target looks (0%-100% of the image)
            joinedTelemetry.addData("Limelight Pipeline Index", limeLightResults.getPipelineIndex());
            joinedTelemetry.addData("Distance to Target inches", getTargetDistanceCalculated(limeLightResults));
            joinedTelemetry.addData("Distance to Target Area in inches",getDistanceFromTarget(limeLightResults));
            joinedTelemetry.addData("Distance to Target Area in inches AVERAGE", sum/count);
        } else {
            indicator.setColor(Constants.RGB_Light.OFF);
            joinedTelemetry.addData("Limelight", "No Targets");
            joinedTelemetry.addData("Limelight Pipeline Index", "No Index");
            sum = 0.0;
            count = 0;
        }
        // Update telemetry
        joinedTelemetry.update();
        if (controller1.getButton(GamepadKeys.Button.X)){

        }


    }
    public double getTargetDistanceCalculated(LLResult limeLightResults) {

        double targetOffsetAngle_Vertical = limeLightResults.getTy();
        double angleToGoalDegree = Constants.LIMELIGHT_MOUNT_ANGLE_DEGREE + targetOffsetAngle_Vertical;
        double angleToGoalRadian = Math.toRadians(angleToGoalDegree);

        // https://docs.limelightvision.io/docs/docs-limelight/tutorials/tutorial-estimating-distance

        return (Constants.LIMELIGHT_GOAL_HEIGHT_INCHES - Constants.LIMELIGHT_LENS_HEIGHT_INCHES) / Math.tan(angleToGoalRadian);
    }

    public double getDistanceFromTarget(LLResult limeLightResults){
        double scale = 5444.216;             //calculated from fit my curve
        double distance = scale / Math.sqrt(limeLightResults.getTa());      //distance is in inches
        return distance;


    }
}
