package org.firstinspires.ftc.teamcode.Subsystem;

import static com.qualcomm.robotcore.hardware.Servo.Direction.FORWARD;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.rev.RevColorSensorV3;
//import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Utils.Constant;

@Configurable
@Config
public class Mechanisms2027 {
    private HardwareMap hwMap;
    private Mode shooterMode = Mode.STOP;
    private ballDetecting ballDetected = ballDetecting.NONE;
    //========== Motors =============================================
    private DcMotorEx shooterRight;
    private DcMotorEx shooterLeft;
    private MotorEx feeder;
    private Motor intake;

    //========== Servos =============================================
    private Servo shooterNectarLift;
    private Servo shooterRamp;
    private Servo shooterStopper;
    //========== Sensors ============================================
    private RevColorSensorV3 shooterColorSensor;
    //private NormalizedColorSensor shooterColorSensor;
    private DistanceSensor shooterProximity;
    //private DistanceSensor shooterColorDistance;
    //========== Time ===============================================
    private double now = 0.0;
    private double shooterModeStartTime = 0.0;
    private double shooterSpinUpTime = -1.0;
    private boolean shooterReachedSpeed = false;



    public Mechanisms2027(HardwareMap hwMap) {
        this.hwMap = hwMap;
        //================================ Motors ============================================
        assignMotors();
        //================================ Servos ============================================
        assignServos();
        //================================ Sensors ============================================
        assignSensors();
    }

    public void init() {
        initializeMotors();
    }// end of init()

    public void start() {

    }

    public void loop() {

        // Shooter Modes
        // shooterSpeed, double intakeSpeed, double feederSpeed, double shooterNectarLiftPosition, double shooterRampPosition
        // shooterStopperPosition
        switch (shooterMode){
            //========== PICKUP/INTAKE ===================================
            case PICKUP:
                // Intake/pickup mode:
                // stop (or reverse?) shooter while running intake and feeder
                // intake servo down, ramp servo mid, shooter door closed

                setOperatorMotors(
                        Constant.SHOOTER_RPM_HOLDING, // Speed - Shooter Flywheels
                        Constant.INTAKE_SPEED_PICKUP,   // Speed - Intake Motor
                        Constant.FEEDER_SPEED_PICKUP,   // Speed - Feeder Motor
                        Constant.NECTAR_LIFT_DOWN,      // Position - Nectar Lift
                        Constant.SHOOTER_RAMP_DOWN,     // Position - Shooter Ramp
                        Constant.SHOOTER_STOPPER_CLOSED // Position - Shooter Stopper
                );

                break;
            //========== SHOOTING ===================================
            case SHOOTING: // Shooting
                if (!ShooterModeActiveForMoreThan(Constant.CORRECTION_DELAY)) { // input delay seconds
                    setOperatorMotors(
                            Constant.SHOOTER_RPM_HOLDING,   // Speed - Shooter Flywheels
                            Constant.INTAKE_SPEED_HOLDING,    // Speed - Intake Motor
                            Constant.FEEDER_SPEED_CORRECTING, // Speed - Feeder Motor
                            Constant.NECTAR_LIFT_DOWN,        // Position - Nectar Lift
                            Constant.SHOOTER_RAMP_DOWN,       // Position - Shooter Ramp
                            Constant.SHOOTER_STOPPER_CLOSED   // Position - Shooter Stopper
                    );
                } else {
                    setOperatorMotors(
                            Constant.SHOOTER_RPM_SHOOTING,   // Speed - Shooter Flywheels
                            Constant.INTAKE_SPEED_HOLDING,    // Speed - Intake Motor
                            Constant.FEEDER_SPEED_CORRECTING, // Speed - Feeder Motor
                            Constant.NECTAR_LIFT_DOWN,        // Position - Nectar Lift
                            Constant.SHOOTER_RAMP_DOWN,       // Position - Shooter Ramp
                            Constant.SHOOTER_STOPPER_CLOSED   // Position - Shooter Stopper
                    );
                    if (canLaunch(Constant.SHOOTER_RPM_SHOOTING)) {
                        setOperatorMotors(
                                Constant.SHOOTER_RPM_SHOOTING,  // Speed - Shooter Flywheels
                                Constant.INTAKE_SPEED_PICKUP,     // Speed - Intake Motor
                                Constant.FEEDER_SPEED_PICKUP,     // Speed - Feeder Motor
                                Constant.NECTAR_LIFT_DOWN,        // Position - Nectar Lift
                                Constant.SHOOTER_RAMP_UP,         // Position - Shooter Ramp
                                Constant.SHOOTER_STOPPER_OPEN     // Position - Shooter Stopper
                        );
                    }
                }
                break;
            //========== STOP ===================================
            case SHOOT_TEST: // Off
                setOperatorMotors(
                        Constant.SHOOTER_RPM_SHOOTING,    // Speed - Shooter Flywheels
                        Constant.INTAKE_SPEED_STOP,     // Speed - Intake Motor
                        Constant.FEEDER_SPEED_STOP,     // Speed - Feeder Motor
                        Constant.NECTAR_LIFT_DOWN,      // Position - Nectar Lift
                        //Constant.SHOOTER_RAMP_DOWN,     // Position - Shooter Ramp
                        getShooterRampTargetPosition(),
                        Constant.SHOOTER_STOPPER_CLOSED // Position - Shooter Stopper
                );
                break;

            //========== STOP ===================================
            case STOP: // Off
            default:
                setOperatorMotors(
                        Constant.SHOOTER_SPEED_STOP,    // Speed - Shooter Flywheels
                        Constant.INTAKE_SPEED_STOP,     // Speed - Intake Motor
                        Constant.FEEDER_SPEED_STOP,     // Speed - Feeder Motor
                        Constant.NECTAR_LIFT_DOWN,      // Position - Nectar Lift
                        Constant.SHOOTER_RAMP_IDLE,     // Position - Shooter Ramp
                        Constant.SHOOTER_STOPPER_CLOSED // Position - Shooter Stopper
                );
                break;
        } // Switch shooterMode

        //========== Shooter Spin-Up Timing ==============================
        if ((shooterMode == Mode.SHOOTING
                || shooterMode == Mode.SHOOT_TEST)
                && !shooterReachedSpeed) {

            if (canLaunch(Constant.SHOOTER_RPM_SHOOTING)) {
                shooterSpinUpTime = now - shooterModeStartTime;
                shooterReachedSpeed = true;
            }
        } // Shooter Spin-Up Timing

    } // Loop


    //========== Sensor Functions ===================================
    public ballDetecting detectBall() {
        double colorDist = getColorDistance();
        boolean nectarDetected = detectNectar();

        if (colorDist > 75.0) {
            return ballDetecting.NONE;
        } else if (nectarDetected) {
            return ballDetecting.NECTAR;
        } else {
            return ballDetecting.POLLEN;
        }
    }
    public boolean detectNectar() {
        NormalizedRGBA colors = this.shooterColorSensor.getNormalizedColors();
        return (((colors.red > colors.green
                && colors.red > colors.blue)
                || (colors.blue > colors.green
                && colors.blue > colors.red))
                && (colors.red > Constant.COLOR_THRESHOLD
                || colors.blue > Constant.COLOR_THRESHOLD
                || colors.green > Constant.COLOR_THRESHOLD));
    }

    //========== Class Get Sections =================================
    public double getProxDistance(){
        return this.shooterProximity.getDistance(DistanceUnit.MM);
    }
    public double getColorDistance() {
        return this.shooterColorSensor.getDistance(DistanceUnit.MM);
    }

    public Mode getMode(){return this.shooterMode;}
    public double getShooterRightCurrent() {
        return shooterRight.getCurrent(CurrentUnit.AMPS);
    }

    public double getShooterLeftCurrent() {
        return shooterLeft.getCurrent(CurrentUnit.AMPS);
    }

    public double getShooterSpinUpTime() {
        return shooterSpinUpTime;
    }

    public boolean isShooterReady() {
        return shooterReachedSpeed;
    }

    //========== Class Set Section ==================================
    public void setMode(Mode mode){
        if (mode != shooterMode) {
            shooterMode = mode;
            onShooterModeChange();
            }
        }
    public void setNow(double nowSeconds) {
        this.now = nowSeconds;
    }
    //========== Class Types ========================================
    public enum Mode {
        PICKUP,
        SHOOTING,
        SHOOT_TEST,
        STOP
    }
    public enum ballDetecting {
        NECTAR,
        POLLEN,
        NONE
    }
    // Added Shooter State Class for future implementation
    private enum shooterState {
        IDLE,
        PICKUP,
        CORRECTING,
        SPINNING_UP,
        READY,
        FEEDING
    }
    //========== Class Functions ====================================
    private void onShooterModeChange() {

        this.shooterModeStartTime = this.now;
        shooterSpinUpTime = -1.0;
        shooterReachedSpeed = false;
    }
    private boolean ShooterModeActiveForMoreThan(double seconds) {
        return (this.now - this.shooterModeStartTime) >= seconds;
    }
    private void setOperatorMotors(double shooterRPM, double intakeSpeed, double feederSpeed, double shooterNectarLiftPosition, double shooterRampPosition, double shooterStopperPosition){
        this.feeder.set(feederSpeed);
        this.intake.set(intakeSpeed);
        setShooterRPM(shooterRPM);
        this.shooterNectarLift.setPosition(shooterNectarLiftPosition);
        this.shooterRamp.setPosition(shooterRampPosition);
        this.shooterStopper.setPosition(shooterStopperPosition);

    }
    //========== Assign functions ===================================
    private void assignMotors(){
        shooterRight = this.hwMap.get(DcMotorEx.class, "ShooterRight");
        shooterLeft = this.hwMap.get(DcMotorEx.class, "ShooterLeft");
        this.feeder = new MotorEx(this.hwMap, "Feeder");
        this.intake = new MotorEx(this.hwMap, "Intake");
    }
    private void assignServos() {
        this.shooterNectarLift = this.hwMap.get(Servo.class, "ShooterNectarLift");
        this.shooterRamp = this.hwMap.get(Servo.class, "ShooterRamp");
        this.shooterStopper = this.hwMap.get(Servo.class, "ShooterStopper");
    }
    private void assignSensors(){
        shooterColorSensor = hwMap.get(RevColorSensorV3.class, "ShooterColorSensor");
        shooterProximity = hwMap.get(DistanceSensor.class, "ShooterProximity");
        //shooterColorDistance = hwMap.get(DistanceSensor.class, "ShooterColorSensor");
    }
    // Initialize functions
    private void  initializeMotors(){
        //========== Shooter Motors ======================================
        shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //shooterRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterRight.setDirection(DcMotor.Direction.FORWARD);

        shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //shooterLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterLeft.setDirection(DcMotor.Direction.REVERSE);

        // Feeder
        this.feeder.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        this.feeder.setInverted(false);
        // Intake
        this.intake.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        this.intake.setInverted(true);
        // Servos
        this.shooterNectarLift.setDirection(FORWARD);
        this.shooterRamp.setDirection(FORWARD);
        this.shooterStopper.setDirection(FORWARD);

    }
    //========== RPM Control functions ===================================

    private void setShooterRPM(double targetRPM) {

        if (targetRPM == 0.0) {
            // Stop commanding velocity and allow flywheel to coast
            shooterRight.setPower(0.0);
            shooterLeft.setPower(0.0);

        } else {
            // Convert RPM to encoder ticks per second
            double targetTPS =
                    targetRPM * Constant.SHOOTER_TICKS_PER_REV / 60.0;

            shooterRight.setVelocity(targetTPS);
            shooterLeft.setVelocity(targetTPS);
        }
    }

    public double getShooterRightRPM() {
        return shooterRight.getVelocity() * 60.0 / Constant.SHOOTER_TICKS_PER_REV;
    }

    public double getShooterLeftRPM() {
        return shooterLeft.getVelocity() * 60.0 / Constant.SHOOTER_TICKS_PER_REV;
    }
    //// canLaunch Overloading
    //public boolean canLaunch(double launchSpeed) {
    //    return this.canLaunch(launchSpeed,0.0);
    //}
    //public boolean canLaunch(double launchSpeed, double FudgeFactor) {
    //    return this.shooterRight.getCorrectedVelocity() >= (launchSpeed * Constant.SHOOTER_MAX) + FudgeFactor;
    //}
    public boolean canLaunch(double targetRPM) {

        double rightRPM = getShooterRightRPM();
        double leftRPM = getShooterLeftRPM();

        double minimumRPM = targetRPM
                - Constant.SHOOTER_READY_TOLERANCE_RPM;

        return targetRPM > 0
                && rightRPM >= minimumRPM
                && leftRPM >= minimumRPM;
    }


    //private double calculateShooterRampPosition() {
    public double getShooterRampTargetPosition() {
        // Average measured RPM from both shooter motors
        double actualRPM = (getShooterRightRPM()
                + getShooterLeftRPM()) / 2.0;

        double targetRPM = Constant.SHOOTER_RPM_SHOOTING;

        // Calculate RPM error (ignore overspeed)
        double rpmError = Math.max(0.0, targetRPM - actualRPM);

        // Proportional compensation
        double rampPosition = Constant.SHOOTER_RAMP_NOMINAL
                - rpmError * Constant.SHOOTER_RAMP_RPM_KP;

        // Limit servo travel
        return Math.max(Constant.SHOOTER_RAMP_MIN,
                Math.min(Constant.SHOOTER_RAMP_MAX, rampPosition));
    } // Calculate shooter ramp position

} // Mechanisms Opmode