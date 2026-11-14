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
        //========== Update Time =========================================
        mechanisms.setNow(getRuntime());
        //========== Read Inputs =========================================
        driverControls.updateController(driver);
        operatorControls.updateController(operator);
        //double batteryVoltage = hardwareMap.voltageSensor.iterator().next().getVoltage();
        addTelemetryData(); // last part of read
        //========== Process Controls ====================================
        operatorControls();
        driverControls();
        //========== Execute Outputs =====================================
        mechanisms.loop();
        driveTrain.loop();
        driveTrain.drive(driverControls.stickLeftY, driverControls.stickLeftX);
        //========== Publish Telemetry ===================================
        joinedTelemetry.update();
    }//loop


    private void operatorControls() {

        if(this.operatorControls.buttonX){}//intake forward
        if(this.operatorControls.buttonCircle){}//shooter forward
        if(this.operatorControls.bummerRight){}//feeder forward

        if(this.operatorControls.dpadDown){}//intake reverse
        if(this.operatorControls.dpadLeft){}//shooter reverse
        if(this.operatorControls.bummerLeft){}//feeder reverse

        if (this.operatorControls.buttonCircle) {
            this.mechanisms.setMode(Mechanisms2027.Mode.PICKUP);

        } else if (this.operatorControls.buttonX) {
            this.mechanisms.setMode(Mechanisms2027.Mode.SHOOTING);

        } else if (this.operatorControls.buttonTriangle) {
            this.mechanisms.setMode(Mechanisms2027.Mode.SHOOT_TEST);

        } else {
            this.mechanisms.setMode(Mechanisms2027.Mode.STOP);
        }

    }//Operator Controls End Bracket

    private void driverControls(){
    driveTrain.turn(driverControls.stickRightX);
    }

    private void addTelemetryData() {

        this.telemetry.addData("Shooter Mode", this.mechanisms.getMode());

        this.telemetry.addData("Shooter Target RPM", "%.0f",
                Constant.SHOOTER_RPM_SHOOTING);

        this.telemetry.addData("Shooter Right RPM", "%.0f",
                this.mechanisms.getShooterRightRPM());

        this.telemetry.addData("Shooter Left RPM", "%.0f",
                this.mechanisms.getShooterLeftRPM());

        this.telemetry.addData("Shooter Ready",
                this.mechanisms.canLaunch(Constant.SHOOTER_RPM_SHOOTING));
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
        this.telemetry.addData("Prox Sensor Distance", "%.2f mm",
                this.mechanisms.getProxDistance());
        this.telemetry.addData("Color Sensor Distance", "%.2f mm",
                this.mechanisms.getColorDistance());
        this.telemetry.addData("Ball Detected",
                this.mechanisms.detectBall());
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
