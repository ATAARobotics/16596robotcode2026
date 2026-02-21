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

@Autonomous(name = "RedFarShootingAuto")
public class RedFarShootingAuto extends OpMode {
    private DriveTrainBasic driveTrain;
    public LightIndicatorSubsystem indicator;
    private JoinedTelemetry joinedTelemetry;
    private double startTime;
    private Location currentDestination;
    private boolean autoDone;
    private WayPoints currentWayPoint = WayPoints.Move_Off_Wall;
    public double speed = 0.0;
    private boolean secondAim  = false;

    @Override
    public void init() {
        driveTrain = new DriveTrainBasic(hardwareMap);
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
    public void start(){startTime = getRuntime();}
    @Override
    public void loop() {
        driveTrain.loop();
        Pose2D pos = driveTrain.odometer.getPosition();
        if (!autoDone) {
            driveTrain.intake.set(Constants.INTAKE_SPEED_AUTO);
            switch (currentWayPoint) {
                case Move_Off_Wall:
                    driveTrain.feed1.setPower(-Constants.FEED_SPEED);
                    driveTrain.feed2.setPower(-Constants.FEED_SPEED);
                    driveTrain.feeder.set(-Constants.FEEDER_SPEED);
                    // Set Destination
                    double distanceTarget = get_distance(currentDestination);
                    currentDestination.x = -250.0; // was 43.0
                    double speed = Math.max(0.1, Math.min(distanceTarget / 100.0,Constants.AUTO_DRIVE_SPEED));
                     currentDestination.x_speed = speed;
                    // currentDestination.x_speed = 0.5;
                    currentDestination.y = 121.0; //current value needs to be test, was -221
                      currentDestination.y_speed = speed;
                    //  currentDestination.y_speed = 0.5;
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
                    // Set Destination
                    // Turns our robot to face to the obelisk
                    currentDestination.x = -200.0;// was - 712.0 was 255
                    currentDestination.x_speed = Constants.AUTO_DRIVE_SPEED;
                    currentDestination.y = 121.0; //shouldn't change from the previous value
                    //current value needs to be test, was -221
                    currentDestination.y_speed = Constants.AUTO_DRIVE_SPEED;
                    currentDestination.facing = -Constants.FAR_AUTO_AIM_ANGLE_RED; //Turns to the obelisk, may need to be adjusted
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    currentWayPoint = WayPoints.Far_Shot;
                    break;

                case Far_Shot://Shoots the artifacts
                    // Currently not running as of Nov 22 2025
                    driveTrain.shooter.set(Constants.FLYWHEEL_FAR_AUTO);
                    if (driveTrain.canLaunch(Constants.FLYWHEEL_FAR_AUTO))
                    {
                        indicator.setColor(Constants.RGB_Light.GREEN);
                        driveTrain.feed1.setPower(Constants.FEED_SPEED);
                        driveTrain.feed2.setPower(Constants.FEED_SPEED);
                        driveTrain.feeder.set(Constants.FEEDER_SPEED);
                    }
                    else
                    {
                        indicator.setColor(Constants.RGB_Light.RED);
                        driveTrain.feed1.setPower(0.0);
                        driveTrain.feed2.setPower(0.0);
                        driveTrain.feeder.set(0.0);
                    }
                    if (getRuntime() - startTime >= Constants.AUTO_WAIT_TIME)
                    {
                        driveTrain.shooter.set(0.0); //this doesn't stop at the end either-we need it to stop
                        driveTrain.stopFlyWheel();
                        driveTrain.feed1.setPower(0.0);
                        driveTrain.feed2.setPower(0.0);
                        driveTrain.feeder.set(0.0);
                        driveTrain.intake.set(0); //this doesn't stop at the end- we need it to stop
                        startTime = startTime + Constants.AUTO_WAIT_TIME;

                       if(!secondAim){
                            secondAim = true;
                           currentWayPoint = WayPoints.Move_Off_White_Tape;
                        }
                        else // done second shoot
                        {
                            currentWayPoint = WayPoints.Safe_Park;
                           startTime = startTime + Constants.AUTO_WAIT_TIME;
                        }
                    }
                    break;
                case Move_Off_White_Tape:
                    // Set Destination
                    currentDestination.x = 360.0; //was -760
                    currentDestination.x_speed = Constants.AUTO_DRIVE_SPEED;
                    currentDestination.y = 735.0;
                    currentDestination.y_speed = Constants.AUTO_DRIVE_SPEED;
                    currentDestination.facing = Constants.NORTH;// was FAR_AUTO_AIM_ANGLE
                    driveTrain.intake.set(Constants.INTAKE_SPEED_AUTO);
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination))
                    {
                        this.goto_xy(currentDestination);
                    }
                    else
                    {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Pick_Up;
                    }
                    break;
                case Pick_Up:
                    currentDestination.x = 800.0; //was -760 //was 870
                    currentDestination.x_speed = Constants.AUTO_DRIVE_SPEED;//was 0.0
                    currentDestination.y = 735.0;// 735
                    currentDestination.y_speed = Constants.AUTO_DRIVE_SPEED;
                    currentDestination.facing = Constants.NORTH;// was FAR_AUTO_AIM_ANGLE
                    driveTrain.intake.set(Constants.INTAKE_SPEED_AUTO);
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                   // currentWayPoint = WayPoints.Aim2;

                    if (!this.at_xy(currentDestination))
                    {
                        this.goto_xy(currentDestination);
                    }
                    else
                    {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Move_Off_Wall;
                    }

                    break;

                case Safe_Park:
                    // Set Destination      // x was -760 the was -260 was 260
                    currentDestination.x = 360.0; // makes if face the drive team, same facing as starting
                    currentDestination.x_speed = Constants.AUTO_DRIVE_SPEED;
                    currentDestination.y = 735.0;
                    currentDestination.y_speed = Constants.AUTO_DRIVE_SPEED;
                    currentDestination.facing = Constants.NORTH;
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination))
                    {
                        this.goto_xy(currentDestination);
                        //stops everything
                        driveTrain.shooter.set(0.0);
                        driveTrain.intake.set(0);
                    }
                    else
                    {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Done;
                    }
                    break;
                case Done:
                default:
                    driveTrain.setFacing(Constants.NORTH);
                    driveTrain.stop();
                    autoDone = true;
                    break;
            }
            // Send data to telemetry
            speed = driveTrain.shooter.getCorrectedVelocity();
            joinedTelemetry.addData("Shooter Speed", speed);
            joinedTelemetry.addData("Feeder Speed", driveTrain.feeder.getVelocity());
            joinedTelemetry.addData("X-cord",driveTrain.getXPosition());
            joinedTelemetry.addData("Y-cord",driveTrain.getYPosition());
            joinedTelemetry.addData("Odometer X-cord", driveTrain.odometer.getPosX());
            joinedTelemetry.addData("Odometer Y-cord", driveTrain.odometer.getPosY());
            joinedTelemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));
            joinedTelemetry.addData("Current Waypoint",currentWayPoint.toString());
            joinedTelemetry.addData("Destination X",currentDestination.x);
            joinedTelemetry.addData("Destination Y",currentDestination.y);
            joinedTelemetry.addData("Run Time",getRuntime());
            joinedTelemetry.addData("Time Diff",getRuntime() - startTime);
            joinedTelemetry.update();
        }
    }

    public boolean at_xy(Location currentLocation) {
        return this.at_x(currentLocation.x) && this.at_y(currentLocation.y);
    }
    public double get_distance(Location currentLocation) {
        return Math.sqrt(Math.pow(driveTrain.getXPosition()-currentLocation.x,2) + Math.pow(driveTrain.getYPosition()-currentLocation.y,2));
    }
    public boolean at_x(double targetX) {
        return Math.abs(Math.abs(targetX) - Math.abs(driveTrain.getXPosition())) <= Constants.AUTO_X_DISTANCE_ERROR;
    }

    public boolean at_y(double targetY) {
        return Math.abs(Math.abs(targetY) - Math.abs(driveTrain.getYPosition())) <= Constants.AUTO_Y_DISTANCE_ERROR;
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
//        joinedTelemetry.addData("X Speed",driveTrain.xControl.calculate(driveTrain.getXPosition()) * Constants.AUTO_DRIVE_SPEED);
//        joinedTelemetry.addData("Y Speed",driveTrain.yControl.calculate(driveTrain.getYPosition()) * Constants.AUTO_DRIVE_SPEED);
        joinedTelemetry.addData("X Speed",driveTrain.xControl.calculate(driveTrain.getXPosition()) * targetLocation.x_speed);
        joinedTelemetry.addData("Y Speed",driveTrain.yControl.calculate(driveTrain.getYPosition()) * targetLocation.y_speed);
        driveTrain.drive(driveTrain.xControl.calculate(driveTrain.getXPosition()) * targetLocation.y_speed, driveTrain.yControl.calculate(driveTrain.getYPosition()) * targetLocation.y_speed);
//        driveTrain.drive(driveTrain.xControl.calculate(driveTrain.getXPosition()) * Constants.AUTO_DRIVE_SPEED, driveTrain.yControl.calculate(driveTrain.getYPosition()) * Constants.AUTO_DRIVE_SPEED);
    }

    // ===== Enum Data Type for waypoint switch
    enum WayPoints {
        Move_Off_Wall, Aim, Far_Shot, Move_Off_White_Tape, Safe_Park, Pick_Up, Done
    }
}