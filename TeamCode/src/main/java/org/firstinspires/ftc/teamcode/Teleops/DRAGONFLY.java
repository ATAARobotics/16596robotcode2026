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


@Configurable
@Config
@TeleOp(name = "DRAGONFLY")
public class DRAGONFLY extends OpMode {
  //  public DriveTrain2027 driveTrain;
    public Mechanisms2027 mechanisms;
    public Controller controller;
    private JoinedTelemetry joinedTelemetry;
    public GamepadEx driver = null;
    public GamepadEx operator = null;


    public void init(){
        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad1);

        // driveTrain = new DriveTrain2027(hardwareMap);
       // driveTrain.init();
        controller = new Controller();
        mechanisms = new Mechanisms2027(hardwareMap);
        mechanisms.init();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        joinedTelemetry = new JoinedTelemetry(telemetry, panelsTelemetry.getTelemetry().getWrapper(), dashboard.getTelemetry());
        joinedTelemetry.update();

    }// init

    public void start(){

    }//start

    public void loop(){
        // Read Gamepad inputs
        controller.updateController(operator);
        controller.updateController(driver);

        // Process Controls
        OperatorControls();

        // Execute Outputs
        mechanisms.loop();
        //driveTrain.loop();

        //Publish Telemetry
        AddTelemetryData();
        joinedTelemetry.update();

    }//loop

    private void OperatorControls() {
        // Configure Operator Controller Buttons
        if (gamepad2.b) {
            Mechanisms2027.ShooterMode = Mechanisms2027.Mode.MODE_1;

        } else {
            Mechanisms2027.ShooterMode = Mechanisms2027.Mode.MODE_3;


        }

    }//Operator Controls End Bracket

    public void AddTelemetryData() {
        telemetry.addData("Mode:", Mechanisms2027.ShooterMode);

    }// Print Telemetry


}// OpMode End Bracket


// DRAGONFLY
