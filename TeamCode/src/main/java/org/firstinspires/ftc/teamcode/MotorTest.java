package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;



@TeleOp
public class MotorTest extends OpMode {
    TestBench1 bench = new TestBench1();
    private Servo servo;

    @Override
    public void init()
    {
        bench.init(hardwareMap);
        servo = hardwareMap.get(Servo.class, "servo");
    }

    @Override
    public void loop()
    {
        bench.setMotorSpeed(0.5);
//        telemetry.addData(servo.Direction);
        servo.setPosition(0.5);
//        linearOpMode.sleep(1000);
//        servo.setPosition(0.25);
    }
}
