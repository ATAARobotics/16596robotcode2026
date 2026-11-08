package org.firstinspires.ftc.teamcode.Utils;

import com.bylazar.configurables.annotations.Configurable;

@Configurable // Needed for Panels

public class Constant {
    //========== SPEED RATIO SETTINGS ===============================
    public static final double DRIVER_SPEED_RATIO = 1.0;  // Use this to slow down robot
    public static final double OPER_SPEED_RATIO = DRIVER_SPEED_RATIO * 0.567;
    public static final double TURN_RATIO = 1.0;
    public static final double MAX_TURN_SPEED = 1.0;

    //========== INTAKE =============================================
    public static final double INTAKE_SPEED_PICKUP = 4.0;//was 1.0
    public static final double INTAKE_SPEED_HOLDING = 1.0;

    //========== SHOOTER ============================================
    public static final double SHOOTER_KP = 5.0;
    public static final double SHOOTER_KI = 0.00;
    public static final double SHOOTER_KD = 0.0;
    public static final double SHOOTER_MAX = 2100;
    public static final double SHOOTER_SPEED_HOLDING = 1.0;
    public static final double SHOOTER_SPEED_SHOOTING = -1.0;

    //========== FEEDER =============================================
    public static double FEEDER_SPEED_CORRECTING = -0.8;//was -0.6
    public static double FEEDER_SPEED_HOLDING = -0.1;
    public static double FEEDER_SPEED_PICKUP = -0.5;
    public static final double FEEDER_SPEED_FWD = -1.0;
    public static final double FEEDER_SPEED_REVERSE = 1.0;
    public static final double FEEDER_SPEED_AUTO = -0.5;
    public static final double CORRECTION_DELAY = 1.7;

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
    public static final double TOLERANCE_JOYSTICK = 0.5;
    public static final double TOLERANCE_TRIGGER = 0.5;


    //========== ODOMETER ===========================================
    public static final double ODOMETER_X_OFFSET = 0.0;
    public static final double ODOMETER_Y_OFFSET = 0.0;


    //========== AUTO ================================================
    public static double AUTO_DRIVE_SPEED = 0.5567;
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
        public static final double BLUE =  0.611;
        public static final double INDIGO = 0.666;
        public static final double VIOLET = 0.722;
        public static final double WHITE = 1.000;
        public static final double ON = 1.000;
    }
}