package org.firstinspires.ftc.teamcode.localizer;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "Pinpoint Test", group = "Test")
public class PinPointTest extends LinearOpMode {

    GoBildaPinpointDriver pinpoint;

    @Override
    public void runOpMode() {
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);

        pinpoint.setOffsets(0.0, 0.0, DistanceUnit.INCH);

        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        pinpoint.resetPosAndIMU();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        FtcDashboard dashboard = FtcDashboard.getInstance();

        waitForStart();

        while (opModeIsActive()) {
            pinpoint.update(); // Must be called to pull new data

            TelemetryPacket packet = new TelemetryPacket();
            Pose2D pos = pinpoint.getPosition();

            double xInches = pos.getX(DistanceUnit.INCH);
            double yInches = pos.getY(DistanceUnit.INCH);
            double headingDegrees = pos.getHeading(AngleUnit.DEGREES);

            telemetry.addData("X Position (Inches)", "%.2f", xInches);
            telemetry.addData("Y Position (Inches)", "%.2f", yInches);
            telemetry.addData("Heading (Degrees)", "%.2f", headingDegrees);
            telemetry.addData("Pinpoint Hardware Status", pinpoint.getDeviceStatus());
            telemetry.update();
            packet.fieldOverlay().drawImage("/images/arena.png", 0, 0, 144, 144);

            double robotRadius = 5.0;
            packet.fieldOverlay().setStroke("green");
            packet.fieldOverlay().strokeCircle(xInches, yInches, robotRadius);

            double arrowLength = 8.0;
            double headingRadians = Math.toRadians(headingDegrees);

            double arrowEndX = xInches + (arrowLength * Math.cos(headingRadians));
            double arrowEndY = yInches + (arrowLength * Math.sin(headingRadians));

            packet.fieldOverlay().setStroke("red");
            packet.fieldOverlay().strokeLine(xInches, yInches, arrowEndX, arrowEndY);

            dashboard.sendTelemetryPacket(packet);

            sleep(10);
        }
    }
}
