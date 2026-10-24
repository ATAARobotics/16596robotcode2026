package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous
public class LimelightTestingSA extends OpMode {
    private Limelight3A limelight3A;
    double NewTa;

    @Override
    public void init() {
        limelight3A = hardwareMap.get(Limelight3A.class, "lime_light" );
        limelight3A.pipelineSwitch(1);//Blue

    }
    public void start(){
        limelight3A.start();
    }
    @Override
    public void loop() {
        LLResult llresult = limelight3A.getLatestResult();
        if(llresult != null && llresult.isValid()); {
            telemetry.addData("target x offset", llresult.getTx());
            telemetry.addData("target y offset", llresult.getTy());
            telemetry.addData("target area offset", llresult.getTa());

            NewTa = llresult.getTa() + llresult.getTy();
            telemetry.addData("New ta", NewTa);
        }


    }
}