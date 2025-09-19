package org.firstinspires.ftc.teamcode.Subsystem;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.drivebase.MecanumDrive;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.GoBildaPinpointDriver;

public class DriveTrainSubsystem extends SubsystemBase {
    // Driving Motors
    private final Motor leftFrontDrive;
    private final Motor rightFrontDrive;
    private final Motor leftBackDrive;
    private final Motor rightBackDrive;
    // Odometer
    private final GoBildaPinpointDriver odometer;
    // driveBase
    private final MecanumDrive driveBase;

    public DriveTrainSubsystem(HardwareMap hwMap){
        // Define and Initialize Motors (note: need to use reference to actual OpMode).
        leftFrontDrive = new Motor(hwMap, "left_front_drive"); // 0
        rightFrontDrive = new Motor(hwMap, "right_front_drive"); // 1
        leftBackDrive = new Motor(hwMap, "left_back_drive"); // 2
        rightBackDrive = new Motor(hwMap, "right_back_drive"); // 3

        // redundant as default is brake mode
        leftBackDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        leftFrontDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        // Inverting because of the extra gear and not direct drive
        rightFrontDrive.setInverted(true);
        rightBackDrive.setInverted(true);
        leftFrontDrive.setInverted(true);
        leftBackDrive.setInverted(true);

        // odometer initializing -- this should go into PracticeDriveTrain2025??
        odometer = hwMap.get(GoBildaPinpointDriver.class, "xy-cord");
        odometer.setOffsets(Constants.ODOMETER_X_OFFSET, Constants.ODOMETER_Y_OFFSET);
        odometer.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD);
        odometer.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        // driveBase
        driveBase = new MecanumDrive(leftFrontDrive, rightFrontDrive, leftBackDrive, rightBackDrive);
    }

    public void setDrivePower(double strafeSpeed, double forwardSpeed, double turnSpeed, double gyroAngle){
        driveBase.driveFieldCentric(strafeSpeed,forwardSpeed,turnSpeed,gyroAngle, false);
    }

    public void stop() {
        setDrivePower(0.0,0.0,0.0,0.0);
    }
}
