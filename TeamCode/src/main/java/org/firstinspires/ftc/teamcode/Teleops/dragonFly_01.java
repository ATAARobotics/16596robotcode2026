package org.firstinspires.ftc.teamcode.Teleops;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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

    public void init(){
        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);
        // Configure Controllers
        driverControls = new Controller();
        operatorControls = new Controller();
        controllerJoystickTolerance(driverControls, Constant.TOLERANCE_JOYSTICK);
        controllerJoystickTolerance(operatorControls, Constant.TOLERANCE_JOYSTICK);

        // driveTrain = new DriveTrain2027(hardwareMap);
        // driveTrain.init();

        mechanisms = new Mechanisms2027(hardwareMap);
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

        //========== Process Controls ====================================
        operatorControls();

        //========== Execute Outputs =====================================
        mechanisms.loop();

        //========== Publish Telemetry ===================================
        addTelemetryData();
        joinedTelemetry.update();
    }//loop


    private void operatorControls() {

        if (operatorControls.buttonCircle) {
            mechanisms.setMode(Mechanisms2027.Mode.PICKUP);

        } else if (operatorControls.buttonX) {
            mechanisms.setMode(Mechanisms2027.Mode.SHOOTING);

        } else if (operatorControls.buttonTriangle) {
            mechanisms.setMode(Mechanisms2027.Mode.SHOOT_TEST);

        } else {
            mechanisms.setMode(Mechanisms2027.Mode.STOP);
        }

    }//Operator Controls End Bracket

    private void addTelemetryData() {

        telemetry.addData("Shooter Mode", mechanisms.getMode());

        telemetry.addData("Shooter Target RPM", "%.0f",
                Constant.SHOOTER_RPM_SHOOTING);

        telemetry.addData("Shooter Right RPM", "%.0f",
                mechanisms.getShooterRightRPM());

        telemetry.addData("Shooter Left RPM", "%.0f",
                mechanisms.getShooterLeftRPM());

        telemetry.addData("Shooter Ready",
                mechanisms.canLaunch(Constant.SHOOTER_RPM_SHOOTING));
        telemetry.addData("Shooter Right Amps", "%.2f",
                mechanisms.getShooterRightCurrent());

        telemetry.addData("Shooter Left Amps", "%.2f",
                mechanisms.getShooterLeftCurrent());

        telemetry.addData("Battery Voltage", "%.2f",
                hardwareMap.voltageSensor.iterator().next().getVoltage());
        telemetry.addData("Shooter Ready", mechanisms.isShooterReady());

        if (mechanisms.getShooterSpinUpTime() >= 0) {
            telemetry.addData("Shooter Spin-Up Time", "%.2f sec",
                    mechanisms.getShooterSpinUpTime());
        } else {
            telemetry.addData("Shooter Spin-Up Time", "Waiting...");
        }
        telemetry.addData("Prox Sensor Distance", "%.2f mm",
                mechanisms.getProxDistance());
        telemetry.addData("Color Sensor Distance", "%.2f mm",
                mechanisms.getColorDistance());
        telemetry.addData("Ball Detected", mechanisms.detectBall());
        telemetry.addData("Shooter Ramp Target", "%.3f",
                mechanisms.getShooterRampTargetPosition());
    }// Print Telemetry


    private void controllerJoystickTolerance(Controller controller, float toleranceValue){
        controller.setToleranceLeftX(toleranceValue);
        controller.setToleranceLeftY(toleranceValue);
        controller.setToleranceRightX(toleranceValue);
        controller.setToleranceRightY(toleranceValue);

    }

}// DRAGONFLY OpMode
