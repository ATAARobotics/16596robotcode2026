package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.JoinedTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Mechanisms.C2;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic2;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

@Configurable
@TeleOp(name = "RobotDrive2",group = "_0A")
public class
robot_drive2 extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private DriveTrainBasic2 driveTrain;
    public GamepadEx driver = null;
    public GamepadEx operator = null;
    private JoinedTelemetry joinedTelemetry;
    public double speed = 0.0;
    public LightIndicatorSubsystem indicator; //port 0 control hub
    public double currentTime = 0.0;
    public boolean operatorDriving = false;
    public String spMode = "FlyWheel Reverse";
    
    @Override
    public void init() {
        driveTrain = new DriveTrainBasic2(hardwareMap);
        driveTrain.init();  // commented out ,done in Auto
        driveTrain.odometer.resetPosAndIMU(); // comment out with Auto
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Update Telemetry
        FtcDashboard dashboard = FtcDashboard.getInstance();
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        // Initializing indicator
        indicator = new LightIndicatorSubsystem(hardwareMap);
        // Join them together
        joinedTelemetry = new JoinedTelemetry(telemetry,panelsTelemetry.getTelemetry().getWrapper(),dashboard.getTelemetry());
        joinedTelemetry.update();
        driveTrain.setDrivemoderobotcentric();
    }

    @Override
    public void start() {
        //driveTrain.start();//reset encoders
        driver = new GamepadEx(gamepad1); // This controls the movement of the robot
        operator = new GamepadEx(gamepad2); // This controls the movement of items on the robot
        runtime.reset();
    }

    @Override
    public void loop() {
        currentTime = getRuntime();
        driveTrain.setNow(currentTime);
        driver.readButtons();
        operator.readButtons();
        driveTrain.loop();

        // ===== Driver Controls =====
        if (driveTrain.manual_FC_turning) {
            driveTrain.headingCorrection = driver.getRightX();
        }
        // - Allow Driver to toggle between robot/fieldcentric controls
        if(driver.wasJustPressed((GamepadKeys.Button.Y))) {
            if (driveTrain.drivemodefieldcentric) {
                driveTrain.setDrivemoderobotcentric();
            } else {
                driveTrain.setDrivemodefieldcentric();
                driveTrain.manual_FC_turning = true;
            }
        }
        // ENABLE OPERATOR DRIVING CONTROLS
        if (operator.wasJustReleased(GamepadKeys.Button.BACK)){
            operatorDriving = !operatorDriving;
        }
        // Driver/Operator driving Controls
        if (operatorDriving) {
            double softLY = operator.getLeftY() * Math.abs(operator.getLeftY()) * C2.OPER_SPEED_RATIO;
            double softLX = operator.getLeftX() * Math.abs(operator.getLeftX()) * C2.OPER_SPEED_RATIO;
            driveTrain.drive(softLY, -softLX);
            double softRX = operator.getRightX() * Math.abs(operator.getRightX()) * C2.SPEED_RATIO;
            driveTrain.turn(softRX);
        } else {
            driveTrain.drive(driver.getLeftY() * C2.SPEED_RATIO, -driver.getLeftX() * C2.SPEED_RATIO);
            driveTrain.turn(driver.getRightX());
        }
        // =========  Operator controls ===========
        // Set shooting mode
        if (operator.wasJustPressed(GamepadKeys.Button.B)) {
            driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterPICKUP;
        }
        if (operator.wasJustPressed(GamepadKeys.Button.A)) {
            driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterSHOOTINGnear;
        }
        if (operator.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterSHOOTINGfar;
        }
        if (operator.wasJustPressed(GamepadKeys.Button.X)) {
            driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterCORRECTING;
        }
        if (operator.wasJustPressed(GamepadKeys.Button.Y)) {
            driveTrain.CurrentShooterMode = DriveTrainBasic2.ShooterMode.shooterOFF;
        }
        // - SP CONFIG FOR TESTING
        if (operator.isDown(GamepadKeys.Button.DPAD_RIGHT)){
            spMode = "Intake FWD";
            if (operator.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                C2.INTAKE_SPD_PICKUP += 0.1;
            } else if (operator.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                C2.INTAKE_SPD_PICKUP -= 0.1;
            }
        } else if (operator.isDown((GamepadKeys.Button.DPAD_UP))){
            spMode = "Feeder REV";
            if (operator.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                C2.FEEDER_SPD_CORRECTING += 0.1;
            } else if (operator.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                C2.FEEDER_SPD_CORRECTING -= 0.1;
            }
        } else {
            spMode = "FlyWheel REV";
            if (operator.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                C2.FLYWHEEL_SPD_REVERSE += 0.1;
            } else if (operator.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                C2.FLYWHEEL_SPD_REVERSE -= 0.1;
            }
        }

        speed = driveTrain.Flywheel.getCorrectedVelocity();
        updateIndicator();
        driveTrain.ShooterControlLoop();

      //===============driving presets======================

        //small  red shooting position
        if(driver.isDown(GamepadKeys.Button.RIGHT_BUMPER)) {
            driveTrain.setFacing(C2.REDSMALL_SHOOT);
        }

        //small blue shooting position
//      dont have a constant angle yet
        if(driver.isDown(GamepadKeys.Button.LEFT_BUMPER)){
            driveTrain.setFacing(C2.BLUESMALL_SHOOT);
        }

//        //big red shooting position
        if(driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER)>= C2.TRIGGER_TOLERANCE)  {
            driveTrain.setFacing(C2.REDBIG_SHOOT);
        }
       //big blue shooting position
        if(driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER)>= C2.TRIGGER_TOLERANCE) {
            driveTrain.setFacing(C2.BLUEBIG_SHOOT);
        }


       //Set small shooting area angel
//        if(driver.isDown(GamepadKeys.Button.DPAD_DOWN)){
//            set.headingpos
//        }
//        else {
//            shootingspeed = Constants2.FLYWHEEL_NEAR;
//        }
//        if(operator.isDown(GamepadKeys.Button.A)) {
//            driveTrain.flywheel.set(shootingspeed);
//        }
//        else  {
//            driveTrain.flywheel.set(0);
//        }


//Select N, S, E, W
//        if (driver.getRightX() <= -Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.WEST); // west
//        } else if (driver.getRightX() >= Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.EAST); // east
//        } else if (driver.getRightY() >= Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.SOUTH); // south
//        } else if (driver.getRightY() <= -Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.NORTH); // north
//        }
//
//// Select Diagonal Directions (NE,SE,SW,NW)
//        if (driver.getRightX() > Constants2.JOYSTICK_TOLERANCE & driver.getRightY() < -Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.NORTH_EAST); // north east
//        } else if (driver.getRightX() < -Constants2.JOYSTICK_TOLERANCE & driver.getRightY() < -Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.NORTH_WEST); // north west
//        } else if (driver.getRightX() < -Constants2.JOYSTICK_TOLERANCE & driver.getRightY() > Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.SOUTH_WEST); // south west
//        } else if (driver.getRightX() > Constants2.JOYSTICK_TOLERANCE & driver.getRightY() > Constants2.JOYSTICK_TOLERANCE) {
//            driveTrain.setDirection(Constants2.SOUTH_EAST); // south east
//        }



        // Send data to telemetry
        joinedTelemetry.addData("Shooter Speed", speed); //telemetry shooter speed
        joinedTelemetry.addData("Xcor",driveTrain.getXPosition());
        joinedTelemetry.addData("Ycor",driveTrain.getYPosition());
        joinedTelemetry.addData("turnspeed", driveTrain.turnSpeed);
        joinedTelemetry.addData("heading", driveTrain.heading);
        joinedTelemetry.addData("-MOTOR-", "-POWER-");
        joinedTelemetry.addData("Setpoint Mode", spMode);
        joinedTelemetry.addData("Intake Forward", C2.INTAKE_SPD_PICKUP);
        joinedTelemetry.addData("Feeder Reverse", C2.FEEDER_SPD_CORRECTING);
        joinedTelemetry.addData("FlyWheel Reverse", C2.FLYWHEEL_SPD_REVERSE);
        joinedTelemetry.update();
    }

    private void updateIndicator() {
        // INDICATOR CASES
        switch (driveTrain.CurrentShooterMode) {
            case shooterCORRECTING:
                indicator.setColor(C2.RGB_Light.VIOLET);
                break;
            case shooterPICKUP:
                indicator.setColor(C2.RGB_Light.YELLOW);
                break;
            case shooterSHOOTINGnear:
            case shooterSHOOTINGfar:
                if (driveTrain.canLaunch(C2.FLYWHEEL_SPD_FAR)) {
                    indicator.setColor(C2.RGB_Light.GREEN);
                } else {
                    indicator.setColor(C2.RGB_Light.RED);
                }
                break;
            case shooterOFF:
            default:
                indicator.setColor(C2.RGB_Light.WHITE);
        }
    }
}














