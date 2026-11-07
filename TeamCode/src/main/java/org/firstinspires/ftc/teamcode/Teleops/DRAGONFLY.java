package org.firstinspires.ftc.teamcode.Teleops;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.Old.DriveTrainBasic2;
import org.firstinspires.ftc.teamcode.Utils.Constant;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants2027;
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
        // Update Section

        // Body Section
        controller.updateController(operator);
        OperatorControls();
        // End Section
        mechanisms.loop();
        //driveTrain.loop();
        joinedTelemetry.update();

    }//loop

    private void OperatorControls() {
        // could also be in loop, but idk
        if (gamepad2.b) {
            Mechanisms2027.ShooterMode = Mechanisms2027.Mode.MODE_1;
            telemetry.addData("b","is pressed");
        } else {
            Mechanisms2027.ShooterMode = Mechanisms2027.Mode.MODE_3;
            telemetry.addData("b","is not pressed");

        }

        }
        // Set shooting mode

        }

// DRAGONFLY
