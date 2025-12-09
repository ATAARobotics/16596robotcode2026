package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.arcrobotics.ftclib.hardware.motors.MotorGroup;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;
import org.firstinspires.ftc.teamcode.Utils.Logger;

import java.util.Locale;

@Configurable
@TeleOp(name = "SpinnerTest", group ="Testing")
public class SpinnerTest extends OpMode{

    public HardwareMap hwMap;
    // Shooter Motors
    public MotorEx shooter; //port 0-expansion hub
    public MotorEx shooter2; //port 2-expansion-hub
    public MotorGroup flywheel; // assigned shooter and shooter2
    // Define GamePad
    public GamepadEx driver = null;
    // Panel Stuff
    private JoinedTelemetry joinedTelemetry;
    // Speed counter
    private double speedCounter = 0.0;
    // Logging
    private Logger logger;

    @Override
    public void init(){
        // Create Logger
        logger = new Logger("spinnertest.csv",true);
        // Flywheel assignment
        shooter = new MotorEx(hwMap, "shooter");// see https://docs.ftclib.org/ftclib/features/hardware/motors -- may have to add gobilda type
        shooter2 = new MotorEx(hwMap, "shooter2");
        flywheel = new MotorGroup(shooter, shooter2);
        //set up for two shooter motors
        shooter.setRunMode(Motor.RunMode.VelocityControl);
        shooter.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        shooter2.setRunMode(Motor.RunMode.VelocityControl);
        shooter2.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT);
        shooter2.setInverted(true);
        // configuring flywheel motor group
        flywheel.setRunMode(Motor.RunMode.VelocityControl);// motor group of the 2 shooter motors
        flywheel.setVeloCoefficients(Constants.FLYWHEEL_KP,Constants.FLYWHEEL_KI, Constants.FLYWHEEL_KD);     //coefficients are. kp, ki, and kd

        // Setup Telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Update Telemetry
        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        // Join them together
        joinedTelemetry = new JoinedTelemetry(telemetry,panelsTelemetry.getTelemetry().getWrapper(),dashboard.getTelemetry());
        joinedTelemetry.update();
    }
    @Override
    public void loop(){
        // Update controller info
        driver.readButtons();
        // Process controller
        if (driver.wasJustPressed(GamepadKeys.Button.DPAD_UP)){
            speedCounter += 0.1;
        }
        if (driver.wasJustPressed(GamepadKeys.Button.DPAD_DOWN) && speedCounter > 0.0) {
            speedCounter -= 0.1;
        }
        flywheel.set(speedCounter);
        // Get logging information
        double shooterVelocity = shooter.getVelocity();
        double shooter2Velocity = shooter2.getVelocity();
        double flywheelVelocity = flywheel.getVelocity();
        logger.writeLog(String.format(Locale.ENGLISH,"%f,%f,%f,%f",speedCounter, shooterVelocity,shooter2Velocity,flywheelVelocity));
        // Send telemetry
        joinedTelemetry.addData("Speed Counter",speedCounter);
        joinedTelemetry.addData("Shooter 1", shooterVelocity);
        joinedTelemetry.addData("Shooter 2",shooter2Velocity);
        joinedTelemetry.addData("Flywheel",flywheelVelocity);
        joinedTelemetry.update();
    }
    @Override
    public void stop(){
        logger.stopLog();
    }
}
