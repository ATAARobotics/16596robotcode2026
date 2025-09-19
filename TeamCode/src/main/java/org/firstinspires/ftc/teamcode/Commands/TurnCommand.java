package org.firstinspires.ftc.teamcode.Commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.Subsystem.DriveTrainSubsystem;

public class TurnCommand extends CommandBase {
    private final DriveTrainSubsystem driveTrain;
    private final double power;
    private final double heading;

    public TurnCommand(DriveTrainSubsystem driveTrain, double power, double heading){
        this.driveTrain = driveTrain;
        this.power = power;
        this.heading = heading;

        addRequirements(driveTrain);
    }

    @Override
    public void initialize(){
        driveTrain.setDrivePower(power,power,power,heading);
    }

    @Override
    public void execute(){

    }

    @Override
    public boolean isFinished(){
        return true;
    }

    @Override
    public void end(boolean interrupted){
        driveTrain.stop();
    }
}
