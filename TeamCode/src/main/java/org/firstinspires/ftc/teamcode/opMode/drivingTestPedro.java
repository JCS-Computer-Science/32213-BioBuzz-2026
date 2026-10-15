package org.firstinspires.ftc.teamcode.opMode;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name = "Pedro TeleOp", group = "Test")
public class drivingTestPedro extends OpMode {
    private boolean fieldCentric = true;
    private Follower follower;
    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
    }
    @Override
    public void loop() {
    if (gamepad1.a) {
        fieldCentric = !fieldCentric;
    }
    if (gamepad1.y) {
        fieldCentric = !fieldCentric;
        follower.setPose(new Pose(0, 0, 0));
    }

    if (fieldCentric)
    {
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading()
        );
        follower.manual(powers);
    }
    else {
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
    }
}