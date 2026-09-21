package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name = "Camera Stream Test", group = "Test")
public class CameraStreamTest extends LinearOpMode {

    public FtcDashboard dashboard;
    @Override
    public void runOpMode() {
        VisionPortal visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "MainCam"))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .enableLiveView(true)
                .setAutoStopLiveView(false) // Keeps live view active
                .build();
        dashboard.startCameraStream(visionPortal, 30);

        // 2. Wait for the match to begin (Press INIT)
        telemetry.addData("Status", "Camera Initialized.");
        telemetry.addData(">", "Press the 3 dots -> Camera Stream NOW while on INIT!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Status", "Running match loop");
            telemetry.update();

            // Share CPU cycles
            sleep(20);
        }

        visionPortal.close();
    }
}
