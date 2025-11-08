package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

import java.util.Locale;
@Autonomous(name = "SimpleAuto")
public class SimpleAuto extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private DriveTrainBasic driveTrain;

    private JoinedTelemetry joinedTelemetry;
    public double speed = 0.0;
    public double speed2 = 0.0;
    public double flywheelspeed = 0.0;

    private int current_step = 0;
    private final int total_waypoints = 2;
    private WayPoint[] wayPoints;
    private boolean autoDone;

    @Override
    public void init() {
        driveTrain = new DriveTrainBasic(hardwareMap);
        driveTrain.init();  // commented out ,done in Auto
        driveTrain.odometer.resetPosAndIMU(); // comment out with Auto
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Update Telemetry
        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        // Join them together
        this.joinedTelemetry = new JoinedTelemetry(telemetry, panelsTelemetry.getTelemetry().getWrapper(), dashboard.getTelemetry());
        joinedTelemetry.update();
        //Waypoint setup
        this.wayPoints = new WayPoint[this.total_waypoints + 1];
        this.wayPoints[0] = new WayPoint();
        this.wayPoints[0].x = 0.0;
        this.wayPoints[0].y = 0.0;
        this.wayPoints[0].facing = Constants.NORTH;
        // WayPoint 1
        this.wayPoints[1] = new WayPoint();
        this.wayPoints[1].x = 100.0;
        this.wayPoints[1].x_speed = 0.5;
        this.wayPoints[1].y = 0.0;
        this.wayPoints[1].y_speed = 0.0;
        this.wayPoints[1].facing = Constants.NORTH;
        // Waypoint Enum

        this.current_step = 1;
        autoDone = false;
    }

    @Override
    public void loop() {
        driveTrain.loop();
        Pose2D pos = driveTrain.odometer.getPosition();
        String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", driveTrain.getXPosition(), driveTrain.getYPosition(), pos.getHeading(AngleUnit.DEGREES));
        if (!autoDone) {
            switch (this.current_step) {
                case 1:
                    driveTrain.setFacing(this.wayPoints[this.current_step].facing);
                    if (!this.at_xy(this.wayPoints[this.current_step])) {
                        this.goto_xy(this.wayPoints[this.current_step]);
                    } else {
                        driveTrain.drive(0.0, 0.0);
                        this.current_step++;
                    }
                    break;
                default:
                    driveTrain.setFacing(Constants.NORTH);
                    driveTrain.stop();
                    autoDone = true;
                    break;
            }
            // Send data to telemetry
            joinedTelemetry.addData("X-cord", driveTrain.odometer.getPosX());
            joinedTelemetry.addData("Y-cord", driveTrain.odometer.getPosY());
            joinedTelemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));
            joinedTelemetry.addData("Current Step",this.current_step);
            joinedTelemetry.addData("Waypoint X",this.wayPoints[this.current_step].x);
            joinedTelemetry.addData("Waypoint Y",this.wayPoints[this.current_step].y);
            joinedTelemetry.update();
        }
    }

    public boolean at_xy(WayPoint currentWaypoint) {
        return this.at_x(currentWaypoint.x) && this.at_y(currentWaypoint.y);
    }

    public boolean at_x(double targetX) {
        return (Math.abs(targetX) - Math.abs(driveTrain.getXPosition())) <= Constants.AUTO_X_DISTANCE_ERROR;
    }

    public boolean at_y(double targetY) {
        return (Math.abs(targetY) - Math.abs(driveTrain.getYPosition())) <= Constants.AUTO_Y_DISTANCE_ERROR;
    }

    public class WayPoint {
        public double x;
        public double x_speed = 0.2;
        public double y;
        public double y_speed = 0.2;
        public double facing;
    }

    public void goto_xy(WayPoint targetWaypoint) {
        // New Way to get there
        driveTrain.xControl.setSetPoint(targetWaypoint.x);
        driveTrain.yControl.setSetPoint(targetWaypoint.y);
        driveTrain.drive(driveTrain.xControl.calculate(driveTrain.getXPosition()) * Constants.AUTO_DRIVE_SPEED, driveTrain.yControl.calculate(driveTrain.getYPosition()) * Constants.AUTO_DRIVE_SPEED);
    }
}