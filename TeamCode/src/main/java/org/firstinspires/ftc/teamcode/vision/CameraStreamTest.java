package org.firstinspires.ftc.teamcode.vision;

import android.annotation.SuppressLint;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection; // For reading data
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor; // For processing tags

import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;


import java.util.List;

@TeleOp(name = "Camera AprilTag Stream Test", group = "Test")
public class CameraStreamTest extends LinearOpMode {

    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() throws InterruptedException {

        AprilTagProcessor aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)       // Draws x/y/z orientation axes on the tag
                .setDrawCubeProjection(true) // Draws a 3D box over the tag
                .setDrawTagOutline(true) // Highlights the square border of the tag
                .build();

        VisionPortal visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "MainCam"))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTagProcessor)
                .enableLiveView(true)
                .setAutoStopLiveView(false)
                .build();

        // Connect to FTC Dashboard
        FtcDashboard dashboard = FtcDashboard.getInstance();
        dashboard.startCameraStream(visionPortal, 30);

        telemetry.addData("Status", "AprilTag Scanner Ready.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();
            telemetry.addData("# Tags Detected", currentDetections.size());

            for (AprilTagDetection detection : currentDetections) {
                // Check if the detection is a Standard Single Tag
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

                    // Check if the detection is part of an AprilTag Cluster Target
                } else if (detection instanceof AprilTagClusterDetection) {
                    AprilTagClusterDetection clusterDet = (AprilTagClusterDetection) detection;

                    if (clusterDet.metadata != null) {
                        // Clusters are identified by their cluster name rather than individual IDs
                        telemetry.addLine(String.format("\n[Cluster] Target: %s", clusterDet.metadata.name));
                    }
                }
            }

            telemetry.update();
            sleep(50); // Share CPU cycles with the web video encoder
        }

        visionPortal.close();
    }
}
