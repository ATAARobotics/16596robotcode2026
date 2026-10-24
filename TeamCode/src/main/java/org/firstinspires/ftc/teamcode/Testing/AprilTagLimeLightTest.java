package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
@Autonomous
public class AprilTagLimeLightTest extends OpMode {
    private Limelight3A limelight;
    private RevBlinkinLedDriver prism;

    private IMU imu;
    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "lime_light");
        prism = hardwareMap.get(RevBlinkinLedDriver.class, "prism_i2c");

        limelight.pipelineSwitch(5);
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
    }

    @Override
    public void start() {
        limelight.start();
    }

    @Override
    public void loop() {
        YawPitchRollAngles oriantation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(oriantation.getYaw());
        LLResult llresult = limelight.getLatestResult();

        if (limelight != null && llresult.isValid()){
            Pose3D botpose = llresult.getBotpose_MT2();
            telemetry.addData("target x", llresult.getTx());
            telemetry.addData("target y", llresult.getTy());
            telemetry.addData("target area", llresult.getTa());
            telemetry.addData("Bot pose", botpose.getOrientation());
            telemetry.addData("Yaw", botpose.getOrientation().getYaw());

        }

        if (Math.abs(llresult.getTx()) < 2 && llresult.getTy() < 2) {
            prism.setPattern(RevBlinkinLedDriver.BlinkinPattern.fromNumber(40));//
        }
        else {
            prism.setPattern(RevBlinkinLedDriver.BlinkinPattern.fromNumber(6));//
        }
    }
}