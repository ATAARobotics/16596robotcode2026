package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic2;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

//@Autonomous(name = "BlueFarShootingAuto2",group = "Testing")
public class BlueFarShootingAuto2 extends OpMode {
    private DriveTrainBasic2 driveTrain;
    public LightIndicatorSubsystem indicator;
    private JoinedTelemetry joinedTelemetry;
    public double startTime = 0.0;
    public double currentTime = 0.0;
    private Location currentDestination;
    private boolean autoDone;
    private WayPoints currentWayPoint = WayPoints.Move_Off_Wall;
    public double speed = 0.0;
    private WayPoints lastWayPoint = null;
    private double wayPointStartTime = 0.0;


    @Override
    public void init() {
        driveTrain = new DriveTrainBasic2(hardwareMap);
        driveTrain.init();
        driveTrain.odometer.resetPosAndIMU();
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
    public void start() {
        startTime = getRuntime();
    }

    @Override
    public void loop() {
        currentTime = getRuntime();
        driveTrain.setNow(currentTime);
        updateWayPointTimer();
        driveTrain.loop();
        Pose2D pos = driveTrain.odometer.getPosition();

        if (!autoDone) {

            //Waypoints:  Move_Off_Wall, Aim, Far_Shot, Move_Off_White_Tape, Safe_Park, Done
            switch (currentWayPoint) {
                case Move_Off_Wall:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterPICKUP;
                    // Set Destination
                    currentDestination.x = Constants.BLUE_FAR_SHOOTING_MOVE_OFF_WALL_X;//move forward towards the tape
                    currentDestination.x_speed = 0.5;
                    currentDestination.y = -121.0; //current value needs to be test, was -121
                    currentDestination.y_speed = 0.5;
                    currentDestination.facing = Constants.NORTH;
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination))
                    {
                        this.goto_xy(currentDestination);
                    }
                    else
                    {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Aim;
                    }
                    break;
                    
                case Aim:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterPICKUP;
                    // Set Destination
                    // Turns our robot to face to the obelisk
                    currentDestination.x = 373.0;// used to be 43.0
                    currentDestination.x_speed = 0.5;
                    currentDestination.y = -121.0; //shouldn't change from the previous value
                    currentDestination.y_speed = 0.5;
                    currentDestination.facing = Constants.FAR_AUTO_AIM_ANGLE_BLUE; //Turns to the obelisk, may need to be adjusted
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    currentWayPoint = WayPoints.Far_Shot;
                    break;
                    
                case Far_Shot://Shoots the artifacts
                    driveTrain.drive(0.0, 0.0);
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterSHOOTINGfar;
                    if (wayPointActiveFor(6)) {
                        driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
                        currentWayPoint = WayPoints.Move_Off_White_Tape;
                    }
                    break;
                    
                case Move_Off_White_Tape:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
                    // Set Destination
                    currentDestination.x = 470.0;
                    currentDestination.x_speed = 0.5;
                    currentDestination.y = -622.0;
                    currentDestination.y_speed = 0.5;
                    currentDestination.facing = Constants.FAR_AUTO_AIM_ANGLE_BLUE;
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination))
                    {
                        this.goto_xy(currentDestination);
                    }
                    else
                    {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Safe_Park;
                    }
                    break;
                    
                case Safe_Park:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
                    // Set Destination
                    currentDestination.x = 430.0; // makes if face the drive team, same facing as starting
                    currentDestination.x_speed = 0.5;
                    currentDestination.y = -735.0;
                    currentDestination.y_speed = 0.5;
                    currentDestination.facing = Constants.NORTH;
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination))
                    {
                        this.goto_xy(currentDestination);
                    }
                    else
                    {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Done;
                    }
                    break;
                    
                case Done:
                default:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
                    driveTrain.setFacing(Constants.NORTH);
                    driveTrain.stop();
                    autoDone = true;
                    break;
            }
            updateWayPointTimer();
            updateIndicator();
            driveTrain.ShooterControlLoop();


            // Send data to telemetry
            speed = driveTrain.Flywheel.getCorrectedVelocity();
            joinedTelemetry.addData("Shooter Speed", speed);
            joinedTelemetry.addData("Xcor",driveTrain.getXPosition());
            joinedTelemetry.addData("Ycor",driveTrain.getYPosition());
            joinedTelemetry.addData("X-cord", driveTrain.odometer.getPosX());
            joinedTelemetry.addData("Y-cord", driveTrain.odometer.getPosY());
            joinedTelemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));
            joinedTelemetry.addData("Current Waypoint",currentWayPoint.toString());
            joinedTelemetry.addData("Destination X",currentDestination.x);
            joinedTelemetry.addData("Destination Y",currentDestination.y);
            joinedTelemetry.addData("Run Time",getRuntime());
            joinedTelemetry.addData("Time Diff",getRuntime() - startTime);
            joinedTelemetry.update();
        }
    }  // END LOOP
    public boolean at_xy(Location currentLocation) {
        return this.at_x(currentLocation.x) && this.at_y(currentLocation.y);
    }

    public boolean at_x(double targetX) {
        //return (Math.abs(targetX) - Math.abs(driveTrain.getXPosition())) <= Constants.AUTO_X_DISTANCE_ERROR;
        //BlueAuto uses negative setpoints, using absolute value of position can cause incorrect readings.
        return (Math.abs(targetX - driveTrain.getXPosition())) <= Constants.AUTO_X_DISTANCE_ERROR;
    }

    public boolean at_y(double targetY) {
        //return (Math.abs(targetY) - Math.abs(driveTrain.getYPosition())) <= Constants.AUTO_Y_DISTANCE_ERROR;
        //BlueAuto uses negative setpoints, using absolute value of position can cause incorrect readings.
        return (Math.abs(targetY - driveTrain.getYPosition())) <= Constants.AUTO_Y_DISTANCE_ERROR;
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
        Move_Off_Wall, Aim, Far_Shot, Move_Off_White_Tape, Safe_Park, Done
    }

    private void updateIndicator() {
        // INDICATOR CASES
        switch (currentWayPoint) {
            case Move_Off_Wall:
                indicator.setColor(Constants.RGB_Light.VIOLET);
            case Aim:
                indicator.setColor(Constants.RGB_Light.YELLOW);
            case Far_Shot:
                if (driveTrain.canLaunch(Constants.FLYWHEEL_FAR_AUTO)) {
                    indicator.setColor(Constants.RGB_Light.GREEN);
                } else {
                    indicator.setColor(Constants.RGB_Light.RED);
                }
            case Move_Off_White_Tape:
                indicator.setColor(Constants.RGB_Light.BLUE);
            case Safe_Park:
                indicator.setColor(Constants.RGB_Light.ORANGE);
            case Done:
            default:
                indicator.setColor(Constants.RGB_Light.WHITE);
        }
    }
    private void updateWayPointTimer() {
        if (currentWayPoint != lastWayPoint) {
            wayPointStartTime = getRuntime();   // capture when we ENTER the waypoint
            lastWayPoint = currentWayPoint;
        }
    }

    private boolean wayPointActiveFor(double seconds) {
        return (getRuntime() - wayPointStartTime) >= seconds;
    }

    private double wayPointTime() {
        return getRuntime() - wayPointStartTime;
    }

}
