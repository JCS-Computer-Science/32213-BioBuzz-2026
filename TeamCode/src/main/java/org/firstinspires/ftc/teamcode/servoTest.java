package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp
public class servoTest extends OpMode {
//    TestBench1 bench = new TestBench1();
    private CRServo servo;

    @Override
    public void init() {
//        bench.init(hardwareMap);
        servo = hardwareMap.get(CRServo.class, "servo");
    }

    @Override
    public void loop() {
//        bench.setMotorSpeed(0.5);

        // Runs the continuous servo at full speed
        servo.setPower(1.0);
    }
}
