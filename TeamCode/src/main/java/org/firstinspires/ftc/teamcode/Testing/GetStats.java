package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.GoBildaPinpointDriver;

@Config
@TeleOp(name = "GetStats")

public class GetStats extends OpMode {
    public GoBildaPinpointDriver odometer;
    public double heading;
    private JoinedTelemetry joinedTelemetry;


    @Override
    public void init() {
        odometer = hardwareMap.get(GoBildaPinpointDriver.class, "xy-cord");
        odometer.setOffsets(Constants.ODOMETER_X_OFFSET, Constants.ODOMETER_Y_OFFSET);
        odometer.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        odometer.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        odometer.resetPosAndIMU();
        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        joinedTelemetry = new JoinedTelemetry(telemetry,panelsTelemetry.getTelemetry().getWrapper(),dashboard.getTelemetry());
    }

    public void loop() {
        odometer.update();
        Pose2D pos = odometer.getPosition();
        heading = pos.getHeading(AngleUnit.DEGREES);
        joinedTelemetry.addData("Xcor",pos.getX(DistanceUnit.MM));
        joinedTelemetry.addData("Ycor",pos.getY(DistanceUnit.MM));
        joinedTelemetry.addData("heading", heading);
        joinedTelemetry.update();
    }
}
