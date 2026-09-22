package org.firstinspires.ftc.teamcode.vision;

import android.annotation.SuppressLint;
import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "Camera AprilTag Stream Test", group = "Test")
public class CameraStreamTest extends LinearOpMode {

    private void setManualCameraControls(VisionPortal portal) {
        while (opModeInInit() && portal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            sleep(20);
        }

        if (!opModeInInit() && !opModeIsActive()) return;

        ExposureControl exposureControl = portal.getCameraControl(ExposureControl.class);
        GainControl gainControl = portal.getCameraControl(GainControl.class);

        if (exposureControl != null && gainControl != null) {
            try {
                exposureControl.setAePriority(false);
            } catch (Exception e) {
                // Ignore if device firmware doesn't support explicit auto-exposure disabling
            }

            exposureControl.setMode(ExposureControl.Mode.Manual);
            exposureControl.setExposure(5, TimeUnit.MILLISECONDS); // Eliminate motion blur
            gainControl.setGain(gainControl.getMaxGain()); // Brightness correction
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() throws InterruptedException {

        AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .build();

        // Downsamples pixels for faster edge math
        aprilTagProcessor.setDecimation(2.0f);

        VisionPortal visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "MainCam"))
                .setCameraResolution(new Size(640, 480)) // High performance processing array
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTagProcessor)
                .enableLiveView(false) // Saves massive driver hub CPU overhead
                .setAutoStopLiveView(false)
                .build();

        // Connect to FTC Dashboard localhost:8080
        FtcDashboard dashboard = FtcDashboard.getInstance();
        dashboard.setImageQuality(100);
        dashboard.startCameraStream(visionPortal, 0);

        // Commit manual constraints prior to match execution loop
        // setManualCameraControls(visionPortal); errors at the moment

        telemetry.addData("Status", "AprilTag Scanner Ready.");
        telemetry.update();

        // Variables for true loop benchmarking calculation
        long lastTime = System.currentTimeMillis();
        int frameCount = 0;
        double calculatedFps = 0;

        waitForStart();

        while (opModeIsActive()) {
            // Fetch live detections from hardware portal
            List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();

            // Perform math checking execution cycles per second
            frameCount++;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= 1000) {
                calculatedFps = frameCount / ((currentTime - lastTime) / 1000.0);
                frameCount = 0;
                lastTime = currentTime;
            }

            // Print benchmark readouts directly on Driver Hub screen text area
            telemetry.addLine("=== PERFORMANCE BENCHMARK ===");
            telemetry.addData("True Processing Loop FPS", String.format("%.2f", calculatedFps));
            telemetry.addLine("=============================\n");

            telemetry.addData("# Tags Detected", currentDetections.size());

            for (AprilTagDetection detection : currentDetections) {
                if (detection instanceof AprilTagSingleDetection) {
                    AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                    if (singleDet.metadata != null) {
                        telemetry.addLine(String.format("\n[Single] Tag ID: %d (%s)", singleDet.id, singleDet.metadata.name));
                        telemetry.addData("X Position", singleDet.ftcPose.x);
                        telemetry.addData("Y Position", singleDet.ftcPose.y);
                        telemetry.addData("Z Position", singleDet.ftcPose.z);
                    } else {
                        telemetry.addLine(String.format("\n[Single] Tag ID: %d (Unknown Metadata)", singleDet.id));
                    }

                } else if (detection instanceof AprilTagClusterDetection) {
                    AprilTagClusterDetection clusterDet = (AprilTagClusterDetection) detection;

                    if (clusterDet.metadata != null) {
                        telemetry.addLine(String.format("\n[Cluster] Target: %s", clusterDet.metadata.name));
                    }
                }
            }

            telemetry.update();

            // Minimal sleep delay ensures fast loop cycle execution limits
            sleep(5);
        }

        visionPortal.close();
    }
}
