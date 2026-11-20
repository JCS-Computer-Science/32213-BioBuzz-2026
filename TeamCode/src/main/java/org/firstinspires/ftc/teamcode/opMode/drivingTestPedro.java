package org.firstinspires.ftc.teamcode.opMode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name = "Pedro TeleOp", group = "MAIN")
public class drivingTestPedro extends LinearOpMode {
    private boolean fieldCentric = true;
    private boolean lastA = false;
    private boolean lastY = false;

    private Follower follower;
    private DcMotor inTake;
    private DcMotor transfer;
    private DcMotor lazy_susan;
    private DcMotor shooter;

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        follower = Constants.create(hardwareMap);
        inTake = hardwareMap.get(DcMotor.class, "intake");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        lazy_susan = hardwareMap.get(DcMotor.class, "lazy_susan");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

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
                shooter.setPower(1.0);
            } else {
                shooter.setPower(0.0);
            }

            // Rotate lazy susan
            if (gamepad1.dpad_left) {
                lazy_susan.setPower(1.0);
            } else if (gamepad1.dpad_right) {
                lazy_susan.setPower(-1.0);
            } else {
                lazy_susan.setPower(0.0);
            }

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
            telemetry.update();
        }
    }
}
