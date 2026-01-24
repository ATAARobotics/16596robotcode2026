package org.firstinspires.ftc.teamcode.Mechanisms;

import static java.lang.Runtime.getRuntime;

import android.widget.Spinner;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.hardware.motors.MotorGroup;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@Config
public class DriveTrainBasic {

    // Driving Motors
    private final Motor leftFrontDrive;
    private final Motor rightFrontDrive;
    private final Motor leftBackDrive;
    private final Motor rightBackDrive;
    // Shooter Motors
    public final MotorEx shooter; //port 0-expansion hub
   // public final MotorEx shooter2; //port 2-expansion-hub
    //public final MotorGroup flywheel; // assigned shooter and shooter2
    // Intake Motor
    public final Motor intake; //port 1-expansion hub
    // Servos
    public CRServo feed1;   //port 1
    public CRServo feed2;   //port 0

    public MecanumDrive driveBase;

    public GoBildaPinpointDriver odometer;
    public PIDController headingControl = null;
    public static PIDCoefficients headingpid = new PIDCoefficients(Constants.HEADING_Kp, 0.001, 0.000);
    public static PIDCoefficients xpid = new PIDCoefficients(Constants.XPID_Kp, 0.0, 0.00);
    public static PIDCoefficients ypid = new PIDCoefficients(Constants.YPID_Kp, 0.0, 0.00);
    public PIDController xControl = null;
    public PIDController yControl = null;
    public double headingCorrection = 0;

    public double headingSetPoint = Constants.FORWARD;
    public double heading;
    // Set drive mode
    private boolean drivemodefieldcentric = true; // True for fieldcentric False for robotcentric

    private boolean autoEnabled = false;

    public HardwareMap hwMap;
    private double currentSpeed = 0.0;
    private double currentXTarget = 0.0;
    private double currentYTarget = 0.0;
    double xSpeed = 0.0;
    double ySpeed = 0.0;
    public double headingError;
    public double turnSpeed = 0.0;
    private double maxTurnSpeed = 1.0;


    public DriveTrainBasic(HardwareMap hwMap)
    {
        this.hwMap = hwMap;
        // Define and Initialize Motors (note: need to use reference to actual OpMode).
        leftFrontDrive = new Motor(hwMap, "left_front_drive"); // 0
        rightFrontDrive = new Motor(hwMap, "right_front_drive"); // 1
        leftBackDrive = new Motor(hwMap, "left_back_drive"); // 2
        rightBackDrive = new Motor(hwMap, "right_back_drive"); // 3
        driveBase = new MecanumDrive(leftFrontDrive, rightFrontDrive, leftBackDrive, rightBackDrive);
        // Flywheel assignment
        shooter = new MotorEx(hwMap, "shooter");// see https://docs.ftclib.org/ftclib/features/hardware/motors -- may have to add gobilda type
       // shooter2 = new MotorEx(hwMap, "shooter2");
       // flywheel = new MotorGroup(shooter, shooter2);
        // Intake assignment
        intake = new Motor(hwMap, "intake");
        // Servo assignment
        feed1 = hwMap.get(CRServo.class,"feed1");
        feed2 = hwMap.get(CRServo.class,"feed2");

    }

    public void init() {
        headingControl = new PIDController(headingpid.p, headingpid.i, headingpid.d);
        headingControl.setTolerance(Constants.HEADING_ERROR_Tolerance);// was 3 increased to see if affects spinnning ..cbw
        xControl = new PIDController(xpid.p, xpid.i, xpid.d);//FOR AUTO
        yControl = new PIDController(ypid.p, ypid.i, ypid.d);//For AUTO
        // Intake configuration
        intake.setInverted(true);
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
        // odometer initializing -- this should go into PracticeDriveTrain2025??
        odometer = hwMap.get(GoBildaPinpointDriver.class, "xy-cord");
        odometer.setOffsets(Constants.ODOMETER_X_OFFSET, Constants.ODOMETER_Y_OFFSET);
        odometer.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        odometer.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        //set up for two shooter motors
        shooter.setRunMode(Motor.RunMode.VelocityControl);
        shooter.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
       // shooter2.setRunMode(Motor.RunMode.VelocityControl);
      //  shooter2.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
       // shooter2.setInverted(true);
        // configuring flywheel mo
       // flywheel.setRunMode(Motor.RunMode.VelocityControl);// motor group of the 2 shooter motors
        shooter.setVeloCoefficients(Constants.FLYWHEEL_KP,Constants.FLYWHEEL_KI, Constants.FLYWHEEL_KD);     //coefficients are. kp, ki, and kd
    }// end of init()

    public void start() {

    }

    public void loop() {
        //heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        odometer.update();      // needs to be called once per loop
        Pose2D pos = odometer.getPosition();
        heading = pos.getHeading(AngleUnit.DEGREES);
        // Because it's a double, can't check for exactly 180, so we check if it's almost 180 in either direction.
        if (Math.abs(Math.abs(headingSetPoint) - 180.0) < Constants.HEADING_ERROR_Tolerance) {
            // "south" is special because it's around the 180/-180 toggle-point
            // Change set-point between 180/-180 depending on which is closer.
            if (heading < 0.0) {
                headingSetPoint = -180;
            } else {
                headingSetPoint = 180;
            }
        }

        // PID controller for heading
        headingControl.setSetPoint(headingSetPoint);
        headingCorrection = -headingControl.calculate(heading);// confirm if (-) is needed.

        headingError = Math.abs(headingSetPoint - heading);

        //temporary test code
        if (headingError <= Constants.HEADING_ERROR_Tolerance) {
            headingCorrection = 0;
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
    }// end of loop()

//    public boolean atTarget() { // Pythagorean theorem to get the distance from target as a number.
//        double xError = getXPosition() - currentXTarget;
//        double yError = getYPosition() - currentYTarget;
//        return !autoEnabled || Math.sqrt(xError * xError + yError * yError) < Constants.DRIVE_PID_ERROR;
//    }

    //============== Move in new Direction ==========
    public void setDirection(double newHeading) {
        headingSetPoint = newHeading;
    }
    public void setFacing(double newHeading) { headingSetPoint = newHeading; }
    public boolean onHeading() {
        return Math.abs(heading - headingSetPoint) < Constants.HEADING_ERROR_Tolerance;

    }
    public void driveTo(double speed, double xDist, double yDist, double facing) {
        autoEnabled = true;
        currentSpeed = speed;
        currentXTarget = xDist;
        currentYTarget = yDist;
        setDirection(facing);

        xControl.setSetPoint(currentXTarget);
        yControl.setSetPoint(currentYTarget);
    }
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
        shooter.set(0.0);
        feed1.setPower(0.0);
        feed2.setPower(0.0);
        intake.set(0);
        driveBase.stop();
        autoEnabled = false;
    }
    public void stopFlyWheel(){
        shooter.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
       // shooter2.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        shooter.set(0.0);
       // shooter2.set(0.0);
        shooter.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        //shooter2.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
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
            telemetry.addData("heading OnTarget:", onHeading());
          //  telemetry.addData("lateral OnTarget:", atTarget());
            telemetry.update();
        }
    }
    public boolean canLaunch(double launchSpeed){
        return shooter.getVelocity() >= (0.8 * launchSpeed * Constants.FLYWHEEL_MAX);
        //return true;// need to fix for comp
    }
    public double convert360(double angle){
        if (angle > 0){
            return 360.0 - angle;
        }
        return Math.abs(angle);
    }
    public void setDrivemodefieldcentric(){
        this.drivemodefieldcentric = true;
    }
    public void setDrivemoderobotcentric(){
        this.drivemodefieldcentric = false;
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
}