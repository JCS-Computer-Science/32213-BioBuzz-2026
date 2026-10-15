package org.firstinspires.ftc.teamcode.localizer;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Odometry Pod Test", group = "Test")
public class odometeryPodTest extends OpMode {
    private DcMotor odometryX;
    private FtcDashboard dashboard;

    @Override
    public void init() {
        dashboard = FtcDashboard.getInstance();
        odometryX = hardwareMap.get(DcMotor.class, "encoderX");
    }

    @Override
    public void loop() {
        int currentPosition = odometryX.getCurrentPosition();
        TelemetryPacket packet = new TelemetryPacket();

        packet.put("Encoder X Position", currentPosition);

        dashboard.sendTelemetryPacket(packet);
    }
}
