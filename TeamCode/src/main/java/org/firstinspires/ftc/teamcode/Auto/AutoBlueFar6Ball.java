package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.C2;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic2;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

@Autonomous(name = "Blue Far 6 Balls")
public class AutoBlueFar6Ball extends OpMode {
    private double atTargetStartTime = -1;
    private static final double AT_TARGET_HOLD_TIME = 0.5; // seconds (500ms)

    private DriveTrainBasic2 driveTrain;
    public LightIndicatorSubsystem indicator;
    private JoinedTelemetry joinedTelemetry;
    public double startTime = 0.0;
    public double currentTime = 0.0;
    //private Location currentDestination;
    private boolean autoDone = false;
    private WayPoints currentWayPoint = WayPoints.MoveOffWall;
    public double speed = 0.0;
    private WayPoints lastWayPoint = null;
    private double wayPointStartTime = 0.0;
    double xOut = 0.0;
    double yOut = 0.0;
    int shotCount = 0;


    //Waypoints:  Move_Off_Wall, Aim, Far_Shot, Move_Off_White_Tape, Safe_Park, Done
    // -x = Forward (mm) Distance from start (0,0) - red=left
    // +x = Reverse (mm) Distance from start (0,0) - red=right
    // -y = Strafe Left (mm) Distance from start (0,0)
    // +y = Strafe Right (mm) Distance from start (0,0)
    // -f = Turn Right/Clockwise (deg)
    // +f = Turn Left/Counterclockwise (deg)
    // When heading = 0/-180 then Y=STRAFE
    // When heading = 90/-90 the X=STRAFE
    //Define Locations relative to start
    private static final Location destMoveOffWall = new Location(-260,-105,0);
    private static final Location destAimLongRed = new Location(-220,-105,108);
    private static final Location destMoveOffWhite = new Location(100,-735,0);
    private static final Location destPickupFarRow = new Location(870,-735,0.0);
    private static final Location destSafePark = new Location(360,-735,0);

    // Drive modes control speed and precision - if precision is required, lower speed higher precision.  else, higher speed lower prec
    private driveMode dmPrecise = new driveMode(0.5567,4.670,4.670,1.50);
    private driveMode dmRough = new driveMode(0.7,17.670,17.670,4.50);

    //Create Location object for currentLocation variable to hold current position read by odometry
    private Location currentLocation = new Location(0.0,0.0,0.0);
    private Location currentDestination = new Location(0.0,0.0,0.0);

    // Holding object for current drive mode precision settings
    private driveMode currentDriveMode = new driveMode(0,0,0,0);

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
        //currentDestination = new Location();
    }

    @Override
    public void start() {
        startTime = getRuntime();
        currentDestination = destPickupFarRow;
    }

    @Override
    public void loop() {
        driveTrain.loop();
        updateCurrentLocation(driveTrain.odometer.getPosition());
        currentTime = getRuntime() - startTime;
        driveTrain.setNow(currentTime);
        updateWayPointTimer();
        Pose2D pos = driveTrain.odometer.getPosition();

        if (!autoDone) {
            switch (currentWayPoint) {
                case MoveOffWall:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterHOLDING;// Set Shooter Mode
                    currentDestination = destMoveOffWall;
                    currentDriveMode = dmRough;
                    if (goto_xy(currentDestination, currentDriveMode) || wayPointActiveFor(1.0)) {
                        driveTrain.drive(0.0, 0.0);
                        currentWayPoint = WayPoints.AimLongRedShoot;
                    }
                    driveTrain.setFacing(currentDestination.facing);
                    break;
                    
                case AimLongRedShoot:
                   //commented for test for waiting time and shooter recovery
                    // driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterHOLDING;// Set Shooter Mode
                    currentDestination = destAimLongRed;
                    currentDriveMode = dmPrecise;
                    if (goto_xy(currentDestination, currentDriveMode) || wayPointActiveFor(3)) {
                        driveTrain.drive(0.0, 0.0);
                        driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterSHOOTINGfarBlue;
                        if (wayPointActiveFor(9)) {
                            driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
                            if (shotCount == 0) {
                                currentWayPoint = WayPoints.MoveOffWhite;
                                shotCount = 1;
                            } else {
                                currentWayPoint = WayPoints.SafePark;
                            }
                        }
                    }
                    driveTrain.setFacing(currentDestination.facing);
                    break;
                    
                case MoveOffWhite:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
                    currentDestination = destMoveOffWhite;
                    currentDriveMode = dmRough;
                    driveTrain.setFacing(currentDestination.facing);
                    if (wayPointActiveFor(0.5)) {
                        if (goto_xy(currentDestination, currentDriveMode) || wayPointActiveFor(2.5)) {
                            driveTrain.drive(0.0, 0.0);
                            currentWayPoint = WayPoints.PickupFarRow;
                        }
                    }
                    break;

                case PickupFarRow:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterPICKUP;
                    currentDestination = destPickupFarRow;
                    currentDriveMode = dmPrecise;
                    if (goto_xy(currentDestination, currentDriveMode) || wayPointActiveFor(5)) {
                        driveTrain.drive(0.0, 0.0);
                        driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterHOLDING;
                        currentWayPoint = WayPoints.MoveOffWall;
                    }
                    driveTrain.setFacing(currentDestination.facing);
                    break;

                case SafePark:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
                    currentDestination = destSafePark;
                    currentDriveMode = dmRough;
                    driveTrain.setFacing(currentDestination.facing);
                    if (wayPointActiveFor(0.1)) {
                        if (goto_xy(currentDestination, currentDriveMode) || wayPointActiveFor(2)) {
                            driveTrain.drive(0.0, 0.0);
                            currentWayPoint = WayPoints.Done;
                        }
                    }
                    break;
                case Done:
                default:
                    driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
                    driveTrain.stop();
                    autoDone = true;
                    break;
            }


            // Send data to telemetry
            speed = driveTrain.Flywheel.getCorrectedVelocity();
            //joinedTelemetry.addData("Shooter Speed", speed);
            joinedTelemetry.addData("Xcor",driveTrain.getXPosition());
            joinedTelemetry.addData("Ycor",driveTrain.getYPosition());
            joinedTelemetry.addData("Heading", currentLocation.facing); //pos.getHeading(AngleUnit.DEGREES));
            joinedTelemetry.addData("----", "-------");
            joinedTelemetry.addData("X-error",Math.abs(currentDestination.x - driveTrain.getXPosition()));
            joinedTelemetry.addData("Y-error",Math.abs(currentDestination.y - driveTrain.getYPosition()));
            joinedTelemetry.addData("H-error", driveTrain.headingError);
            joinedTelemetry.addData("----", "-------");
            joinedTelemetry.addData("Current Waypoint",currentWayPoint.toString());
            joinedTelemetry.addData("Destination X",currentDestination.x);
            joinedTelemetry.addData("Destination Y",currentDestination.y);
            joinedTelemetry.addData("Run Time",getRuntime());
            joinedTelemetry.addData("Time Diff",getRuntime() - startTime);
            joinedTelemetry.addData("Speed",speed);
            joinedTelemetry.update();
        }else {
            driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
            driveTrain.stop();
        }
        updateWayPointTimer();
        updateIndicator();
        driveTrain.ShooterControlLoop();
    }  // END LOOP

    public void updateCurrentLocation(Pose2D pos) {
        currentLocation.x = -pos.getX(DistanceUnit.MM);
        currentLocation.y = -pos.getY(DistanceUnit.MM);
        currentLocation.facing = pos.getHeading(AngleUnit.DEGREES);
    }

    //If we run out of time then goto safepark
    public void overtimeOverride() {
        if (currentTime > 28.0
                && currentWayPoint != WayPoints.SafePark
                && currentWayPoint != WayPoints.Done) {
            currentWayPoint = WayPoints.SafePark;
        }
    }
    public boolean at_xy(Location destinationLocation, driveMode driveMode) {
    boolean withinTolerance =
            at_x(destinationLocation.x, driveMode) && at_y(destinationLocation.y, driveMode) && driveTrain.onHeading;
    if (withinTolerance) {
        // First time entering tolerance
        if (atTargetStartTime < 0) {
            atTargetStartTime = getRuntime();
        }
        // Have we stayed long enough?
        return (getRuntime() - atTargetStartTime) >= AT_TARGET_HOLD_TIME;
    } else {
        // Left tolerance — reset timer
        atTargetStartTime = -1;
    }

    return false;
}

    public boolean at_x(double destinationX, driveMode driveMode) {
        //return (Math.abs(destinationX - driveTrain.getXPosition())) <= C2.AUTO_X_DISTANCE_ERROR;
        return (Math.abs(destinationX - driveTrain.getXPosition())) <= driveMode.xTolerance;
    }

    public boolean at_y(double destinationY, driveMode driveMode) {
        //return (Math.abs(destinationY - driveTrain.getYPosition())) <= C2.AUTO_Y_DISTANCE_ERROR;
        return (Math.abs(destinationY - driveTrain.getYPosition())) <= driveMode.yTolerance;
    }

    public static class Location {
        public double x;
        public double y;
        public double facing;

        public Location(double x, double y, double facing) {
            this.x = x;
            this.y = y;
            this.facing = facing;
        }
    }

    public static class driveMode {
        public double spdFactor;
        public double xTolerance;
        public double yTolerance;
        public double hTolerance;

        public driveMode(double spdFactor, double xTolerance, double yTolerance, double hTolerance) {
            this.spdFactor = spdFactor;
            this.xTolerance = xTolerance;
            this.yTolerance = yTolerance;
            this.hTolerance = hTolerance;
        }
    }


    private double applyKS(double output, double kS) {
        if (Math.abs(output) < 1e-6) return 0;   // true zero stays zero
        return Math.signum(output) * Math.max(Math.abs(output), kS);
    }
    public boolean goto_xy(Location targetLocation, driveMode driveMode) {
        driveTrain.xControl.setSetPoint(targetLocation.x);
        driveTrain.yControl.setSetPoint(targetLocation.y);
        xOut = driveTrain.xControl.calculate(driveTrain.getXPosition());
        yOut = driveTrain.yControl.calculate(driveTrain.getYPosition());

        xOut *= driveMode.spdFactor;
        yOut *= driveMode.spdFactor;

        double kS_x = C2.AUTO_DRIVE_SPEED_MIN;
        double kS_y = C2.AUTO_DRIVE_SPEED_MIN;
        // When heading = 0/-180 then Y=STRAFE
        // When heading = 90/-90 the X=STRAFE
        if ((currentLocation.facing < 15 && currentLocation.facing > -15) || (currentLocation.facing > 145 || currentLocation.facing < -145)) {
            //kS_x = C2.AUTO_DRIVE_SPEED_MIN;
            kS_y = C2.AUTO_DRIVE_SPEED_MIN * C2.AUTO_DRIVE_SPEED_MIN_STRAFEFACTOR;
        } else if ((currentLocation.facing < 105 && currentLocation.facing > 75) || (currentLocation.facing > -105 && currentLocation.facing < -75)) {
            kS_x = C2.AUTO_DRIVE_SPEED_MIN * C2.AUTO_DRIVE_SPEED_MIN_STRAFEFACTOR;
            //kS_y = C2.AUTO_DRIVE_SPEED_MIN;
        }

        if (!at_x(targetLocation.x, driveMode))
            xOut = applyKS(xOut, kS_x);
        else
            xOut = 0;

        if (!at_y(targetLocation.y, driveMode))
            yOut = applyKS(yOut, kS_y);
        else
            yOut = 0;

        driveTrain.drive(xOut, yOut);
        joinedTelemetry.addData("X Speed",xOut);
        joinedTelemetry.addData("Y Speed",yOut);
        return at_xy(targetLocation, driveMode);
    }

    // ===== Enum Data Type for waypoint switch
    public enum WayPoints {
        MoveOffWall, PickupFarRow, Far_Shot, AimLongRedShoot, MoveOffWhite, SafePark, Done
    }

    private void updateIndicator() {
        // INDICATOR CASES
        switch (currentWayPoint) {
            case MoveOffWall:
                indicator.setColor(C2.RGB_Light.VIOLET);
                break;
            case PickupFarRow:
                indicator.setColor(C2.RGB_Light.YELLOW);
                break;
            case AimLongRedShoot:
                if (driveTrain.canLaunch(C2.FLYWHEEL_SPD_FAR_BLUE+C2.FLYWHEEL_SPD_FAR_BLUE_EXTRA)) {
                    indicator.setColor(C2.RGB_Light.GREEN);
                } else {
                    indicator.setColor(C2.RGB_Light.RED);
                }
                break;
            case Far_Shot:
                indicator.setColor(C2.RGB_Light.BLUE);
                break;
            case MoveOffWhite:
                indicator.setColor(C2.RGB_Light.ORANGE);
                break;
            case Done:
            default:
                indicator.setColor(C2.RGB_Light.WHITE);
        }
    }
    private void updateWayPointTimer() {
        if (currentWayPoint != lastWayPoint) {
            driveTrain.xControl.reset();
            driveTrain.yControl.reset();
            driveTrain.headingControl.reset();
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
