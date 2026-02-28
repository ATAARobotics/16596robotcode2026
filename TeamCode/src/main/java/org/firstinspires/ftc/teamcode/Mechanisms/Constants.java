package org.firstinspires.ftc.teamcode.Mechanisms;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;
//import com.bylazar.configurables.annotations.Configurable;

//@Configurable// need this to access constants from Panels-Bruce
@Config// for FTC dashboard
@Configurable // Needed for Panels

public class Constants {
    //
    // ========== Enable Section
    //
    public static final boolean DASHBOARD_ENABLED = true;       // this needs to be false for competitions!!
    public static final boolean TELEMETRY_ENABLED = true;
    public static final boolean JUST_TESTING = true;      // will be used to insert temporary test code
    //
    // ========== Setting
    //
    //public static final double TICKS_TO_INCHES = Math.PI * 48 / (25.4 * 2000);                      // for use in Odometry
    public static final double SPEED_RATIO = 1.0;  // Use this to slow down robot
    public static final double TURN_RATIO = 1.0; // use this to slow turn rate1q
    //
    // ========== Intake, Shooter and Feeder Speeds

    public static double INTAKE_SPEED = 0.70; //changed from 1.0 used for both auto and teleop
    public static double INTAKE_SPEED_AUTO = 0.55;
    public static  double FLYWHEEL_SPEED = 0.85;// in RPM --this needs to be verified; only far was test on Oct 25 -CBW
    //public static double FLYWHEEL_FUDGE_FACTOR = 2100.0;
    public static double FLYWHEEL_IDLE_SPEED = -0.3; // idle speed to start in init
    //public static double FLYWHEEL_IDLE_TARGET = FLYWHEEL_IDLE_SPEED * FLYWHEEL_FUDGE_FACTOR;
    public static double FLYWHEEL_NEAR = 0.6;
    public static double FLYWHEEL_NEAR_AUTO = 0.58;
    //public static double FLYWHEEL_NEAR_TARGET = FLYWHEEL_NEAR * FLYWHEEL_FUDGE_FACTOR;

    public static double FLYWHEEL_FAR = 0.73; // previously 0.85
    public static double FLYWHEEL_FAR_AUTO = 0.73; //was 0.82

    //public static double FLYWHEEL_FAR_TARGET = FLYWHEEL_FAR * FLYWHEEL_FUDGE_FACTOR; // previously 0.85
    public static double FLYWHEEL_REVERSE = -0.5;
    public static double FLYWHEEL_REVERSE_EXTRA = -0.5;

    public static double FLYWHEEL_MAX = 2100;
    //public static final double FLYWHEEL_RECOVERY = 0.6 * FLYWHEEL_MAX; //chnage back to 0.9
    public static double FLYWHEEL_KP = 5.0;
    public static double FLYWHEEL_KI = 0.00;
    public static double FLYWHEEL_KD = 0.0;// WAS 0.31
    public static double FAR_AUTO_AIM_ANGLE_RED = 111.67;
    public static double FAR_AUTO_AIM_ANGLE_BLUE = 111.67;//turn to face shooting  was 112.67

    public static double BLUE_FAR_SHOOTING_MOVE_OFF_WALL_X = -100;

    public static final double AUTO_DRIVE_SPEED = 0.3;
    public static final double AUTO_WAIT_TIME = 10.0;

    public static final double AUTO_INTAKE_TIME = 6.7;
  //  public static final double AUTO_STEP_DELAY = 2.0;

    public static double FEEDER_SPEED = 0.2;
    public static double FEEDER_SPEED_REVERSE = -0.15;

    //SERVO SPEED SETPOINTS
    public static double FEED_SPEED = -1.0;
    public static final double FEED_SPEED_AUTO = -0.5;
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
    public static double BLUESMALL_SHOOT = 112.67; //Small Triangle Shoot
    public static double BLUEBIG_SHOOT = 136.67;
    public static double REDSMALL_SHOOT = -112.67; //Small Triangle Shoot
    public static double REDBIG_SHOOT = -136.67;
    // ========== Tolerance Section
    //
    public static final double JOYSTICK_TOLERANCE = 0.5;
//    public static final double AT_XY_TOLERANCE = 25.0;
    public static final double TRIGGER_TOLERANCE = 0.5;
    public static double AUTO_Y_DISTANCE_ERROR = 15.0;
    public static double AUTO_X_DISTANCE_ERROR = 15.0;
    public static final double DRIVE_PID_ERROR = 1.5; // inches?? Only used in PractiseDriveTrain??
    public static double HEADING_ERROR_Tolerance = 5.0; // Degrees... this now matches PID tolerance
    //    // ========== Odometer constants
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
