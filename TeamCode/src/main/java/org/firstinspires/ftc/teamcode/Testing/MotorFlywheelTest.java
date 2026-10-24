package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp
public class MotorFlywheelTest extends OpMode {
    private DcMotor flywheel;
    double flywheelspeed;
    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotor.class, "fly_wheel");
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }


    @Override
    public void loop() {
        flywheel.setPower(flywheelspeed);
        if (gamepad1.a){
            flywheelspeed = flywheelspeed + 0.05;
        } else if (gamepad1.b) {
            flywheelspeed = flywheelspeed + 0.01;
        } else if (gamepad1.x){
            flywheelspeed = 0;
        }
        telemetry.addData("fly wheel speed", flywheelspeed);
    }
}

