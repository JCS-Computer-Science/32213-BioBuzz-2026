package org.firstinspires.ftc.teamcode.opMode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "Driving Test Pinpoint Fixed", group = "Test")
public class drivingTest extends OpMode {
    private DcMotor front_left;
    private DcMotor front_right;
    private DcMotor back_left;
    private DcMotor back_right;
    private GoBildaPinpointDriver pinpoint;

    // Toggle state for field-centric vs non-heading based
    private boolean fieldCentric = false;
    private boolean lastButtonState = false;

    @Override
    public void init() {
        // Initialize Drivetrain Motors
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        back_left = hardwareMap.get(DcMotor.class, "back_left");
        back_right = hardwareMap.get(DcMotor.class, "back_right");

        // Reverse left side motors so positive power drives the robot forward
        front_left.setDirection(DcMotorSimple.Direction.REVERSE);
        back_left.setDirection(DcMotorSimple.Direction.REVERSE);

        // Initialize goBILDA Pinpoint Computer
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        telemetry.addData("Status", "Calibrating Pinpoint IMU...");
        telemetry.update();
        pinpoint.recalibrateIMU();
        pinpoint.resetPosAndIMU();
        telemetry.addData("Status", "Initialized and Calibrated!");
    }

    @Override
    public void loop() {
        // Must poll the Pinpoint coprocessor at the top of every loop cycle
        pinpoint.update();

        // Get driver inputs (Left stick Y is inverted so pushing up is positive)
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double twist = gamepad1.right_stick_x;

        // Reset heading calibration if driver drifts (bind to Options button)
        if (gamepad1.options) {
            pinpoint.resetPosAndIMU();
        }

        // Toggle field-centric mode using the A button (edge-detected to prevent rapid toggling)
        boolean currentButtonState = gamepad1.a;
        if (currentButtonState && !lastButtonState) {
            fieldCentric = !fieldCentric;
        }
        lastButtonState = currentButtonState;

        double rotX;
        double rotY;

        // Retrieve current field heading from Pinpoint in RADIANS
        double botHeading = pinpoint.getHeading(AngleUnit.RADIANS);

        if (fieldCentric) {
            // Apply 2D Vector Rotation matrix for field-centric control
            rotX = strafe * Math.cos(-botHeading) - drive * Math.sin(-botHeading);
            rotY = strafe * Math.sin(-botHeading) + drive * Math.cos(-botHeading);
        } else {
            // Robot-centric control (bypass heading rotation)
            rotX = strafe;
            rotY = drive;
        }

        // Standard Mecanum Kinematics mixing with rotated vector offsets
        double[] speeds = {
                (rotY + rotX + twist), // Front Left
                (rotY - rotX - twist), // Front Right
                (rotY - rotX + twist), // Back Left
                (rotY + rotX - twist)  // Back Right
        };

        // Normalize motor powers if any calculation climbs past 1.0 (prevents clipping)
        double max = Math.abs(speeds[0]);
        for (int i = 1; i < speeds.length; i++) {
            if (max < Math.abs(speeds[i])) {
                max = Math.abs(speeds[i]);
            }
        }

        if (max > 1.0) {
            for (int i = 0; i < speeds.length; i++) {
                speeds[i] /= max;
            }
        }

        // Send power directly to hardware channels
        front_left.setPower(speeds[0]);
        front_right.setPower(speeds[1]);
        back_left.setPower(speeds[2]);
        back_right.setPower(speeds[3]);

        // Output diagnostic telemetry to Driver Hub
        telemetry.addData("Field Centric", fieldCentric);
        telemetry.addData("Heading (Deg)", Math.toDegrees(botHeading));
        telemetry.addData("X Position", pinpoint.getPosX(DistanceUnit.INCH));
        telemetry.addData("Y Position", pinpoint.getPosY(DistanceUnit.INCH));
        telemetry.update();
    }
}
