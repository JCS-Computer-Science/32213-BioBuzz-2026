package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class TestBench1 {

    private DcMotor motor;

    public void init(HardwareMap hwMap)
    {
        motor = hwMap.get(DcMotor.class, "motor");
    }

    public void setMotorSpeed(double speed) {
        motor.setPower(speed);
    }
}
