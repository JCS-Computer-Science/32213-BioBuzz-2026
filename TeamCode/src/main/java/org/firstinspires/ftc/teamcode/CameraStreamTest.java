package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard; // Ensure this import is here
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

@TeleOp(name = "Camera Stream Test", group = "Test")
public class CameraStreamTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // 1. Initialize the VisionPortal
        VisionPortal visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "MainCam"))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .enableLiveView(true)
                .setAutoStopLiveView(false)
                .build();

        // 2. Fetch the instance correctly using the class name directly
        FtcDashboard dashboard = FtcDashboard.getInstance();
        dashboard.startCameraStream(visionPortal, 30);

        telemetry.addData("Status", "Camera Initialized.");
        telemetry.addData(">", "Open localhost:8080/dash and check the 'Camera' box!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Status", "Running match loop");
            telemetry.update();

            sleep(20);
        }

        // Clean up the stream when stopped
        visionPortal.close();
    }
}
