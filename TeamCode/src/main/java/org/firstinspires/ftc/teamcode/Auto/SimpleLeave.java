package org.firstinspires.ftc.teamcode.Auto;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Subsystem.DriveTrain2027;
import org.firstinspires.ftc.teamcode.Subsystem.Mechanisms2027;
import org.firstinspires.ftc.teamcode.Utils.Constant;

@Autonomous(name = "Simple Leave Auto")
public class SimpleLeave extends OpMode {

    private DriveTrain2027 driveTrain;
    public HardwareMap hwMap;
    public Mechanisms2027 mechanisms;

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
    }//init

    @Override
    public void start() {

        elapsedTime.reset();

    }//start

    @Override
    public void loop() {
        if (elapsedTime.seconds() < Constant.TIME_ONE_SECOND) {
            // Drive forward
            driveTrain.drive(Constant.AUTO_DRIVE_SPEED, 0.0);
        } else {
            // Stop
            driveTrain.drive(0.0, 0.0);
        }
        driveTrain.loop();
    }//loop

}//simple leave