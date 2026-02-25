package org.firstinspires.ftc.teamcode.Mechanisms;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Auto.AutoBlueNear9ball;

@Config
public class DriveTrainBasic2 {
    // Basic config
    public HardwareMap hwMap;
    public GoBildaPinpointDriver odometer;
    public boolean autoEnabled = false;
    private double now = 0.0;
    public boolean modeRED = false;
    public boolean modeNEAR = false;
    private static final Location Blue_Near_Close_Shot = new Location(1000.0,0.0,0);
    public Location Location_Close_Shot = new Location(0.0, 0.0, 0.0);
    //Create Location object for currentLocation variable to hold current position read by odometry
    public Location currentLocation = new Location(0.0,0.0,0.0);
    private Location currentDestination = new Location(0.0,0.0,0.0);
    private driveMode dmPrecise = new driveMode(0.6767,4.670,4.670,1.50);
    private driveMode dmPickup = new driveMode(0.4567,4.670,4.670,1.50);
    public driveMode dmRough = new driveMode(0.8,20.670,20.670,4.50);
    // Holding object for current drive mode precision settings
    private driveMode currentDriveMode = new driveMode(0,0,0,0);
    // Drive Train Motors
    private final Motor leftFrontDrive;
    private final Motor rightFrontDrive;
    private final Motor leftBackDrive;
    private final Motor rightBackDrive;
    // Drive Train Control
    public MecanumDrive driveBase;
    public boolean drivemodefieldcentric = true; // True for fieldcentric False for robotcentric
    public PIDController headingControl = null;
    public static PIDCoefficients headingpid = new PIDCoefficients(0.019, 0.0, 0.0);
    public static PIDCoefficients xpid = new PIDCoefficients(0.005, 0.0, 0.001);
    public static PIDCoefficients ypid = new PIDCoefficients(0.005, 0.0, 0.001);
//    public static PIDCoefficients headingpid = new PIDCoefficients(Constants2.HEADING_Kp, 0.0001, 0.003);
//    public static PIDCoefficients xpid = new PIDCoefficients(Constants2.XPID_Kp, 0.09, 0.03);
//    public static PIDCoefficients ypid = new PIDCoefficients(Constants2.YPID_Kp, 0.12, 0.04);
    public PIDController xControl = null;
    public PIDController yControl = null;
    public double headingCorrection = 0;
    public double headingSetPoint = C2.FORWARD;
    public double heading;
    public boolean onHeading = false;
    private double currentSpeed = 0.0;
    private double currentXTarget = 0.0;
    private double currentYTarget = 0.0;
    double xSpeed = 0.0;
    double ySpeed = 0.0;
    public double headingError;
    public double turnSpeed = 0.0;
    private double maxTurnSpeed = 1.0;
    public boolean manual_FC_turning = false;

    // Shooter Configuration
    public final MotorEx Flywheel; //port 1 -expansion hub
    public final MotorEx feeder; //port 0-expansion hub
    public final Motor intake; //port 1-expansion hub
    public CRServo feed1;   //port 1
    public CRServo feed2;   //port 0
    private double shooterModeStartTime = 0.0;
    private ShooterMode lastShooterMode = ShooterMode.shooterOFF;
    public ShooterMode CurrentShooterMode = ShooterMode.shooterOFF;
    private double xOut = 0.0;
    private double yOut = 0.0;



    public DriveTrainBasic2(HardwareMap hwMap)
    {
        this.hwMap = hwMap;
        // Define and Initialize Motors (note: need to use reference to actual OpMode).
        leftFrontDrive = new Motor(hwMap, "left_front_drive"); // 0
        rightFrontDrive = new Motor(hwMap, "right_front_drive"); // 1
        leftBackDrive = new Motor(hwMap, "left_back_drive"); // 2
        rightBackDrive = new Motor(hwMap, "right_back_drive"); // 3
        driveBase = new MecanumDrive(leftFrontDrive, rightFrontDrive, leftBackDrive, rightBackDrive);
        // Flywheel assignment
        Flywheel = new MotorEx(hwMap, "shooter");// see https://docs.ftclib.org/ftclib/features/hardware/motors -- may have to add gobilda type
        feeder = new MotorEx(hwMap, "feeder");
        // Intake assignment
        intake = new Motor(hwMap, "intake");
        // Servo assignment
        feed1 = hwMap.get(CRServo.class,"feed1");
        feed2 = hwMap.get(CRServo.class,"feed2");

    }

    public void init() {
        if (!modeRED && modeNEAR) {
            Location_Close_Shot = Blue_Near_Close_Shot;
        }
        //Drive Motor Configuration
        headingControl = new PIDController(headingpid.p, headingpid.i, headingpid.d);
        headingControl.setTolerance(C2.HEADING_ERROR_Tolerance);// was 3 increased to see if affects spinnning ..cbw
        xControl = new PIDController(xpid.p, xpid.i, xpid.d);//FOR AUTO
        yControl = new PIDController(ypid.p, ypid.i, ypid.d);//For AUTO
        // redundant as default is brake mode
        leftBackDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        leftFrontDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        // Inverting because of the extra gear and not direct drive
        rightFrontDrive.setInverted(true);
        rightBackDrive.setInverted(true);
        leftFrontDrive.setInverted(true);
        leftBackDrive.setInverted(true);
        // odometer initializing 
        odometer = hwMap.get(GoBildaPinpointDriver.class, "xy-cord");
        odometer.setOffsets(C2.ODOMETER_X_OFFSET, C2.ODOMETER_Y_OFFSET);
        odometer.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        odometer.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        
        //set up for flywheel shooter
        Flywheel.setRunMode(Motor.RunMode.VelocityControl);
        Flywheel.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        Flywheel.setInverted(true);
        Flywheel.setVeloCoefficients(C2.FLYWHEEL_KP, C2.FLYWHEEL_KI, C2.FLYWHEEL_KD);     //coefficients are. kp, ki, and kd
        // Feeder configuration
        feeder.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        feeder.setInverted(false);
        // Intake configuration
        intake.setInverted(true);


    }// end of init()

    public void start() {

    }

    public void loop() {
        // - Update Inputs at the beginning of every loop
        odometer.update();
        Pose2D pos = odometer.getPosition();
        updateCurrentLocation(odometer.getPosition());
        heading = pos.getHeading(AngleUnit.DEGREES);

        // PID controller for heading
        if (autoEnabled) {
            manual_FC_turning = false;
        }
        if (!manual_FC_turning) {
            headingError = headingErrorDeg(heading, headingSetPoint);
            headingControl.setSetPoint(0);
            onHeading = headingWithinTolerance(heading, headingSetPoint, C2.HEADING_ERROR_Tolerance);
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
    public static double wrapDeg180(double a) {
        while (a > 180) a -= 360;
        while (a <= -180) a += 360;
        return a;
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

    public void printTelemetry(Telemetry telemetry) {
        //telemetry.addData("actual heading:", "%5.2f", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
        telemetry.addData("saved heading:", "%5.2f", heading);
        telemetry.addData("heading Target:", headingSetPoint);
        telemetry.addData("heading correction:", headingCorrection);
        telemetry.addData("X Distance inches:", "%5.2f", getXPosition());
        telemetry.addData("Y Distance inches:", "%5.2f", getYPosition());
        telemetry.addData("Auto DriveTo Enabled", autoEnabled);

       // telemetry.addData("elevator","%5.2f",elevator.getCurrentPosition());

        if(autoEnabled) {
            telemetry.addData("Auto target X:", "%5.2f", currentXTarget);
            telemetry.addData("Auto target Y:", "%5.2f", currentYTarget);
            telemetry.addData("heading OnTarget:", onHeading);
            telemetry.addData("headingCorrection:", headingCorrection);
        }
        telemetry.update();
    }

    public boolean canLaunch(double launchSpeed,  double FudgeFactor){
        return Flywheel.getCorrectedVelocity() >= (launchSpeed * C2.FLYWHEEL_MAX) + FudgeFactor;
    }
    public boolean canLaunch(double launchSpeed){
        return Flywheel.getCorrectedVelocity() >= (launchSpeed * C2.FLYWHEEL_MAX);
    }

    public void setDrivemodefieldcentric(){
        this.drivemodefieldcentric = true;
    }
    public void setDrivemoderobotcentric(){
        this.drivemodefieldcentric = false;
        manual_FC_turning = false;
    }
    public void turn(double turnDirection){
        // Check turnDirection speed for max
        if ((Math.abs(turnDirection) <= this.maxTurnSpeed)) {
            this.turnSpeed = turnDirection;
        } else if (turnDirection >= 0.0) {
            this.turnSpeed = this.maxTurnSpeed;
        } else {
            this.turnSpeed = -this.maxTurnSpeed;
        }
    }
    public enum ShooterMode {
        shooterSHOOTINGfarBlue, shooterSHOOTINGfarRedWithCorrection, shooterSHOOTINGfarRedNOcorrection, shooterSHOOTINGnear, shooterSHOOTINGnearNOcorrection, shooterPICKUP, shooterCORRECTING, shooterHOLDING, shooterOFF
    }

    public void ShooterControlLoop() {
        // Reset Shooter Mode Start Timer when mode changes
        if (CurrentShooterMode != lastShooterMode) {
            onShooterModeChange();
            lastShooterMode = CurrentShooterMode;
        }

        switch (CurrentShooterMode) {
            // shooterMotors: flywheelSpeed intakeSpeed feederSpeed feed1/2Speed
            case shooterSHOOTINGfarBlue:
                // shooter delayed for feeder reverse to correct ball position
                if (!shooterModeActiveForMoreThan(C2.CORRECTION_DELAY)) { // input delay seconds
                    shooterMotors(C2.FLYWHEEL_SPD_REVERSE, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                } else {
                    shooterMotors(C2.FLYWHEEL_SPD_FAR_BLUE, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                    if (canLaunch(C2.FLYWHEEL_SPD_FAR_BLUE,C2.FLYWHEEL_SPD_FAR_BLUE_EXTRA)) {
                        shooterMotors(C2.FLYWHEEL_SPD_FAR_BLUE, C2.INTAKE_SPD_SHOOTING, C2.FEEDER_SPD_FAR, C2.FEED_SPD_FWD);
                    }
                } break;
            case shooterSHOOTINGfarRedWithCorrection:
                // shooter delayed for feeder reverse to correct ball position
                if (!shooterModeActiveForMoreThan(C2.CORRECTION_DELAY)) { // input delay seconds
                    shooterMotors(C2.FLYWHEEL_SPD_REVERSE, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                } else {
                    if (canLaunch(C2.FLYWHEEL_SPD_FAR_RED)) {
                        shooterMotors(C2.FLYWHEEL_SPD_FAR_RED, C2.INTAKE_SPD_SHOOTING, C2.FEEDER_SPD_FAR, C2.FEED_SPD_FWD);
                    } else {
                        shooterMotors(C2.FLYWHEEL_SPD_FAR_RED, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                    }
                } break;
            case shooterSHOOTINGfarRedNOcorrection:
                if (canLaunch(C2.FLYWHEEL_SPD_FAR_RED)) {
                    shooterMotors(C2.FLYWHEEL_SPD_FAR_RED, C2.INTAKE_SPD_SHOOTING, C2.FEEDER_SPD_FAR, C2.FEED_SPD_FWD);
                } else {
                    shooterMotors(C2.FLYWHEEL_SPD_FAR_RED, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                }
                break;
            case shooterSHOOTINGnear:
                // shooter delayed for feeder reverse to correct ball position
                if (!shooterModeActiveForMoreThan(C2.CORRECTION_DELAY)) { // input delay seconds
                    shooterMotors(C2.FLYWHEEL_SPD_REVERSE, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                } else {
                    shooterMotors(C2.FLYWHEEL_SPD_NEAR, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                    if (canLaunch(C2.FLYWHEEL_SPD_NEAR)) {
                        shooterMotors(C2.FLYWHEEL_SPD_NEAR, C2.INTAKE_SPD_SHOOTING, C2.FEEDER_SPD_NEAR, C2.FEED_SPD_FWD);
                    }
                } break;

            case shooterSHOOTINGnearNOcorrection:
                if (canLaunch(C2.FLYWHEEL_SPD_NEAR)) {
                    shooterMotors(C2.FLYWHEEL_SPD_NEAR, C2.INTAKE_SPD_SHOOTING, C2.FEEDER_SPD_NEAR, C2.FEED_SPD_FWD);
                } else {
                    shooterMotors(C2.FLYWHEEL_SPD_NEAR, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                }
                 break;

            case shooterPICKUP:
                shooterMotors(C2.FLYWHEEL_SPD_REVERSE, C2.INTAKE_SPD_PICKUP, C2.FEEDER_SPD_PICKUP, C2.FEED_SPD_REVERSE);
                break;

            case shooterCORRECTING:
                shooterMotors(C2.FLYWHEEL_SPD_REVERSE, C2.INTAKE_SPD_CORRECTING, C2.FEEDER_SPD_CORRECTING, C2.FEED_SPD_REVERSE);
                break;

            case shooterHOLDING:
                shooterMotors(C2.FLYWHEEL_SPD_HOLDING, C2.INTAKE_SPD_HOLDING, C2.FEEDER_SPD_HOLDING, C2.FEED_SPD_REVERSE);
                break;

            case shooterOFF:
            default:
                shooterMotors(0,0,0,0);
                break;
        }//END SHOOTER MODE CASE/SELECT
    }// END SHOOTER CONTROL LOOP

    // Shooter Helper Methods/Functions
    public void shooterMotors(double flywheelSpeed, double intakeSpeed, double feederSpeed, double feed1Speed){
        feeder.set(feederSpeed);
        feed1.setPower(feed1Speed);
        feed2.setPower(feed1Speed);
        intake.set(intakeSpeed);
        Flywheel.set(flywheelSpeed);
    }
    public void setNow(double nowSeconds) {
        now = nowSeconds;
    }
    private void onShooterModeChange() {
        shooterModeStartTime = now;

    }

    public boolean shooterModeActiveForMoreThan(double seconds) {
        return (now - shooterModeStartTime) >= seconds;
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
    public boolean goto_xy(Location targetLocation, driveMode driveMode) {
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



    public boolean at_x(double destinationX, driveMode driveMode) {
        //return (Math.abs(destinationX - getXPosition())) <= C2.AUTO_X_DISTANCE_ERROR;
        return (Math.abs(destinationX - getXPosition())) <= driveMode.xTolerance;
    }

    public boolean at_y(double destinationY, driveMode driveMode) {
        //return (Math.abs(destinationY - getYPosition())) <= C2.AUTO_Y_DISTANCE_ERROR;
        return (Math.abs(destinationY - getYPosition())) <= driveMode.yTolerance;
    }

    public boolean at_xy(Location destinationLocation, driveMode driveMode) {
            return at_x(destinationLocation.x, driveMode) && at_y(destinationLocation.y, driveMode) && onHeading;
    }

    public void updateCurrentLocation(Pose2D pos) {
        currentLocation.x = -pos.getX(DistanceUnit.MM);
        currentLocation.y = -pos.getY(DistanceUnit.MM);
        currentLocation.facing = pos.getHeading(AngleUnit.DEGREES);
    }
}