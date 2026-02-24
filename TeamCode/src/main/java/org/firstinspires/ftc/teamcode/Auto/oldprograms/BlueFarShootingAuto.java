package org.firstinspires.ftc.teamcode.Auto.oldprograms;

import com.acmerobotics.dashboard.FtcDashboard;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

//@Autonomous(name = "BlueFarShootingAuto",group = "Testing")
public class BlueFarShootingAuto extends OpMode {
    private DriveTrainBasic driveTrain;
    public LightIndicatorSubsystem indicator;
    private JoinedTelemetry joinedTelemetry;
    private double startTime;
    private Location currentDestination;
    private boolean autoDone;
    private WayPoints currentWayPoint = WayPoints.Move_Off_Wall;
    public double speed = 0.0;
    private double shotStartTime = -1.0;

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

            //Condition Motor control
            //Waypoints:  Move_Off_Wall, Aim, Far_Shot, Move_Off_White_Tape, Safe_Park, Done
            
            //INTAKE
            if (currentWayPoint == WayPoints.Move_Off_Wall || currentWayPoint == WayPoints.Aim || currentWayPoint == WayPoints.Far_Shot) {
                driveTrain.intake.set(Constants.INTAKE_SPEED_AUTO);
            } 
            else 
            {
                driveTrain.intake.set(0);
            }
            
            //FEEDS
            if (currentWayPoint == WayPoints.Move_Off_Wall || currentWayPoint == WayPoints.Aim) 
            {
                driveTrain.feed1.setPower(-Constants.FEED_SPEED);
                driveTrain.feed2.setPower(-Constants.FEED_SPEED);
            } 
            else if (currentWayPoint == WayPoints.Far_Shot && driveTrain.canLaunch(Constants.FLYWHEEL_FAR_AUTO))
            {
                driveTrain.feed1.setPower(Constants.FEED_SPEED);
                driveTrain.feed2.setPower(Constants.FEED_SPEED);
            }
            else
            {
                driveTrain.feed1.setPower(0.0);
                driveTrain.feed2.setPower(0.0);
            }
            
            //SHOOTER
            if (currentWayPoint == WayPoints.Far_Shot) 
            {
                    //Shooter Timer Test - If this works, replace shooter time with constant                    
                    // Get runtime when shooter starts shooting
                    if (shotStartTime < 0) shotStartTime = getRuntime();
                    if (getRuntime() - shotStartTime < 5.0)
                    {
                        driveTrain.shooter.set(Constants.FLYWHEEL_FAR_AUTO);
                    }
                    else
                    {
                        driveTrain.shooter.set(0.0);
                    }
            }
            else
            {
                driveTrain.shooter.set(0.0);
            }
            
            //INDICATOR
            if (currentWayPoint == WayPoints.Move_Off_Wall) 
            {
                indicator.setColor(Constants.RGB_Light.VIOLET);
            }
            else if (currentWayPoint == WayPoints.Aim) 
            {
                indicator.setColor(Constants.RGB_Light.YELLOW);
            }
            else if (currentWayPoint == WayPoints.Far_Shot) 
            {
                if (driveTrain.canLaunch(Constants.FLYWHEEL_FAR_AUTO))
                {
                    indicator.setColor(Constants.RGB_Light.GREEN);
                }
                else
                {
                    indicator.setColor(Constants.RGB_Light.RED);
                }
            }
            else if (currentWayPoint == WayPoints.Move_Off_White_Tape) 
            {
                indicator.setColor(Constants.RGB_Light.BLUE);
            }
            else if (currentWayPoint == WayPoints.Safe_Park) 
            {
                indicator.setColor(Constants.RGB_Light.ORANGE);
            }
            else if (currentWayPoint == WayPoints.Done) 
            {
                indicator.setColor(Constants.RGB_Light.WHITE);
            }  
            else
            {
                indicator.setColor(Constants.RGB_Light.OFF);
            }
            
            switch (currentWayPoint) {
                case Move_Off_Wall:
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
                    if (shotStartTime < 0) shotStartTime = getRuntime();
                    driveTrain.drive(0.0, 0.0);
                    //Shooter Timer Test - If this works, replace shooter time with constant
                    if (getRuntime() - shotStartTime >= Constants.AUTO_WAIT_TIME)
                    {
                        driveTrain.stopFlyWheel();
                        currentWayPoint = WayPoints.Move_Off_White_Tape;
                        shotStartTime = -1;
                    }
                    break;
                    
                case Move_Off_White_Tape:
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
                    driveTrain.setFacing(Constants.NORTH);
                    driveTrain.stop();
                    autoDone = true;
                    break;
            }

            
            // Send data to telemetry
            speed = driveTrain.shooter.getCorrectedVelocity();
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
    }

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
}
