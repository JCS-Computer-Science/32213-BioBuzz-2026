package org.firstinspires.ftc.teamcode.vision;

// Import Main Variables
import static org.firstinspires.ftc.teamcode.MainVariableConfigurations.DECIMATION;

import android.annotation.SuppressLint;
import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

@Config // 1. Expose this entire class to FTC Dashboard configuration panel
@TeleOp(name = "Camera AprilTag Stream Test", group = "Test")
public class CameraStreamTest extends LinearOpMode {

    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {
        // INIT Main variables from dependency scripts
        AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .build();

        VisionPortal visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "MainCam"))
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTagProcessor)
                .enableLiveView(false)
                .setAutoStopLiveView(false)
                .build();

        VisionFunctions visionFunctions = new VisionFunctions();

        FtcDashboard dashboard = FtcDashboard.getInstance();

        // Modify Init Variables
        aprilTagProcessor.setDecimation(DECIMATION);
        dashboard.setImageQuality(100);
        dashboard.startCameraStream(visionPortal, 0);

        telemetry.addData("Status", "AprilTag Scanner Ready.");
        telemetry.update();

        // Wait for streaming to begin during INIT stage to push initial config
        while (!isStopRequested() && visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            sleep(20);
        }

        if (!isStopRequested()) {
            visionFunctions.updateCameraControls(visionPortal);
        }

        long lastTime = System.currentTimeMillis();
        int frameCount = 0;
        double calculatedFps = 0;

        waitForStart();

        while (opModeIsActive()) {
            // Keep monitoring the dashboard variables for real-time updates
            visionFunctions.updateCameraControls(visionPortal);

            List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();

            frameCount++;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= 1000) {
                calculatedFps = frameCount / ((currentTime - lastTime) / 1000.0);
                frameCount = 0;
                lastTime = currentTime;
            }

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
                    }
                }
            }

            telemetry.update();
            sleep(5);
        }
        visionPortal.close();
    }
}
