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
    private boolean manualLiftUp = false;
    private boolean nectarLiftUp = false;
    private Servo shooterRamp;
    private Servo shooterStopper;
    //========== Sensors ============================================
    private RevColorSensorV3 shooterColorSensor;
    //private NormalizedColorSensor shooterColorSensor;
    private DistanceSensor shooterProximity;
    //========== Sensor Enable ========================================
    public static boolean color_prox_enable = false;
    //========== Color Sensor Cache ==================================
    private static final long COLOR_INTERVAL_NS = 20_000_000L;

    private long lastColorReadNs = 0;

    private double cachedColorDistance = 1000.0;
    private ballDetecting cachedBall = ballDetecting.NONE;

    public double colorReadTimeMs = 0.0;
    public double maxColorReadTimeMs = 0.0;
    //========== Time ===============================================
    private double now = 0.0;
    private double shooterModeStartTime = 0.0;
    private double shooterSpinUpTime = -1.0;
    private boolean shooterReachedSpeed = false;
    private boolean feederEnabled = false;


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
        //========== Update Sensors =====================================
        updateColorSensor();

        //========== Determine Lift Position =============================
        boolean automaticNectar =
                (shooterMode == Mode.PICKUP
                        || shooterMode == Mode.SHOOTING)
                        && detectBall() == ballDetecting.NECTAR;
        nectarLiftUp = manualLiftUp || automaticNectar;

        //========== Shooter Modes =====================================
        switch (shooterMode){
            //========== PICKUP/INTAKE ===================================
            case PICKUP:
                // Intake/pickup mode:
                setOperatorMotors(
                        Constant.SHOOTER_RPM_HOLDING, // Speed - Shooter Flywheels
                        Constant.INTAKE_SPEED_PICKUP,   // Speed - Intake Motor
                        Constant.FEEDER_SPEED_PICKUP,   // Speed - Feeder Motor
                        Constant.NECTAR_LIFT_DOWN,      // Position - Nectar Lift
                        Constant.SHOOTER_RAMP_UP,     // Position - Shooter Ramp
                        Constant.SHOOTER_STOPPER_CLOSED // Position - Shooter Stopper
                );

                break;
            //========== SHOOTING ============================================
            case SHOOTING:

                if (!ShooterModeActiveForMoreThan(Constant.CORRECTION_DELAY)) {

                    setOperatorMotors(
                            Constant.SHOOTER_RPM_CORRECTING,
                            Constant.INTAKE_SPEED_CORRECTING,
                            Constant.FEEDER_SPEED_CORRECTING,
                            Constant.NECTAR_LIFT_DOWN,
                            Constant.SHOOTER_RAMP_UP,
                            Constant.SHOOTER_STOPPER_CLOSED
                    );

                } else {

                    double targetRPM = getShooterTargetRPM();

                    //if (canLaunch(targetRPM)) {
                    if (updateFeederEnabled(targetRPM)) {
                        setOperatorMotors(
                                targetRPM,
                                Constant.INTAKE_SPEED_SHOOTING,
                                Constant.FEEDER_SPEED_SHOOTING,
                                Constant.NECTAR_LIFT_DOWN,
                                Constant.SHOOTER_RAMP_UP,
                                Constant.SHOOTER_STOPPER_OPEN
                        );

                    } else {

                        setOperatorMotors(
                                targetRPM,
                                Constant.INTAKE_SPEED_CORRECTING,
                                Constant.FEEDER_SPEED_CORRECTING,
                                Constant.NECTAR_LIFT_DOWN,
                                getShooterRampTargetPosition(),
                                Constant.SHOOTER_STOPPER_CLOSED
                        );
                    }
                }

                break;
            //========== SHOOTER TESTING ===================================
            case SHOOT_TEST: // Off
                setOperatorMotors(
                        Constant.SHOOTER_RPM_SHOOTING_POLLEN,    // Speed - Shooter Flywheels
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
                        Constant.SHOOTER_RAMP_UP,     // Position - Shooter Ramp
                        Constant.SHOOTER_STOPPER_CLOSED // Position - Shooter Stopper
                );
                break;
        } // Switch shooterMode

        //========== Shooter Spin-Up Timing ==============================
        if ((shooterMode == Mode.SHOOTING
                || shooterMode == Mode.SHOOT_TEST)
                && !shooterReachedSpeed) {

            if (canLaunch(Constant.SHOOTER_RPM_SHOOTING_POLLEN)) {
                shooterSpinUpTime = now - shooterModeStartTime;
                shooterReachedSpeed = true;
            }
        } // Shooter Spin-Up Timing

    } // Loop


    //========== Sensor Functions ===================================
    //public ballDetecting detectBall() {
    //    double colorDist = getColorDistance();
    //    boolean nectarDetected = detectNectar();
//
 //        if (colorDist > 75.0) {
//            return ballDetecting.NONE;
//        } else if (nectarDetected) {
//            return ballDetecting.NECTAR;
//        } else {
//            return ballDetecting.POLLEN;
//        }
//    }

    //========== Update Color Sensor ================================
    public void updateColorSensor() {
        if (!color_prox_enable) {
            cachedBall = ballDetecting.NONE;
            cachedColorDistance = 1000.0;
            return;
        }
        long nowNs = System.nanoTime();

        if (nowNs - lastColorReadNs < COLOR_INTERVAL_NS) {
            return;
        }

        lastColorReadNs = nowNs;
        long startNs = System.nanoTime();

        // Read distance once.
        cachedColorDistance =
                shooterColorSensor.getDistance(DistanceUnit.MM);

        // Only read RGB when a ball is within detection range.
        if (cachedColorDistance > 75.0) {
            cachedBall = ballDetecting.NONE;
        } else {
            NormalizedRGBA colors =
                    shooterColorSensor.getNormalizedColors();

            boolean nectarDetected =
                    ((colors.red > colors.green
                            && colors.red > colors.blue)
                            || (colors.blue > colors.green
                            && colors.blue > colors.red))
                            && (colors.red > Constant.COLOR_THRESHOLD
                            || colors.blue > Constant.COLOR_THRESHOLD
                            || colors.green > Constant.COLOR_THRESHOLD);

            cachedBall = nectarDetected
                    ? ballDetecting.NECTAR
                    : ballDetecting.POLLEN;
        }

        colorReadTimeMs =
                (System.nanoTime() - startNs) / 1e6;

        maxColorReadTimeMs =
                Math.max(maxColorReadTimeMs, colorReadTimeMs);
    }

    //========== Cached Sensor Results =============================
    public ballDetecting detectBall() {
        return cachedBall;
    }

    public boolean detectNectar() {
        if (!color_prox_enable) {
            return false;
        }
        return cachedBall == ballDetecting.NECTAR;
    }

    public double getColorDistance() {
        if (!color_prox_enable) {
            return 1000.0;
        }
        return cachedColorDistance;
    }

//    public boolean detectNectar() {
//        NormalizedRGBA colors = this.shooterColorSensor.getNormalizedColors();
//        return (((colors.red > colors.green
//                && colors.red > colors.blue)
//                || (colors.blue > colors.green
//                && colors.blue > colors.red))
//                && (colors.red > Constant.COLOR_THRESHOLD
//                || colors.blue > Constant.COLOR_THRESHOLD
//                || colors.green > Constant.COLOR_THRESHOLD));
//    }

    //========== Class Get Sections =================================
    public double getProxDistance() {
        if (!color_prox_enable) {
            return 1000.0;
        }
        return shooterProximity.getDistance(DistanceUnit.MM);
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
    public void setManualLiftUp(boolean enabled) {
        manualLiftUp = enabled;
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
        feederEnabled = false;
    }
    private boolean ShooterModeActiveForMoreThan(double seconds) {
        return (this.now - this.shooterModeStartTime) >= seconds;
    }
    //========== Shooter RPM Selection ===============================
    public double getShooterTargetRPM() {
        return nectarLiftUp
                ? Constant.SHOOTER_RPM_SHOOTING_NECTAR
                : Constant.SHOOTER_RPM_SHOOTING_POLLEN;
    }

    //========== Set Mechanism Outputs ===============================
    private void setOperatorMotors(
            double shooterRPM,
            double intakeSpeed,
            double feederSpeed,
            double shooterNectarLiftPosition,
            double shooterRampPosition,
            double shooterStopperPosition) {

        this.feeder.set(feederSpeed);
        this.intake.set(intakeSpeed);

        setShooterRPM(shooterRPM);

        this.shooterNectarLift.setPosition(
                nectarLiftUp
                        ? Constant.NECTAR_LIFT_UP
                        : shooterNectarLiftPosition
        );

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
        this.intake.setInverted(false);
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
    //========== Shooter Feed Control ================================
    private boolean updateFeederEnabled(double targetRPM) {

        if (!feederEnabled) {
            // Start only when shooter is at target RPM.
            feederEnabled = canLaunch(targetRPM);

        } else {
            // Continue feeding through normal RPM dips.
            double minimumRPM =
                    targetRPM - Constant.SHOOTER_FEED_STOP_DROP_RPM;

            if (getShooterRightRPM() < minimumRPM
                    || getShooterLeftRPM() < minimumRPM) {

                feederEnabled = false;
            }
        }

        return feederEnabled;
    }

    //private double calculateShooterRampPosition() {
    public double getShooterRampTargetPosition() {
        // Average measured RPM from both shooter motors
        double actualRPM = (getShooterRightRPM()
                + getShooterLeftRPM()) / 2.0;

        double targetRPM = getShooterTargetRPM();

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