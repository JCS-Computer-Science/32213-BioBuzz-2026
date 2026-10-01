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
}
