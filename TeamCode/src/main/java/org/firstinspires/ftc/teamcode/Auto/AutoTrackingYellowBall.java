package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.command.RunCommand;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.C2;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic2;
import org.firstinspires.ftc.teamcode.Subsystem.DriveTrainSubsystem;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

@Autonomous
public class AutoTrackingYellowBall extends OpMode {
    private double atTargetStartTime = -1;
    private static final double AT_TARGET_HOLD_TIME = 0.5; // seconds (500ms)
    private DriveTrainSubsystem driveTrain1;

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

    private Limelight3A limelight3A;
    ElapsedTime forwardTimer = new ElapsedTime();
    boolean timerStarted = false;


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
        limelight3A = hardwareMap.get(Limelight3A.class, "lime_light" );
        limelight3A.pipelineSwitch(1);//Blue
    }

    @Override
    public void start() {
        startTime = getRuntime();
        limelight3A.start();
    }
    boolean reachedBall = false;
    boolean foundBall = false;
    boolean finishedForward = false;
    double ballY = 0;

    @Override
    public void loop() {
        driveTrain.loop();
        updateCurrentLocation(driveTrain.odometer.getPosition());
        currentTime = getRuntime() - startTime;
        driveTrain.setNow(currentTime);
        updateWayPointTimer();
        Pose2D pos = driveTrain.odometer.getPosition();

        driveTrain.drive(0.0, 0.0);
        LLResult llResult = limelight3A.getLatestResult();

        if (llResult != null && llResult.isValid()) {
            llResult.isValid();
            telemetry.addData("target x offset", llResult.getTx());
            telemetry.addData("target y offset", llResult.getTy());
            telemetry.addData("target area offset", llResult.getTa());
            driveTrain.drive(0.0 + (llResult.getTy() / 10), 0.0 + llResult.getTx() / -20);
        }

    }

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