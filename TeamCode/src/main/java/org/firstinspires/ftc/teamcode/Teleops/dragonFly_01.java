package org.firstinspires.ftc.teamcode.Teleops;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Subsystem.DriveTrain2027;
import org.firstinspires.ftc.teamcode.Subsystem.Mechanisms2027;
import org.firstinspires.ftc.teamcode.Utils.Controller;
import org.firstinspires.ftc.teamcode.Utils.Constant;

@Configurable
@Config
@TeleOp(name = "dragonFly_01",group="a_comp")
public class dragonFly_01 extends OpMode {
  //  public DriveTrain2027 driveTrain;
    public Mechanisms2027 mechanisms;
    public Controller driverControls;
    public Controller operatorControls;
    private JoinedTelemetry joinedTelemetry;
    public GamepadEx driver;
    public GamepadEx operator;
    private DriveTrain2027 driveTrain;
    private long lastTelemetryNs = 0;
    private long previousLoopStartNs = 0;
    private double maxLoopPeriodMs = 0;
    private static final long TELEMETRY_INTERVAL_NS = 100_000_000L;
    public HardwareMap hwMap;
    public void init(){

        hwMap = hardwareMap;
        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);
        // Configure Controllers
        driverControls = new Controller();
        operatorControls = new Controller();
        controllerJoystickTolerance(driverControls, Constant.TOLERANCE_JOYSTICK);
        controllerJoystickTolerance(operatorControls, Constant.TOLERANCE_JOYSTICK);

        driveTrain = new DriveTrain2027(hwMap);
        driveTrain.init();
        driveTrain.setDrivemoderobotcentric();

        mechanisms = new Mechanisms2027(hwMap);
        mechanisms.init();
        mechanisms.setMode(Mechanisms2027.Mode.STOP);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        joinedTelemetry = new JoinedTelemetry(telemetry, panelsTelemetry.getTelemetry().getWrapper(), dashboard.getTelemetry());
        joinedTelemetry.update();

    }// init

    public void start(){
        mechanisms.start();
    }//start

    public void loop(){
        // temp scan time checking
        long currentLoopStartNs = System.nanoTime();

        if (previousLoopStartNs != 0) {
            double loopPeriodMs =
                    (currentLoopStartNs - previousLoopStartNs) / 1e6;

            maxLoopPeriodMs =
                    Math.max(maxLoopPeriodMs, loopPeriodMs);
        }

        previousLoopStartNs = currentLoopStartNs;
        // end temp scan time checking
        //========== Update Time =========================================
        long loopStart = System.nanoTime();
        mechanisms.setNow(getRuntime());
        //========== Read Inputs =========================================
        driverControls.updateController(driver);
        operatorControls.updateController(operator);
        long inputsDone = System.nanoTime();
        //========== Process Controls ====================================
        operatorControls();
        driverControls();
        driveTrain.drive(driverControls.stickLeftY, driverControls.stickLeftX);
        //========== Execute Outputs =====================================
        driveTrain.loop();
        long driveDone = System.nanoTime();
        mechanisms.loop();
        long mechanismsDone = System.nanoTime();
        //========== Publish Telemetry ===================================
        long nowNs = System.nanoTime();
        if (nowNs - lastTelemetryNs >= TELEMETRY_INTERVAL_NS) {
            lastTelemetryNs = nowNs;
            telemetry.addData("Inputs ms", "%.2f",
                    (inputsDone - loopStart) / 1e6);

            telemetry.addData("Drive ms", "%.2f",
                    (driveDone - inputsDone) / 1e6);

            telemetry.addData("Mechanisms ms", "%.2f",
                    (mechanismsDone - driveDone) / 1e6);

            telemetry.addData("Control loop ms", "%.2f",
                    (mechanismsDone - loopStart) / 1e6);

            addTelemetryData();
        joinedTelemetry.update();
        }
    }//loop


    private void operatorControls() {

        if(this.operatorControls.buttonX){}//intake forward
        if(this.operatorControls.buttonCircle){}//shooter forward
      //  if(this.operatorControls.bummerRight){}//feeder forward

        if(this.operatorControls.dpadDown){}//intake reverse
        if(this.operatorControls.dpadLeft){}//shooter reverse
        if(this.operatorControls.bummerLeft){}//feeder reverse
        mechanisms.setManualLiftUp(operatorControls.bummerRight);

        if (this.operatorControls.buttonX) {
            this.mechanisms.setMode(Mechanisms2027.Mode.PICKUP);

        } else if (this.operatorControls.buttonCircle) {
            this.mechanisms.setMode(Mechanisms2027.Mode.SHOOTING);

        } else if (this.operatorControls.buttonTriangle) {
            this.mechanisms.setMode(Mechanisms2027.Mode.SHOOT_TEST);

        } else if (this.operatorControls.dpadDown){
            this.mechanisms.setMode(Mechanisms2027.Mode.REVERSE);

        } else{
            this.mechanisms.setMode(Mechanisms2027.Mode.STOP);
        }

    }//Operator Controls End Bracket

    private void driverControls(){
    driveTrain.turn(driverControls.stickRightX);
    }

    private void addTelemetryData() {
        //========== Color and Proximity Telemetry =========================
        if (Mechanisms2027.color_prox_enable) {

            telemetry.addData("Prox Sensor Distance", "%.2f mm",
                    mechanisms.getProxDistance());

            telemetry.addData("Color Sensor Distance", "%.2f mm",
                    mechanisms.getColorDistance());

            telemetry.addData("Ball Detected",
                    mechanisms.detectBall());

            telemetry.addData("Color Read ms", "%.2f",
                    mechanisms.colorReadTimeMs);

            telemetry.addData("Color Max Read ms", "%.2f",
                    mechanisms.maxColorReadTimeMs);
        }
        // PINPOINT TESTING
        telemetry.addData("Pinpoint X (mm)",
                driveTrain.odometer.getPosition().getX(DistanceUnit.MM));
        telemetry.addData("Pinpoint Y (mm)",
                driveTrain.odometer.getPosition().getY(DistanceUnit.MM));
        telemetry.addData("Pinpoint Heading (deg)",
                driveTrain.heading);
        telemetry.addData("Raw X Encoder",
                driveTrain.odometer.getEncoderX());
        telemetry.addData("Raw Y Encoder",
                driveTrain.odometer.getEncoderY());
        // END PINPOINT TESTING
        telemetry.addData("Motor Write ms", "%.2f",
                driveTrain.motorWriteTimeMs);
        telemetry.addData("Max Loop Period ms", "%.2f",
                maxLoopPeriodMs);

        this.telemetry.addData("Shooter Mode", this.mechanisms.getMode());

        this.telemetry.addData("Shooter Target RPM", "%.0f",
                mechanisms.getShooterTargetRPM());

        this.telemetry.addData("Shooter Right RPM", "%.0f",
                this.mechanisms.getShooterRightRPM());

        this.telemetry.addData("Shooter Left RPM", "%.0f",
                this.mechanisms.getShooterLeftRPM());

        this.telemetry.addData("Shooter Right Amps", "%.2f",
                this.mechanisms.getShooterRightCurrent());

        this.telemetry.addData("Shooter Left Amps", "%.2f",
                this.mechanisms.getShooterLeftCurrent());

        this.telemetry.addData("Battery Voltage", "%.2f",
                hardwareMap.voltageSensor.iterator().next().getVoltage());
        this.telemetry.addData("Shooter Ready",
                this.mechanisms.isShooterReady());

        if (this.mechanisms.getShooterSpinUpTime() >= 0) {
            this.telemetry.addData("Shooter Spin-Up Time", "%.2f sec",
                    this.mechanisms.getShooterSpinUpTime());
        } else {
            this.telemetry.addData("Shooter Spin-Up Time", "Waiting...");
        }

        this.telemetry.addData("Shooter Ramp Target", "%.3f",
                this.mechanisms.getShooterRampTargetPosition());

    }// Print Telemetry


    private void controllerJoystickTolerance(Controller controller, float toleranceValue){
        controller.setToleranceLeftX(toleranceValue);
        controller.setToleranceLeftY(toleranceValue);
        controller.setToleranceRightX(toleranceValue);
        controller.setToleranceRightY(toleranceValue);

    }

}// DRAGONFLY OpMode
