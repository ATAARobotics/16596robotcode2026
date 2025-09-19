package org.firstinspires.ftc.teamcode.Testing;

import com.arcrobotics.ftclib.command.CommandBase;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.RunCommand;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Subsystem.DriveTrainSubsystem;

// This test program is going to turn the robot
@TeleOp(name="CommandTurnTest")
public class CommandTurnTest extends CommandOpMode {
    private DriveTrainSubsystem driveTrain;

    @Override
    public void initialize(){
        driveTrain = new DriveTrainSubsystem(hardwareMap);
        driveTrain.setDefaultCommand(new RunCommand(
                () -> driveTrain.setDrivePower(0.2,0.2,0.2, Constants.EAST),
                driveTrain
        ));
    }
}
