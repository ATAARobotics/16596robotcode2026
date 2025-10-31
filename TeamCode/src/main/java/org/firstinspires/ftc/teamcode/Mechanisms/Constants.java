package org.firstinspires.ftc.teamcode.Mechanisms;

import com.bylazar.configurables.annotations.Configurable;
@Configurable// need this to access constants from Panels-Bruce

public class Constants {
    //
    // ========== Enable Section
    //
    public static final boolean DASHBOARD_ENABLED = true;       // this needs to be false for competitions!!
    public static final boolean TELEMETRY_ENABLED = true;
    public static final boolean JUST_TESTING=true;      // will be used to insert temporary test code
    //
    // ========== Setting
    //
    public static final double TICKS_TO_INCHES = Math.PI * 48 / (25.4 * 2000);                      // for use in Odometry
    public static final double SPEED_RATIO = 1.0;  // Use this to slow down robot
    public static final double TURN_RATIO = 1.0; // use this to slow turn rate
    //
    // ========== Intake, Shooter and Feeder Speeds
    //
    public static final double INTAKE_SPEED = 1.0;
    public static final double FLYWHEEL_SPEED = 0.7;// this needs to be verified; only far was test on Oct 25 -CBW
    public static final double FLYWHEEL_CLOSE = 0.7;
    public static final double FLYWHEEL_FAR = 0.71;
    public static final double FLYWHEEL_RECOVERY = 1750.0;

    public static final double FLYWHEEL_KP = 0.05;
    public static final double FLYWHEEL_KI = 0.01;
    public static final double FLYWHEEL_KD = 0.31;
    //public static final double AUTO_DRIVE_SPEED = 0.3;
  //  public static final double AUTO_STEP_DELAY = 2.0;
    public static final double FEED_SPEED = -1.0;
    //
    // ========== Define Drive constants.  Make them public so they CAN be used by the calling OpMode
    //
    public static final double FORWARD = 0; // north
    public static final double BACK = 180; // south
    public static final double RIGHT = -90; // east
    public static final double LEFT = 90; //west
    public static final double NORTH = 0;
    public static final double SOUTH = 180;
    public static final double EAST = -90;
    public static final double WEST = 90;
    public static final double NORTH_EAST = -45; // North East
    public static final double SOUTH_EAST = -135; // South East
    public static final double SOUTH_WEST = 135; // South West
    public static final double NORTH_WEST = 45; // North West
    //
    // ========== Tolerance Section
    //
    public static final double JOYSTICK_TOLERANCE = 0.5;
//    public static final double AT_XY_TOLERANCE = 25.0;
//    public static double AUTO_Y_DISTANCE_ERROR = 15.0;
//    public static double AUTO_X_DISTANCE_ERROR = 15.0;
    public static final double DRIVE_PID_ERROR = 1.5; // inches?? Only used in PractiseDriveTrain??
    public static double HEADING_ERROR_Tolerance = 5.0; // Degrees... this now matches PID tolerance
    //
    // ========== Odometer constants
    //
//    public static int ODOMETER_PRACTICE_MODE = 1;
//    public static int ODOMETER_COMPETITION_MODE = 8;
    public static double ODOMETER_X_OFFSET = 0;// left offset is +  uppdated after contact with gobilda support
    public static double ODOMETER_Y_OFFSET = 24.0;
    //
    // ========== auto constants
    //
    public static double XPID_Kp = 0.018;// was 0.14
    public static double YPID_Kp = 0.018;
    public static double HEADING_Kp = 0.015; // was 0.01
    //
    // ========== Limelight Settings
    //
    public static int LIMELIGHT_APRIL_TAG_BLUE = 0;
    public static int LIMELIGHT_APRIL_TAG_RED = 1;
    public static int LIMELIGHT_APRIL_TESTING = 2;
    public static double LIMELIGHT_GOAL_HEIGHT_INCHES = 2.5; //29.5
    public static double LIMELIGHT_LENS_HEIGHT_INCHES = 2;
    public static double LIMELIGHT_MOUNT_ANGLE_DEGREE = 90.0;
    //
    // ========== RGB
    //
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
