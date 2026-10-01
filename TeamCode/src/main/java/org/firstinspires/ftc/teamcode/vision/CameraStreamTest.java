package org.firstinspires.ftc.teamcode.vision;

// Import Main Variables
import static org.firstinspires.ftc.teamcode.MainVariableConfigurations.DECIMATION;

import org.firstinspires.ftc.teamcode.MainVariableConfigurations;
// Import Main Functions

import android.annotation.SuppressLint;
import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.sun.tools.javac.Main;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

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
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTagProcessor)
//                .enableLiveView(false)
                .setAutoStopLiveView(false)
                .build();

        VisionFunctions visionFunctions = new VisionFunctions();

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // Modify Init Variables
        aprilTagProcessor.setDecimation(DECIMATION);
        dashboard.setImageQuality(100);
        if (MainVariableConfigurations.VIEW_CAMERA_IN_DASHBOARD) dashboard.startCameraStream(visionPortal, 0);

        // Wait for streaming to begin during Init stage to push initial config
        while (!isStopRequested() && visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            sleep(20);
        }

        waitForStart();

        while (opModeIsActive()) {

            List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();
            
            telemetry.addData("Vision Portal FPS", visionPortal.getFps());

            telemetry.addData("Number of Tags Detected", currentDetections.size());

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

            telemetry.addData("Test Int", MainVariableConfigurations.TEST_INT);
            telemetry.update();
            sleep(5);
        }
        visionPortal.close();
    }
}
