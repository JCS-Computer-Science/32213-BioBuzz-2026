package org.firstinspires.ftc.teamcode.opMode;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.MainVariableConfigurations;
import org.firstinspires.ftc.teamcode.pedro.Constants;
import org.firstinspires.ftc.teamcode.vision.VisionFunctions;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

@TeleOp(name = "Pedro TeleOp", group = "MAIN")
public class drivingTestPedro extends LinearOpMode {
    private boolean fieldCentric = true;
    private boolean lastA = false;
    private boolean lastY = false;
    private boolean autoAim = false;
    private boolean lastB = false;

    private Follower follower;
    private DcMotor inTake;
    private DcMotor transfer;
    private DcMotor lazy_susan;
    private DcMotor shooter;

    // Camera is on the lazy susan, so turning the lazy susan turns the camera
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private final VisionFunctions visionFunctions = new VisionFunctions();

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        follower = Constants.create(hardwareMap);
        inTake = hardwareMap.get(DcMotor.class, "intake");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        lazy_susan = hardwareMap.get(DcMotor.class, "lazy_susan");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

        // Set up the camera (tryGet gives null instead of crashing if the webcam isn't in the config)
        WebcamName webcam = hardwareMap.tryGet(WebcamName.class, "Webcam 1");
        if (webcam != null) {
            aprilTagProcessor = new AprilTagProcessor.Builder()
                    .setDrawAxes(true)
                    .setDrawCubeProjection(true)
                    .setDrawTagOutline(true)
                    .build();
            aprilTagProcessor.setDecimation(MainVariableConfigurations.DECIMATION);

            visionPortal = new VisionPortal.Builder()
                    .setCamera(webcam)
                    .setCameraResolution(new Size(640, 480))
                    .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                    .addProcessor(aprilTagProcessor)
                    .setAutoStopLiveView(false)
                    .build();

            if (MainVariableConfigurations.VIEW_CAMERA_IN_DASHBOARD) {
                FtcDashboard.getInstance().startCameraStream(visionPortal, 0);
            }
        } else {
            telemetry.addLine("Webcam 1 not found, auto aim won't work");
            telemetry.update();
        }

        waitForStart();

        while (opModeIsActive()) {
            // Toggle field centric mode on button press
            if (gamepad1.a && !lastA) {
                fieldCentric = !fieldCentric;
            }
            lastA = gamepad1.a;

            // Reset pose/heading without toggling out of field centric mode
            if (gamepad1.y && !lastY) {
                follower.setPose(new Pose(0, 0, 0));
                fieldCentric = true;
            }
            lastY = gamepad1.y;

            // Intake/transfer, left trigger pulls balls in, left bumper spits them out
            if (gamepad1.left_trigger > 0.1) {
                inTake.setPower(-1.0);
                transfer.setPower(-1.0);
            } else if (gamepad1.left_bumper) {
                inTake.setPower(1.0);
                transfer.setPower(1.0);
            } else {
                inTake.setPower(0.0);
                transfer.setPower(0.0);
            }

            // Shooter runs only while right trigger is held
            if (gamepad1.right_trigger > 0.1) {
                shooter.setPower(-1.0);
            } else {
                shooter.setPower(0.0);
            }

            // Toggle auto aim on B
            if (gamepad1.b && !lastB) {
                autoAim = !autoAim;
            }
            lastB = gamepad1.b;

            // Look for an AprilTag
            boolean seesTag = false;
            double tagAngle = 0;
            String tagName = "none";
            if (visionPortal != null) {
                visionFunctions.updateCameraControls(visionPortal);
                for (AprilTagDetection detection : aprilTagProcessor.getDetections()) {
                    if (detection.ftcPose == null) continue;
                    seesTag = true;
                    tagAngle = -detection.ftcPose.bearing;
                    if (detection instanceof AprilTagSingleDetection) {
                        tagName = "tag " + ((AprilTagSingleDetection) detection).id;
                    } else {
                        tagName = "cluster";
                    }
                    break;
                }
            }

            // Rotate lazy susan, dpad always wins over auto aim
            double lazySusanPower = 0.0;
            if (gamepad1.dpad_left) {
                lazySusanPower = 1.0;
            } else if (gamepad1.dpad_right) {
                lazySusanPower = -1.0;
            } else if (autoAim && seesTag && Math.abs(tagAngle) > MainVariableConfigurations.AIM_DEADBAND_DEG) {
                // Turn faster the further the tag is from the middle
                lazySusanPower = tagAngle * MainVariableConfigurations.AIM_KP;

                // Not so slow that it can't move, not too fast
                if (Math.abs(lazySusanPower) < MainVariableConfigurations.AIM_MIN_POWER) {
                    lazySusanPower = Math.signum(lazySusanPower) * MainVariableConfigurations.AIM_MIN_POWER;
                }
                if (Math.abs(lazySusanPower) > MainVariableConfigurations.AIM_MAX_POWER) {
                    lazySusanPower = Math.signum(lazySusanPower) * MainVariableConfigurations.AIM_MAX_POWER;
                }

                if (MainVariableConfigurations.AIM_REVERSED) {
                    lazySusanPower = -lazySusanPower;
                }
            }
            lazy_susan.setPower(lazySusanPower);

            // Drivetrain control
            if (fieldCentric) {
                DrivePowers powers = ManualDrive.fieldCentric(
                        -gamepad1.left_stick_y,
                        gamepad1.left_stick_x,
                        gamepad1.right_stick_x,
                        follower.pose().heading()
                );
                follower.manual(powers);
            } else {
                follower.manual(
                        -gamepad1.left_stick_y,
                        gamepad1.left_stick_x,
                        gamepad1.right_stick_x
                );
            }

            follower.update();
            Pose robotPose = follower.pose();

            telemetry.addData("Field Centric Enabled?", fieldCentric);
            telemetry.addData("Robot X", robotPose.x());
            telemetry.addData("Robot Y", robotPose.y());
            telemetry.addData("Robot Heading", Math.toDegrees(robotPose.heading()));
            telemetry.addData("Lazy Susan Encoder", lazy_susan.getCurrentPosition());
            telemetry.addData("Auto Aim (B)", autoAim);
            telemetry.addData("Target", tagName);
            telemetry.addData("Tag Angle", tagAngle);
            telemetry.addData("Lazy Susan Power", lazySusanPower);
            telemetry.update();
        }

        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}
