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

@Autonomous(name = "RedFarShootingAuto",group = "Testing")
public class RedFarShootingAuto extends OpMode {
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
            driveTrain.intake.set(Constants.INTAKE_SPEED);
            switch (currentWayPoint) {

                case Move_Off_Wall:
                    // Set Destination
                    currentDestination.x = -250.0; // was 43.0
                    currentDestination.x_speed = 0.5;
                    currentDestination.y = 121.0; //current value needs to be test, was -221
                    currentDestination.y_speed = 0.5;
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
                    // Turns our robot to face to the obelisk
                    currentDestination.x = -250.0;// was - 712.0 was 255
                    currentDestination.x_speed = 0.5;
                    currentDestination.y = 121.0; //shouldn't change from the previous value
                    //current value needs to be test, was -221
                    currentDestination.y_speed = 0.5;
                    currentDestination.facing = -Constants.FAR_AUTO_AIM_ANGLE; //Turns to the obelisk, may need to be adjusted
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                 //  currentWayPoint = WayPoints.Far_Shot; // put back in for final
                    currentWayPoint = WayPoints.Far_Shot;
                    break;
                case Far_Shot://Shoots the artifacts
                    // Currently not running as of Nov 22 2025
                    driveTrain.shooter.set(Constants.FLYWHEEL_FAR_AUTO);
                    if (driveTrain.canLaunch(Constants.FLYWHEEL_FAR_AUTO)) {
                        indicator.setColor(Constants.RGB_Light.GREEN);
                        driveTrain.feed1.setPower(Constants.FEED_SPEED);
                        driveTrain.feed2.setPower(Constants.FEED_SPEED);
                        // creates an if statement based on the flywheel recovery speed so it only shoots when it's up to speed
                        if(Constants.FLYWHEEL_FAR_AUTO >= Constants.FLYWHEEL_RECOVERY){
                            driveTrain.feed2.setPower(Constants.FEED_SPEED);
                        }
//                    else {
//                        driveTrain.feed2.setPower(0);
//                    }
                    } else {
                        indicator.setColor(Constants.RGB_Light.RED);
                    }

                    if (getRuntime() - startTime >= 16.7) {
                        driveTrain.shooter.set(0.0); //this doesnt stop at the end either-we need it to stop
                        driveTrain.feed1.setPower(0.0);
                        driveTrain.feed2.setPower(0.0);
                        driveTrain.intake.set(0); //this doesn't stop at the end- we need it to stop
                        driveTrain.stopFlyWheel();
                        currentWayPoint = WayPoints.Move_Off_White_Tape;
                    }
                        else{ //rechecks constantly if under that amount of time
                        if(Constants.FLYWHEEL_FAR_AUTO >= Constants.FLYWHEEL_RECOVERY) {
                            driveTrain.feed2.setPower(Constants.FEED_SPEED);
                        }
                    }
                    break;
                case Move_Off_White_Tape:
                    // Set Destination
                    currentDestination.x = 360.0; //was -760
                    currentDestination.x_speed = 0.5;
                    currentDestination.y = 622.0;
                    currentDestination.y_speed = 0.5;
                    currentDestination.facing = Constants.NORTH;// was FAR_AUTO_AIM_ANGLE
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination)) {
                        this.goto_xy(currentDestination);
                    } else {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.Safe_Park;
                    }
                    break;
                case Safe_Park:
                    // Set Destination      // x was -760 the was -260 was 260
                    currentDestination.x = 360.0; // makes if face the drive team, same facing as starting
                    currentDestination.x_speed = 0.5;
                    currentDestination.y = 735.0;
                    currentDestination.y_speed = 0.5;
                    currentDestination.facing = Constants.NORTH;
                    // Move to Location
                    driveTrain.setFacing(currentDestination.facing);
                    if (!this.at_xy(currentDestination)) {
                        this.goto_xy(currentDestination);
                        //stops everything
                      driveTrain.shooter.set(0.0);
         //               driveTrain.feed1.setPower(0.0);
          //              driveTrain.feed2.setPower(0.0);
                      driveTrain.intake.set(0);
                    } else {
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
            joinedTelemetry.addData("Xcor",driveTrain.getXPosition());
            joinedTelemetry.addData("Ycor",driveTrain.getYPosition());
            joinedTelemetry.addData("X-cord", driveTrain.odometer.getPosX());
            joinedTelemetry.addData("Y-cord", driveTrain.odometer.getPosY());
            joinedTelemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));
            joinedTelemetry.addData("Current Waypoint",currentWayPoint.toString());
            joinedTelemetry.addData("Destination X",currentDestination.x);
            joinedTelemetry.addData("Deqstination Y",currentDestination.y);
           // joinedTelemetry.addData("shooter", driveTrain.shooter.getVelocity());
            joinedTelemetry.addData("shooter2", driveTrain.shooter.getVelocity());
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
        Move_Off_Wall, Aim, Far_Shot, Move_Off_White_Tape, Safe_Park, Done
    }
}