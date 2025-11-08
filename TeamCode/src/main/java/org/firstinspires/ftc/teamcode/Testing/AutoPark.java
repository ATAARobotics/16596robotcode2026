package org.firstinspires.ftc.teamcode.Testing;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Mechanisms.CAITelemetry;
import org.firstinspires.ftc.teamcode.Mechanisms.Constants;
import org.firstinspires.ftc.teamcode.Mechanisms.DriveTrainBasic;
import org.firstinspires.ftc.teamcode.Subsystem.LightIndicatorSubsystem;

import java.util.Locale;
public class AutoPark{
    @Autonomous(name = "Auto_Leave")
    public class AutoLeave extends OpMode {
        private DriveTrainBasic driveTrain;
        private int current_step;
        private boolean autodone = false;
        private int total_waypoints = 20;
        private WayPoint[] wayPoints;
        private HardwareMap HardwareMap;

        public void init_loop(){
            driveTrain.odometer.update();
            driveTrain.odometer.resetPosAndIMU();
            CAITelemetry telemetry = null;
            telemetry.addData("Sequence Step", this.current_step);
            Pose2D pos = driveTrain.odometer.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Position", data);
            data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", driveTrain.getXPosition(), driveTrain.getYPosition(), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Function Position", data);
            // This gets called when we have zero out and ready to hit play.
            if (driveTrain.getXPosition() <= 0.1 && driveTrain.getYPosition() <= 0.1) {
                telemetry.addData("Initialized:","Done");
            }
        }
        public void init(){
            HardwareMap hardwareMap;
            DriveTrainBasic auto;
            driveTrain = new DriveTrainBasic(HardwareMap);
            driveTrain.init();
            driveTrain.odometer.resetPosAndIMU();
            driveTrain.odometer.update();
            this.current_step = 1;
            // Display Setup
            telemetry.addData("Sequence Step", this.current_step);
            Pose2D pos = driveTrain.odometer.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Position", data);
            data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", driveTrain.getXPosition(), driveTrain.getYPosition(), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Function Position", data);


            //Waypoint setup
            this.wayPoints = new WayPoint[this.total_waypoints + 1];
            this.wayPoints[0] = new WayPoint();
            this.wayPoints[0].x = 0.0;
            this.wayPoints[0].y = 0.0;
            this.wayPoints[0].facing = Constants.NORTH;
            // WayPoint 1
            this.wayPoints[1] = new WayPoint();
            this.wayPoints[1].x = 25.0;
            this.wayPoints[1].x_speed = 0.3;
            this.wayPoints[1].y = 0.0;
            this.wayPoints[1].y_speed = 0.0;
            this.wayPoints[1].facing = Constants.NORTH;
            // Done Waypoint 1
            // WayPoint 2
            this.wayPoints[2] = new WayPoint();
            this.wayPoints[2].x = 25.0;
            this.wayPoints[2].x_speed = 0.0;
            this.wayPoints[2].y = -1058.0;
            this.wayPoints[2].y_speed = 0.3;
            this.wayPoints[2].facing = Constants.NORTH;
            //Done Waypoint 2
        }//init
        @Override
        public void loop(){
            driveTrain.odometer.update();

            // Send information to the display
            telemetry.addData("Sequence Step", this.current_step);

            Pose2D pos = driveTrain.odometer.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Position", data);
            data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", driveTrain.getXPosition(), driveTrain.getYPosition(), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Function Position", data);
            if (this.wayPoints[this.current_step] != null){
                telemetry.addData("At Waypoint",at_xy(this.wayPoints[this.current_step]));
                telemetry.addData("At Waypoint X",this.at_x(this.wayPoints[this.current_step].x));
                telemetry.addData("At Waypoint Y",this.at_y(this.wayPoints[this.current_step].y));
                telemetry.addData("Current Waypoint","X: %.3f, Y %.3f",this.wayPoints[this.current_step].x,this.wayPoints[this.current_step].y);
            }
            else {
                telemetry.addData("At Waypoint",this.at_xy(this.wayPoints[this.current_step-1]));
                telemetry.addData("At Waypoint X",this.at_x(this.wayPoints[this.current_step-1].x));
                telemetry.addData("At Waypoint Y",this.at_y(this.wayPoints[this.current_step-1].y));
                telemetry.addData("Current Waypoint","X: %.3f, Y %.3f",this.wayPoints[this.current_step-1].x,this.wayPoints[this.current_step-1].y);
            }
            telemetry.addData("heading:","%.2f",driveTrain.heading);
            telemetry.addData("heading error:","%.2f",driveTrain.headingError);
            telemetry.addData("heading correction:","%.2f",driveTrain.headingCorrection);
            //starting waypoint navigation
            switch (this.current_step) {
                case 1: // Move an inch from the wall
                    driveTrain.setFacing(this.wayPoints[this.current_step].facing);
                    if (!this.at_xy(this.wayPoints[this.current_step]))
                    {
                        this.goto_xy(this.wayPoints[this.current_step]);
                    }
                    else
                    {
                        driveTrain.drive(0.0,0.0);
                        this.current_step++;
                    }
                    break;
                case 2: // Move over to Park
                    if (!this.at_xy(this.wayPoints[this.current_step]))
                    {
                        this.goto_xy(this.wayPoints[this.current_step]);
                    }
                    else
                    {
                        driveTrain.drive(0.0,0.0);
                        this.current_step++;
                    }
                    // this.current_step++;
                    break;
                default:
                    driveTrain.setFacing(Constants.NORTH);
                    driveTrain.drive(0.0,0.0);
                    autodone = true;
                    break;
            }
            // Done Processing Waypoints
            // Process loop
            //elevator.setElevatorState(3);
            stop();// put here for testing bw
            if (!autodone) {
                driveTrain.loop();
            }
        }

        @Override
        public void stop() {
      //      super.stop();
        }
        public boolean at_xy(WayPoint currentWaypoint)
        {
            return this.at_x(currentWaypoint.x) && this.at_y(currentWaypoint.y);
        }
        public boolean at_x(double targetX)
        {
            return (Math.abs(targetX) - Math.abs(driveTrain.getXPosition())) <= Constants.AUTO_X_DISTANCE_ERROR;
        }

        public boolean at_y(double targetY)
        {
            return (Math.abs(targetY) - Math.abs(driveTrain.getYPosition())) <= Constants.AUTO_Y_DISTANCE_ERROR;
        }

        public void goto_xy(WayPoint targetWaypoint) {
            // New Way to get there
            driveTrain.xControl.setSetPoint(targetWaypoint.x);
            driveTrain.yControl.setSetPoint(targetWaypoint.y);
            driveTrain.drive(driveTrain.xControl.calculate(driveTrain.getXPosition())*Constants.AUTO_DRIVE_SPEED,driveTrain.yControl.calculate(driveTrain.getYPosition())*Constants.AUTO_DRIVE_SPEED);

            // New Way End
            // Old Way Start
            // Check to see if you are at y. If yes, then process x;
            // Process y direction
//        if (!this.at_y(targetWaypoint.y))  {
//            if (targetWaypoint.y >= driveTrain.getYPosition()) {
//                this.driveTrain.drive(0.0, targetWaypoint.y_speed);
//            }
//            else {
//                this.driveTrain.drive(0.0,-targetWaypoint.y_speed);
//            }
//            return false;
//        }
//        // Process x direction
//        if (!this.at_x(targetWaypoint.x))  {
//            if (targetWaypoint.x >= driveTrain.getXPosition()) {
//                this.driveTrain.drive(targetWaypoint.x_speed, 0.0);
//            }
//            else {
//                this.driveTrain.drive(-targetWaypoint.x_speed, 0.0);
//            }
//            return false;
//        }
//        else
//        {
//            this.driveTrain.drive(0.0,0.0);
//        }
//        return true;
            // End of Old Way
        }

    }
    public class WayPoint {
        public double x;
        public double x_speed = 0.2;
        public double y;
        public double y_speed = 0.2;
        public double facing;
    }
}
