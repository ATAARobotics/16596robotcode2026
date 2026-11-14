package org.firstinspires.ftc.teamcode.Subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import org.firstinspires.ftc.teamcode.Mechanisms.Old.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Utils.Constant;

@Config
public class DriveTrain2027 {
    public HardwareMap hwMap;
    public GoBildaPinpointDriver odometer;

    public boolean autoEnabled = false;
    private double now = 0.0;

    // Drive Train Motors
    private Motor leftFrontDrive;
    private Motor rightFrontDrive;
    private Motor leftBackDrive;
    private Motor rightBackDrive;
    // Drive Train Control
    public MecanumDrive driveBase;
    public boolean drivemodefieldcentric = true; // True for fieldcentric False for robotcentric

    public PIDController headingControl = null;
    public static PIDCoefficients headingpid = new PIDCoefficients(0.019, 0.0, 0.0);
    public static PIDCoefficients xpid = new PIDCoefficients(0.005, 0.0, 0.001);
    public static PIDCoefficients ypid = new PIDCoefficients(0.005, 0.0, 0.001);

    public PIDController xControl = null;
    public PIDController yControl = null;
    public double headingCorrection = 0;
    public double headingSetPoint = Constant.DRIVE_FORWARD;
    public double heading;
    public boolean onHeading = false;
    private double currentSpeed = 0.0;
    private double currentXTarget = 0.0;
    private double currentYTarget = 0.0;
    private double xSpeed = 0.0;
    private double ySpeed = 0.0;
    public double headingError;
    public double turnSpeed = 0.0;
    public boolean manual_FC_turning = false;

    public DriveTrain2027(HardwareMap hwMap)
    {
        this.hwMap = hwMap;
        this.assignMotors();
        this.initializeMotors();

        driveBase = new MecanumDrive(leftFrontDrive, rightFrontDrive, leftBackDrive, rightBackDrive);


    }

    public void init() {
        //Drive Motor Configuration
        headingControl = new PIDController(headingpid.p, headingpid.i, headingpid.d);
        headingControl.setTolerance(Constant.AUTO_HEADING_ERROR_TOLERANCE);// was 3 increased to see if affects spinnning ..cbw
        xControl = new PIDController(xpid.p, xpid.i, xpid.d);//FOR AUTO
        yControl = new PIDController(ypid.p, ypid.i, ypid.d);//For AUTO
        // odometer initializing
   /*     odometer = hwMap.get(GoBildaPinpointDriver.class, "xy-cord");
        odometer.setOffsets(Constant.ODOMETER_X_OFFSET, Constant.ODOMETER_Y_OFFSET);
        odometer.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        odometer.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
*/
    }// end of init()

    public void start() {

    }

    public void loop() {
        // - Update Inputs at the beginning of every loop
  /*      odometer.update();
        Pose2D pos = odometer.getPosition();
   */
       // updateCurrentLocation(odometer.getPosition());
   //     heading = pos.getHeading(AngleUnit.DEGREES);

        // PID controller for heading
        if (autoEnabled) {
            manual_FC_turning = false;
        }
        if (!manual_FC_turning) {
            headingError = headingErrorDeg(heading, headingSetPoint);
            headingControl.setSetPoint(0);
            onHeading = headingWithinTolerance(heading, headingSetPoint, Constant.AUTO_HEADING_ERROR_TOLERANCE);
            if (!onHeading){
                headingCorrection = -headingControl.calculate(headingError);
                double kS_turn = 0.2;   // tune separately
                headingCorrection = applyKS(headingCorrection, kS_turn);
            } else {
                headingCorrection = 0.0;
            }
        }

        if (drivemodefieldcentric) {
            driveBase.driveFieldCentric(
                    xSpeed,// strafe
                    ySpeed,//forward
                    headingCorrection,// turn
                    heading,// heading
                    false);
        }
        else {
            driveBase.driveRobotCentric(-xSpeed,-ySpeed,turnSpeed);
        }
    }// end of main drivetrain loop()
    // Angle Wrapping
    // Returns error in [-180, 180], where 0 means "on target"
    private double applyKS(double output, double kS) {
        if (Math.abs(output) < 1e-6) return 0;   // true zero stays zero
        return Math.signum(output) * Math.max(Math.abs(output), kS);
    }
    public static double headingErrorDeg(double currentDeg, double targetDeg) {
        return wrapDeg180(currentDeg - targetDeg);
    }

    // Wrap any angle to (-180, 180]
    public static double wrapDeg180(double angle) {
        while (angle > 180) angle -= 360;
        while (angle <= -180) angle += 360;
        return angle;
    }
    public boolean headingWithinTolerance(double currentDeg,
                                          double targetDeg,
                                          double toleranceDeg) {

        double error = wrapDeg180(targetDeg - currentDeg);
        return Math.abs(error) <= toleranceDeg;
    }


    //============== Move in new Direction ==========
    public void setDirection(double newHeading) {
        headingSetPoint = newHeading;
    }
    public void setFacing(double newHeading) { headingSetPoint = newHeading; }

    public void drive(double forwardSpeed,  double strafeSpeed) {
        // tell ftclib its inputs  strafeSpeed,forwardSpeed,turn,heading
        // turn and heading are managed in loop with the heading control PID
        // inverting strafe speed to correct directions, inverting no longer required for telemtry pdos have been turned to right position
        xSpeed = -strafeSpeed;
        ySpeed = forwardSpeed;
    }
    public double getXPosition() { // Convert xPod into actual direction based on current heading
        Pose2D pos = odometer.getPosition();
        return -pos.getX(DistanceUnit.MM);// pod mounted backwards
    }
    public double getYPosition() { // Convert yPod into actual direction based on current heading
        Pose2D pos = odometer.getPosition();
        return -pos.getY(DistanceUnit.MM);// pod mounted backwards
    }

    public void stop() {
        xSpeed = 0.0;
        ySpeed = 0.0;
        driveBase.stop();
        autoEnabled = false;
    }

    public void setDrivemodefieldcentric(){this.drivemodefieldcentric = true;}
    public void setDrivemoderobotcentric(){this.drivemodefieldcentric = false;}

    public void turn(double turnDirection){
        // Check turnDirection speed for max
        if ((Math.abs(turnDirection) <= Constant.MAX_TURN_SPEED)) {
            this.turnSpeed = turnDirection;
        } else if (turnDirection >= 0.0) {
            this.turnSpeed = Constant.MAX_TURN_SPEED;
        } else {
            this.turnSpeed = -Constant.MAX_TURN_SPEED;
        }
    }


    public void setNow(double nowSeconds) {
        now = nowSeconds;
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
/*    public boolean goto_xy(Location targetLocation, driveMode driveMode) {
        xControl.setSetPoint(targetLocation.x);
        yControl.setSetPoint(targetLocation.y);
        xOut = xControl.calculate(getXPosition());
        yOut = yControl.calculate(getYPosition());

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

        drive(xOut, yOut);
        return at_xy(targetLocation, driveMode);
    }*/
    /*public static class driveMode {
        public double spdFactor;
        public double xTolerance;
        public double yTolerance;
        public double hTolerance;

        public driveMode(double spdFactor, double xTolerance, double yTolerance, double hTolerance) {
            this.spdFactor = spdFactor;
            this.xTolerance = xTolerance;
            this.yTolerance = yTolerance;
            this.hTolerance = hTolerance;
    }*/

    private void assignMotors(){
        // Assign the motors
        this.leftFrontDrive = new Motor(hwMap, "left_front_drive"); // 0
        this.rightFrontDrive = new Motor(hwMap, "right_front_drive"); // 1
        this.leftBackDrive = new Motor(hwMap, "left_back_drive"); // 2
        this.rightBackDrive = new Motor(hwMap, "right_back_drive"); // 3
    }

    private void initializeMotors(){
        // Initialize Motors
        this.leftFrontDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        this.leftBackDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        this.rightFrontDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        this.rightBackDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        // Inverting, since it isn't direct drive
        this.leftFrontDrive.setInverted(true);
        this.leftBackDrive.setInverted(true);
        this.rightFrontDrive.setInverted(true);
        this.rightBackDrive.setInverted(true);
    }

/*

    public boolean at_x(double destinationX, driveMode driveMode) {
        //return (Math.abs(destinationX - getXPosition())) <= C2.AUTO_X_DISTANCE_ERROR;
        return (Math.abs(destinationX - getXPosition())) <= driveMode.xTolerance;
    }
*/
    /*
    public boolean at_y(double destinationY, driveMode driveMode) {
        //return (Math.abs(destinationY - getYPosition())) <= C2.AUTO_Y_DISTANCE_ERROR;
        return (Math.abs(destinationY - getYPosition())) <= driveMode.yTolerance;
    }*/
/*
    public boolean at_xy(Location destinationLocation, driveMode driveMode) {
        return at_x(destinationLocation.x, driveMode) && at_y(destinationLocation.y, driveMode) && onHeading;
    }
    */
/*
    public void updateCurrentLocation(Pose2D pos) {
        currentLocation.x = -pos.getX(DistanceUnit.MM);
        currentLocation.y = -pos.getY(DistanceUnit.MM);
        currentLocation.facing = pos.getHeading(AngleUnit.DEGREES);
    }
    */

}