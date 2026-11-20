package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Config
public class MainVariableConfigurations {
    // Camera control settings
    public static int EXPOSURE_MS = 3;
    public static int GAIN = 175;
    public static boolean VIEW_CAMERA_IN_DASHBOARD = false;
    // Camera Vision settings
    public static float DECIMATION = 2.0f; // Controls the down-sampling amount for pixels, only used for vision calculation, quality:speed.
    public static int TEST_INT = 1;

    // Lazy susan auto aim settings
    public static double AIM_KP = 0.015;            // How hard it turns per degree off. Higher = faster, lower if it wobbles
    public static double AIM_MAX_POWER = 0.5;      // Fastest it can turn while aiming
    public static double AIM_MIN_POWER = 0.15;     // Slowest it can turn (any lower and it won't move)
    public static double AIM_DEADBAND_DEG = 0.75;  // Stops when the tag is this many degrees from the middle
    public static boolean AIM_REVERSED = true;     // Flip if it turns away from the tag
}
