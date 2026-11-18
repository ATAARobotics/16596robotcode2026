package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Subsystem.DriveTrain2027;
import org.firstinspires.ftc.teamcode.Utils.Constant;
import org.firstinspires.ftc.teamcode.Utils.Controller;

@Config
@TeleOp(name = "GetStats2027", group = "b_Config")
public class GetStats2027 extends OpMode {

    //========== Configuration ========================================
    // Limit drive speed during testing. Set to 1.0 for full speed.
    public static double DRIVE_SPEED_LIMIT = 0.35;
    private static final long TELEMETRY_INTERVAL_NS = 100_000_000L;

    //========== Hardware and Controls ================================
    private DriveTrain2027 driveTrain;
    private GamepadEx gamepad;
    private Controller controls;
    private JoinedTelemetry joinedTelemetry;
    private long lastTelemetryNs = 0;
    private boolean previousResetButton = false;

    @Override
    public void init() {
        gamepad = new GamepadEx(gamepad1);
        controls = new Controller();
        controls.setToleranceLeftX(Constant.TOLERANCE_JOYSTICK);
        controls.setToleranceLeftY(Constant.TOLERANCE_JOYSTICK);
        controls.setToleranceRightX(Constant.TOLERANCE_JOYSTICK);
        controls.setToleranceRightY(Constant.TOLERANCE_JOYSTICK);

        driveTrain = new DriveTrain2027(hardwareMap);
        driveTrain.init();
        driveTrain.setDrivemoderobotcentric();
        driveTrain.resetOdometry();

        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        joinedTelemetry = new JoinedTelemetry(
                telemetry,
                panelsTelemetry.getTelemetry().getWrapper(),
                dashboard.getTelemetry());

        telemetry.addData("Status", "Initialized - robot centric");
        telemetry.addData("Controls", "Left: drive; Right X: turn");
        telemetry.addData("Reset", "Gamepad A: reset position + IMU");
        telemetry.update();
    }

    @Override
    public void loop() {
        //========== Read Controls =====================================
        controls.updateController(gamepad);

        // Edge detection prevents resetting continuously while held.
        boolean resetPressed = controls.buttonX;
        if (resetPressed && !previousResetButton) {
            driveTrain.resetOdometry();
        }
        previousResetButton = resetPressed;

        //========== Command Drivetrain ================================
        double speedLimit = Math.max(0.0, Math.min(1.0, DRIVE_SPEED_LIMIT));
        driveTrain.drive(
                controls.stickLeftY * speedLimit,
                controls.stickLeftX * speedLimit);
        driveTrain.turn(controls.stickRightX * speedLimit);

        // Updates Pinpoint once and writes the drive motor commands.
        driveTrain.loop();

        //========== Display Coordinates ===============================
        long nowNs = System.nanoTime();
        if (nowNs - lastTelemetryNs >= TELEMETRY_INTERVAL_NS) {
            lastTelemetryNs = nowNs;

            // Coordinates as used by DriveTrain2027 (negated X/Y).
            telemetry.addData("Robot X (mm)", "%.1f",
                    driveTrain.getXPosition());
            telemetry.addData("Robot Y (mm)", "%.1f",
                    driveTrain.getYPosition());
            telemetry.addData("Robot Heading (deg)", "%.1f",
                    driveTrain.heading);

            // Raw Pinpoint data for diagnostics and comparison.
            Pose2D pose = driveTrain.odometer.getPosition();
            telemetry.addData("Pinpoint X (mm)", "%.1f",
                    pose.getX(DistanceUnit.MM));
            telemetry.addData("Pinpoint Y (mm)", "%.1f",
                    pose.getY(DistanceUnit.MM));
            telemetry.addData("Pinpoint Heading (deg)", "%.1f",
                    pose.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Raw X Encoder",
                    driveTrain.odometer.getEncoderX());
            telemetry.addData("Raw Y Encoder",
                    driveTrain.odometer.getEncoderY());

            telemetry.addData("Drive Limit", "%.0f%%", speedLimit * 100);
            telemetry.addData("Odometry Update (ms)", "%.2f",
                    driveTrain.odometryTimeMs);
            telemetry.addData("Motor Write (ms)", "%.2f",
                    driveTrain.motorWriteTimeMs);
            telemetry.addData("Reset", "Press A to zero pose + IMU");
            joinedTelemetry.update();
        }
    }

    @Override
    public void stop() {
        driveTrain.stop();
    }
}
