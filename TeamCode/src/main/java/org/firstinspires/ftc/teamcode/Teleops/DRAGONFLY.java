package org.firstinspires.ftc.teamcode.Teleops;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Utils.Constant;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants2027;
import org.firstinspires.ftc.teamcode.Subsystem.DriveTrain2027;
import org.firstinspires.ftc.teamcode.Subsystem.Mechanisms2027;
import org.firstinspires.ftc.teamcode.Utils.Controller;


@Configurable
@Config
@TeleOp(name = "DRAGONFLY")
public class DRAGONFLY extends OpMode {

    public DriveTrain2027 driveTrain;
    public Mechanisms2027 mechanisms;
    private JoinedTelemetry joinedTelemetry;


    public void init(){
        driveTrain = new DriveTrain2027(hardwareMap);
        driveTrain.init();

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

        // End Section
        mechanisms.loop();
        driveTrain.loop();
        joinedTelemetry.update();
    }//loop

}// DRAGONFLY
