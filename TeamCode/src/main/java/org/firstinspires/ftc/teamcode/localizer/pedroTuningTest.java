package org.firstinspires.ftc.teamcode.localizer;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import com.pedropathing.follower.Follower;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Pedro Tuning Test", group = "Test")
public class pedroTuningTest extends OpMode {

    private Follower follower;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
    }
    @Override
    public void start() {
    }
    @Override
    public void loop() {
    }
}
