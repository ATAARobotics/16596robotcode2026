package org.firstinspires.ftc.teamcode.Auto;
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Subsystem.DriveTrain2027;
import org.firstinspires.ftc.teamcode.Subsystem.Mechanisms2027;
import org.firstinspires.ftc.teamcode.Utils.Constant;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;

@Autonomous(name = "Simple Leave Auto")
public class SimpleLeave extends OpMode {

    private DriveTrain2027 driveTrain;
    public HardwareMap hwMap;
    public Mechanisms2027 mechanisms;
    private JoinedTelemetry joinedTelemetry;
    private Steps currentSteps = Steps.STOP;

    ElapsedTime elapsedTime = new ElapsedTime();

    @Override
    public void init() {
        hwMap = hardwareMap;
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
    }//init

    @Override
    public void start() {
        elapsedTime.reset();
    }//start

    @Override
    public void loop() {

        switch (currentSteps){
            case SHOOT:
                mechanisms.setMode(Mechanisms2027.Mode.SHOOTING);
                driveTrain.drive(0,0);
                break;

            case MOVE_OFF_WALL:
                mechanisms.setMode(Mechanisms2027.Mode.STOP);
                driveTrain.drive(Constant.AUTO_DRIVE_SPEED, 0.0);
                break;

            case PARK:
                mechanisms.setMode(Mechanisms2027.Mode.STOP);
                driveTrain.drive(0.0, 0.0);
                break;

            case STOP:
            default:
                mechanisms.setMode(Mechanisms2027.Mode.STOP);
                driveTrain.drive(0.0, 0.0);
                break;
        }//switch current steps


        if (elapsedTime.seconds() < 1.0) {
            // Drive forward
            currentSteps = Steps.STOP;
        } else if (elapsedTime.seconds() < 3.0){
            // Stop
            currentSteps = Steps.SHOOT;
        } else if (elapsedTime.seconds() < 4.0){
            currentSteps = Steps.MOVE_OFF_WALL;
        }else if (elapsedTime.seconds() < 5.0){
            currentSteps = Steps.PARK;
        }else{
            currentSteps = Steps.STOP;
        }
        driveTrain.loop();

        addTelemetryData();
        telemetry.update();
        joinedTelemetry.update();
    }//loop

    //========== Class Types ========================================
    public enum Steps {
        SHOOT,
        MOVE_OFF_WALL,
        PARK,
        STOP
    }

    private void addTelemetryData() {
        this.telemetry.addData("AUTO STEPS", currentSteps.toString());
        this.telemetry.addData("Shooter Mode", this.mechanisms.getMode());
    }
}//simple leave