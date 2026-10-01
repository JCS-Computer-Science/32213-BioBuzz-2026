package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@Config

@TeleOp(name = "Test Config", group = "Test")
public class TestConfig extends LinearOpMode {
    public static int TEST_INT = 1;
    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        while (opModeIsActive())
        {
            telemetry.addData("TestINT", TEST_INT);
            telemetry.update();
        }

    }
}
