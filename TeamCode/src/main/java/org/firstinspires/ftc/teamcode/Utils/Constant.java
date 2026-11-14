package org.firstinspires.ftc.teamcode.Utils;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

@Configurable // Needed for Panels
@Config
public class Constant {
    //========== SPEED RATIO SETTINGS ===============================
    public static final double DRIVER_SPEED_RATIO = 0.6;  // Use this to slow down robot
    public static final double OPER_SPEED_RATIO = DRIVER_SPEED_RATIO * 0.567;
    public static final double TURN_RATIO = 1.0;
    public static final double MAX_TURN_SPEED = 1.0;

    //========== INTAKE =============================================
    public static final double INTAKE_SPEED_STOP = 0.0;
    public static final double INTAKE_SPEED_PICKUP = 1.0;
    public static final double INTAKE_SPEED_HOLDING = 1.0;

    //========== SHOOTER =============================================
    public static final double SHOOTER_RPM_STOP = 0.0;
    public static final double SHOOTER_RPM_HOLDING = -1500.0;
    public static double SHOOTER_RPM_SHOOTING = 4000.0;


    public static final double SHOOTER_READY_TOLERANCE_RPM = 150.0;
    //========== SHOOTER ============================================

    public static final double SHOOTER_SPEED_STOP = 0.0;
    public static final double SHOOTER_SPEED_HOLDING = -1.0;
    public static final double SHOOTER_SPEED_SHOOTING = 1.0;
    public static final double SHOOTER_TICKS_PER_REV = 28.0;
    public static final double SHOOTER_KP = 5.0;
    public static final double SHOOTER_KI = 0.00;
    public static final double SHOOTER_KD = 0.0;
    public static final double SHOOTER_MAX = 2100;

    //========== SERVOS =============================================

    public static final double NECTAR_LIFT_DOWN = 0.0;
    public static final double NECTAR_LIFT_UP = 1.0;
    public static final double SHOOTER_RAMP_UP = 0.0;
    public static final double SHOOTER_STOPPER_CLOSED = 0.0;
    public static final double SHOOTER_STOPPER_OPEN = 1.0;

    public static final double SHOOTER_RAMP_DOWN = 1.0;
    public static final double SHOOTER_RAMP_IDLE = 0.5;
    //========== Shooter Ramp RPM Compensation =======================
    public static double SHOOTER_RAMP_NOMINAL = 0.50;
    public static double SHOOTER_RAMP_MIN = 0.25;
    public static double SHOOTER_RAMP_MAX = 0.50;
    public static double SHOOTER_RAMP_RPM_KP = 0.00025;


    //========== FEEDER =============================================
    public static final double FEEDER_SPEED_STOP = 0.0;
    public static final double FEEDER_SPEED_CORRECTING = -0.8;
    public static final double FEEDER_SPEED_HOLDING = -0.1;
    public static final double FEEDER_SPEED_PICKUP = -0.5;
    public static final double FEEDER_SPEED_FWD = -1.0;
    public static final double FEEDER_SPEED_REVERSE = 1.0;
    public static final double FEEDER_SPEED_AUTO = -0.5;
    public static final double CORRECTION_DELAY = 1.7;  //  Should this move to a timers section?

    //========== DRIVE ==============================================
    public static final double DRIVE_FORWARD = 0; // north
    public static final double DRIVE_BACK = -180; // south
    public static final double DRIVE_RIGHT = -90; // east
    public static final double DRIVE_LEFT = 90; //west
    public static final double DRIVE_NORTH = 0;
    public static final double DRIVE_SOUTH = -180;
    public static final double DRIVE_EAST = -90;
    public static final double DRIVE_WEST = 90;
    public static final double DRIVE_NORTH_EAST = -45; // North East
    public static final double DRIVE_SOUTH_EAST = -135; // South East
    public static final double DRIVE_SOUTH_WEST = 135; // South West
    public static final double DRIVE_NORTH_WEST = 45; // North West


    //========== TOLERANCE ==========================================
    public static final float TOLERANCE_JOYSTICK = 0.05F;
    public static final float TOLERANCE_TRIGGER = 0.05F;


    //========== ODOMETER ===========================================
    public static final double ODOMETER_X_OFFSET = 0.0;
    public static final double ODOMETER_Y_OFFSET = 0.0;


    //========== AUTO ================================================
    public static final double AUTO_DRIVE_SPEED = 0.5567;
    public static final double AUTO_DRIVE_SPEED_MIN = 0.1067;
    public static final double AUTO_DRIVE_SPEED_MIN_STRAFEFACTOR = 1.35;
    public static final double AUTO_Y_DISTANCE_ERROR = 5.67;
    public static final double AUTO_X_DISTANCE_ERROR = 5.67;
    public static final double AUTO_XPID_Kp = 0.00167;
    public static final double AUTO_YPID_Kp = 0.00267;
    public static final double AUTO_HEADING_ERROR_TOLERANCE = 1.5; // Degrees
    public static final double AUTO_HEADING_Kp = 0.001;


    //========== LIMELIGHT ==========================================
    public static final int LIMELIGHT_APRIL_TAG_BLUE = 0;
    public static final int LIMELIGHT_APRIL_TAG_RED = 1;
    public static final int LIMELIGHT_APRIL_TESTING = 2;
    public static final double LIMELIGHT_GOAL_HEIGHT_INCHES = 2.5; //29.5
    public static final double LIMELIGHT_LENS_HEIGHT_INCHES = 2;
    public static final double LIMELIGHT_MOUNT_ANGLE_DEGREE = 90.0;
    public static final double MOTOR_ADJUSTER_POLLEN = -1.0;
    public static final double MOTOR_ADJUSTER_NECTAR = 1.0;

    //========== COLOR SENSOR =======================================
    public static final double COLOR_THRESHOLD = 0.05;

    //========== DEPENDANT ON SHOOTER SPEED =========================
    public static final double POLLEN_GATE_OPEN = 1.0;// open when shooter up to speed
    public static final double NECTAR_GATE_OPEN = 1.0;//  open when shooter up to speed
    public static final double POLLEN_GATE_CLOSE = -1.0;// no button
    public static final double NECTAR_GATE_CLOSE = -1.0;// no button

    //========== RGB ================================================
        public static final class RGB_Light {
        public static final double OFF = 0.0;
        public static final double BLACK = 0.0;
        public static final double RED = 0.279;
        public static final double ORANGE = 0.333;
        public static final double YELLOW = 0.388;
        public static final double SAGE = 0.444;
        public static final double GREEN = 0.5;
        public static final double AZURE = 0.555;
        public static final double BLUE = 0.611;
        public static final double INDIGO = 0.666;
        public static final double VIOLET = 0.722;
        public static final double WHITE = 1.000;
        public static final double ON = 1.000;
    }
    //========== Timer ================================================
        public static final double TIME_ONE_SECOND = 1.0;//change name later

}