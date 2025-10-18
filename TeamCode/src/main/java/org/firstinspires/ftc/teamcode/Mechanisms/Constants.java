package org.firstinspires.ftc.teamcode.Mechanisms;

public class


Constants {
    public static final boolean DASHBOARD_ENABLED = true;       // this needs to be false for competitions!!
    public static final boolean TELEMETRY_ENABLED = true;
    public static final boolean JUST_TESTING=true;      // will be used to insert temporary test code

    public static final double TICKS_TO_INCHES = Math.PI * 48 / (25.4 * 2000);                      // for use in Odometry
    public static final double SPEED_RATIO = 1.0;  // Use this to slow down robot
    public static final double TURN_RATIO = 1.0; // use this to slow turn rate
// intake and shooter speeds
    public static final double INTAKE_SPEED = 1.0;
    public static final double FLYWHEEL_SPEED = 1.0;
    public static final double FLYWHEEL_KP = 0.05;
    public static final double FLYWHEEL_KI = 0.01;
    public static final double FLYWHEEL_KD = 0.31;
    public static final double AUTO_DRIVE_SPEED = 0.3;
    public static final double AUTO_STEP_DELAY = 2.0;



    //
    // ========== Define Drive constants.  Make them public so they CAN be used by the calling OpMode
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

    //   SERVO CONSTANTS  // finger servo closed position
    // public static final double STEP = 0.05; // used for manual increment of wrist servo
    // public static final double  LF_CLOSED       =  0.3 ;// finger servo closed position
    public static final double  LF_OPEN    =  0.7 ; // open position
    public static final double  RF_CLOSED       =  0.6 ;
    public static final double  RF_OPEN    =  0.2 ; // open position*/
// for elevator moves
    public static final int ELEVATOR_START  =  0;
    public static final int ELEVATOR_PICKUP_SPECIMEN = 40;
    public static final int ELEVATOR_HIGH_BAR = 1833;  // nov 22

    // public static final int ELEVATOR_LOW_BAR= 350;  // nov 22

    public static final int ELEVATOR_HIGH_BASKET = 3578;
    public static final int ELEVATOR_MAX = 4337;
    public static final int ELEVATOR_MIN = 0;
    public static final double ELEVATOR_SPEED_FACTOR = 0.7; // 1.0 Old elevator and 0.3 for new elevator
//    public static final double ELEVATOR_POSITION_TOLERANCE = 15.0;
//    public static final double ELEVATOR_POSITION_COEFFICIENT = 0.05;
//    public static final int ELEVATOR_STUCK_COUNT = 5;
    // Claw constants
    //robot 2 need to be fixed, values were revered
    public static boolean CLAW_OPEN = true;
    public static boolean CLAW_CLOSED = false;
    public static final double SERVO_CLAW_OPEN = 0.2 ;
    public static final double SERVO_CLAW_CLOSED = 0.8 ;

// ===========  for elbow movements ======================
//robot 2 need to be fixed, values were revered
   // public static final double ELBOW_BOTTOM = 0.3;
    public static final double ELBOW_START_POSITION = 0.0;
//    public static final double ELBOW_PICKUP_SAMPLE = 1.0;
    public static final double ELBOW_PICKUP_SPECIMEN = 0.4;
    //Pickup specimen from the wall for CompBot (Tortuga)
    public static final double ELBOW_PICKUP_SPECIMEN_WALL = 0.5;
    public static final double ELBOW_DEPOSIT_SAMPLE_HIGH_BUCKET = 0.67;

    public static int ELBOW_MAX = 3;
    public static int ELBOW_MIN = 0;
    //
// Tolerance Section
    public static final double JOYSTICK_TOLERANCE = 0.5;
    public static final double AT_XY_TOLERANCE = 25.0;
    public static double AUTO_Y_DISTANCE_ERROR = 15.0;
    public static double AUTO_X_DISTANCE_ERROR = 15.0;
    public static final double DRIVE_PID_ERROR = 1.5; // inches?? Only used in PractiseDriveTrain??
    public static double HEADING_ERROR_Tolerance = 5.0; // Degrees... this now matches PID tolerance
    // Bruce:sept30: change these for use in elevator?kk
//    public static final double ARM_RADIANS_PER_TICK = Math.PI/244.0;
//    public static final double ARM_TICKS_PER_90DEG = 113;   // CONFIRM #TICKS PER 90.
   // public static final double DRIVE_PID_TOLERANCE = 1;//testing changed from .5 to 1
    //odometer constants
    public static int ODOMETER_PRACTICE_MODE = 1;
    public static int ODOMETER_COMPETITION_MODE = 8;
    public static double ODOMETER_X_OFFSET = 0;// left offset is +  uppdated after contact with gobilda support
    public static double ODOMETER_Y_OFFSET = 24.0;
    // auto constants
    public static double XPID_Kp = 0.018;// was 0.14
    public static double YPID_Kp = 0.018;
    public static double HEADING_Kp = 0.015; // was 0.01


    // Limelight Settings
    public static int LIMELIGHT_APRIL_TAG_BLUE = 0;
    public static int LIMELIGHT_APRIL_TAG_RED = 1;
    public static double LIMELIGHT_GOAL_HEIGHT_INCHES = 29.50;
    public static double LIMELIGHT_LENS_HEIGHT_INCHES = 8.25;
    public static double LIMELIGHT_MOUNT_ANGLE_DEGREE = 1.0;

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
