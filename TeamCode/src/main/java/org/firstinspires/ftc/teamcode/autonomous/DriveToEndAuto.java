package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Drive To End", group = "MAIN")
public class DriveToEndAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        Follower follower = Constants.create(hardwareMap);
        AutoPaths paths = new AutoPaths();

        follower.setPose(paths.start);

        telemetry.addLine("Ready, place robot at the start spot");
        telemetry.update();

        waitForStart();

        follower.setPose(paths.start);
        follower.update();
        follower.follow(paths.startToEnd());
        while (opModeIsActive()) {
            follower.update();

            Pose pose = follower.pose();
            telemetry.addData("Driving?", follower.following());
            telemetry.addData("X", pose.x());
            telemetry.addData("Y", pose.y());
            telemetry.addData("Heading", Math.toDegrees(pose.heading()));
            telemetry.update();
        }
    }
}
