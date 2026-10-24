package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp
public class PredicamentTesting extends OpMode {
    private DcMotor PollenShooter;
    double PollenShooterSpeed;

    private DcMotor NectarShooter;
    double NectarShooterSpeed;

    @Override
    public void init() {
        PollenShooter = hardwareMap.get(DcMotor.class, "Pollen Shooter");
        PollenShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        PollenShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PollenShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        NectarShooter = hardwareMap.get(DcMotor.class, "Nectar Shooter");
        NectarShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        NectarShooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        NectarShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    @Override
    public void loop() {
        PollenShooter.setPower(PollenShooterSpeed);
        if (gamepad1.a){
           PollenShooterSpeed = PollenShooterSpeed + 0.05;
        } else if (gamepad1.b) {
           PollenShooterSpeed = PollenShooterSpeed + 0.01;
        } else if (gamepad1.x){
           PollenShooterSpeed = 0;
        }

        NectarShooter.setPower(PollenShooterSpeed);
        if (gamepad1.a){
           NectarShooterSpeed = NectarShooterSpeed + 0.05;
        } else if (gamepad1.b) {
            NectarShooterSpeed =NectarShooterSpeed + 0.01;
        } else if (gamepad1.x){
            NectarShooterSpeed = 0;
        }

        telemetry.addData("Pollen Shooter Speed", PollenShooterSpeed);
        telemetry.addData("Nectar Shooter Speed", NectarShooterSpeed);

    }
}

