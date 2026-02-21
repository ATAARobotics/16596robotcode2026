package org.firstinspires.ftc.teamcode.Mechanisms;
import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
//import com.bylazar.configurables.annotations.Configurable;

//@Configurable// need this to access constants from Panels-Bruce
@Config// for FTC dashboard
@Configurable // Needed for Panels

public class C2 {
    public static final double SPEED_RATIO = 1.0;  // Use this to slow down robot
    public static final double OPER_SPEED_RATIO = SPEED_RATIO * 0.567;
    public static final double TURN_RATIO = 1.0; // use this to slow turn rate1q
    //
    // ========== Intake, Shooter and Feeder Speeds
    public static double INTAKE_SPD_PICKUP = 1.0;
    public static double INTAKE_SPD_SHOOTING = 0.83;
    public static double INTAKE_SPD_CORRECTING = 0.1;
    public static double INTAKE_SPD_HOLDING = 0.1;
    public static double FEEDER_SPD_CORRECTING = -0.4;
    public static double FEEDER_SPD_HOLDING = -0.1;
    public static double FEEDER_SPD_PICKUP = -0.1;
    public static double FEEDER_SPD_NEAR = 0.75;
    public static double FEEDER_SPD_FAR = 0.60;
    //public static double FLYWHEEL_IDLE_SPEED = 0.3; // might not use this anymore
    //public static double FLYWHEEL_NEAR = 0.62;
    public static double FLYWHEEL_SPD_NEAR = 0.58;
    //public static double FLYWHEEL_FAR = 0.78; // previously 0.85
    public static double FLYWHEEL_SPD_FAR = 0.78; //was 0.82
    public static double FLYWHEEL_SPD_REVERSE = -0.5;
    public static double FLYWHEEL_MAX = 2100; // Used for calculating speed feedback for at speed setpoint
    public static double FLYWHEEL_KP = 5.0;
    public static double FLYWHEEL_KI = 0.00;
    public static double FLYWHEEL_KD = 0.0;
//    public static double FAR_AUTO_AIM_ANGLE_RED = 111.67;
//    public static double FAR_AUTO_AIM_ANGLE_BLUE = 111.67;//turn to face shooting  was 112.67
//    public static double BLUE_FAR_SHOOTING_MOVE_OFF_WALL_X = -100;
//    public static final double AUTO_WAIT_TIME = 10.0;
  //  public static final double AUTO_STEP_DELAY = 2.0;
  public static final double FEED_SPD_FWD = -1.0;
    public static final double FEED_SPD_REVERSE = 1.0;
    public static final double FEED_SPEED_AUTO = -0.5;
    public static double CORRECTION_DELAY = 1.65;
    //
    // ========== Define Drive constants.  Make them public so they CAN be used by the calling OpMode
    //
    public static final double FORWARD = 0; // north
    public static final double BACK = -180; // south
    public static final double RIGHT = -90; // east
    public static final double LEFT = 90; //west
    public static final double NORTH = 0;
    public static final double SOUTH = -180;
    public static final double EAST = -90;
    public static final double WEST = 90;
    public static final double NORTH_EAST = -45; // North East
    public static final double SOUTH_EAST = -135; // South East
    public static final double SOUTH_WEST = 135; // South West
    public static final double NORTH_WEST = 45; // North West
    public static final double BLUESMALL_SHOOT = 112.67; //Small Triangle Shoot
    public static final double BLUEBIG_SHOOT = 136.67;
    public static final double REDSMALL_SHOOT = -112.67; //Small Triangle Shoot
    public static final double REDBIG_SHOOT = -136.67;
    // ========== Tolerance Section
    //
    public static final double JOYSTICK_TOLERANCE = 0.5;
//    public static final double AT_XY_TOLERANCE = 25.0;
    public static final double TRIGGER_TOLERANCE = 0.5;


    //    // ========== Odometer constants
    //
//    public static int ODOMETER_PRACTICE_MODE = 1;
//    public static int ODOMETER_COMPETITION_MODE = 8;
    public static final double ODOMETER_X_OFFSET = 0;// left offset is +  uppdated after contact with gobilda support
    public static final double ODOMETER_Y_OFFSET = 24.0;
    //
    // ========== auto constants
    //
    public static double AUTO_DRIVE_SPEED = 0.5567;
    public static double AUTO_DRIVE_SPEED_PICKUP = 0.1067;
    public static final double AUTO_DRIVE_SPEED_MIN = 0.1067;
    public static double AUTO_DRIVE_SPEED_MIN_STRAFEFACTOR = 1.35;
    public static double AUTO_Y_DISTANCE_ERROR = 5.67;
    public static double AUTO_X_DISTANCE_ERROR = 5.67;
    public static final double XPID_Kp = 0.00167;
    public static final double YPID_Kp = 0.00267;
    public static double HEADING_ERROR_Tolerance = 1.5; // Degrees
    public static final double HEADING_Kp = 0.001;
    //
    // ========== Limelight Settings
    //
    public static final int LIMELIGHT_APRIL_TAG_BLUE = 0;
    public static final int LIMELIGHT_APRIL_TAG_RED = 1;
    public static final int LIMELIGHT_APRIL_TESTING = 2;
    public static final double LIMELIGHT_GOAL_HEIGHT_INCHES = 2.5; //29.5
    public static final double LIMELIGHT_LENS_HEIGHT_INCHES = 2;
    public static final double LIMELIGHT_MOUNT_ANGLE_DEGREE = 90.0;
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
