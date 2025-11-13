package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

@Autonomous(name = "FarShootingAuto",group = "Testing")
public class FarShootingAuto extends OpMode {
    private DriveTrainBasic driveTrain;
    public LightIndicatorSubsystem indicator;

    private JoinedTelemetry joinedTelemetry;
    private double startTime;
    private Location currentDestination;
    private boolean autoDone;
    private WayPoints currentWayPoint = WayPoints.Move_Off_Wall;

    @Override
    public void init() {
        driveTrain = new DriveTrainBasic(hardwareMap);
        driveTrain.init();  // commented out ,done in Auto
        driveTrain.odometer.resetPosAndIMU(); // comment out with Auto
        // Initializing indicator
        indicator = new LightIndicatorSubsystem(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Update Telemetry
        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        // Join them together
        this.joinedTelemetry = new JoinedTelemetry(telemetry, panelsTelemetry.getTelemetry().getWrapper(), dashboard.getTelemetry());
        joinedTelemetry.update();
        currentDestination = new Location();
        autoDone = false;
    }

    @Override
    public void start(){startTime = getRuntime();}
    @Override
    public void loop() {
        driveTrain.loop();
        Pose2D pos = driveTrain.odometer.getPosition();
        if (!autoDone) {
            switch (currentWayPoint) {
                case Move_Off_Wall:
                    // Set Destination
                    currentDestination.x = 0.0;
                    currentDestination.x_speed = 0.0;
                    currentDestination.y = 0.0;
                    currentDestination.y_speed = 0.0;
                    currentDestination.facing = Constants.NORTH;
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination)) {
                        this.goto_xy(currentDestination);
                    } else {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Aim;
                    }
                    break;
                case Aim:
                    // Set Destination
                    currentDestination.x = 0.0;
                    currentDestination.x_speed = 0.0;
                    currentDestination.y = 0.0;
                    currentDestination.y_speed = 0.0;
                    currentDestination.facing = Constants.NORTH;
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    currentWayPoint = WayPoints.Far_Shot;
                    break;
                case Far_Shot:
                    driveTrain.flywheel.set(Constants.FLYWHEEL_FAR);
                    driveTrain.feed1.setPower(Constants.FEED_SPEED);
                    if (driveTrain.canLaunch(Constants.FLYWHEEL_FAR)) {
                        indicator.setColor(Constants.RGB_Light.GREEN);
                        driveTrain.feed2.setPower(Constants.FEED_SPEED);
                    } else {
                        indicator.setColor(Constants.RGB_Light.RED);
                    }
                    if (getRuntime() - startTime >= 25.0){
                        driveTrain.flywheel.set(0.0);
                        driveTrain.feed1.setPower(0.0);
                        driveTrain.feed2.setPower(0.0);
                        currentWayPoint = WayPoints.Safe_Park;
                    }
                    currentWayPoint = WayPoints.Safe_Park;
                    break;
                case Safe_Park:
                    // Set Destination
                    currentDestination.x = 0.0;
                    currentDestination.x_speed = 0.0;
                    currentDestination.y = 0.0;
                    currentDestination.y_speed = 0.0;
                    currentDestination.facing = Constants.NORTH;
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination)) {
                        this.goto_xy(currentDestination);
                    } else {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Done;
                    }
                    break;
                default:
                    driveTrain.setFacing(Constants.NORTH);
                    driveTrain.stop();
                    autoDone = true;
                    break;
            }
            // Send data to telemetry
            joinedTelemetry.addData("Xcor",driveTrain.getXPosition());
            joinedTelemetry.addData("Ycor",driveTrain.getYPosition());
            joinedTelemetry.addData("X-cord", driveTrain.odometer.getPosX());
            joinedTelemetry.addData("Y-cord", driveTrain.odometer.getPosY());
            joinedTelemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));
            joinedTelemetry.addData("Current Waypoint",currentWayPoint);
            joinedTelemetry.addData("Destination X",currentDestination.x);
            joinedTelemetry.addData("Destination Y",currentDestination.y);
            joinedTelemetry.update();
        }
    }

    public boolean at_xy(Location currentLocation) {
        return this.at_x(currentLocation.x) && this.at_y(currentLocation.y);
    }

    public boolean at_x(double targetX) {
        return (Math.abs(targetX) - Math.abs(driveTrain.getXPosition())) <= Constants.AUTO_X_DISTANCE_ERROR;
    }

    public boolean at_y(double targetY) {
        return (Math.abs(targetY) - Math.abs(driveTrain.getYPosition())) <= Constants.AUTO_Y_DISTANCE_ERROR;
    }

    public static class Location {
        public double x = 0.0;
        public double x_speed = 0.0;
        public double y = 0.0;
        public double y_speed = 0.0;
        public double facing;
    }

    public void goto_xy(Location targetLocation) {
        // New Way to get there
        driveTrain.xControl.setSetPoint(targetLocation.x);
        driveTrain.yControl.setSetPoint(targetLocation.y);
        joinedTelemetry.addData("X Speed",driveTrain.xControl.calculate(driveTrain.getXPosition()) * Constants.AUTO_DRIVE_SPEED);
        joinedTelemetry.addData("Y Speed",driveTrain.yControl.calculate(driveTrain.getYPosition()) * Constants.AUTO_DRIVE_SPEED);
        driveTrain.drive(driveTrain.xControl.calculate(driveTrain.getXPosition()) * Constants.AUTO_DRIVE_SPEED, driveTrain.yControl.calculate(driveTrain.getYPosition()) * Constants.AUTO_DRIVE_SPEED);
    }

    // ===== Enum Data Type for waypoint switch
    enum WayPoints {
        Move_Off_Wall, Aim, Far_Shot, Safe_Park, Done
    }
}