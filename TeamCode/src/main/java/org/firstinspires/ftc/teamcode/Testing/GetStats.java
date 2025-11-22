package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.controller.PIDController;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegistrar;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.GoBildaPinpointDriver;

@TeleOp(name = "GetStats")

public class GetStats extends OpMode {
    public GoBildaPinpointDriver odometer;
    public PIDController headingControl = null;
    public static PIDCoefficients headingpid = new PIDCoefficients(Constants.HEADING_Kp, 0.001, 0.000);
    public static PIDCoefficients xpid = new PIDCoefficients(Constants.XPID_Kp, 0.0, 0.00);
    public static PIDCoefficients ypid = new PIDCoefficients(Constants.YPID_Kp, 0.0, 0.00);
    public PIDController xControl = null;
    public PIDController yControl = null;
    public double headingCorrection = 0;
    public double heading;
    public HardwareMap hwMap;
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
        // flywheelspeed = driveTrain.flywheel.getCorrectedVelocity();// what is corrected velocity??
        // joinedTelemetry.addData("Flywheel Speed",flywheelspeed); //telemetry shooter speed


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
