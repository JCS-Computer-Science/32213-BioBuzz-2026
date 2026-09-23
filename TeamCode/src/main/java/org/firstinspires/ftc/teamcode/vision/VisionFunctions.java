package org.firstinspires.ftc.teamcode.vision;

import static android.os.SystemClock.sleep;

// Import Main Variables
import static org.firstinspires.ftc.teamcode.MainVariableConfigurations.EXPOSURE_MS;
import static org.firstinspires.ftc.teamcode.MainVariableConfigurations.GAIN;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.concurrent.TimeUnit;

public class VisionFunctions {

    // Track the last applied values to avoid overwhelming the camera USB bus
    private final ElapsedTime controlThrottleTimer = new ElapsedTime();
    private int lastAppliedExposure = -1;
    private int lastAppliedGain = -1;

    public void updateCameraControls(VisionPortal portal) {
        if (portal.getCameraState() != VisionPortal.CameraState.STREAMING) return;

        // Throttle checks to 500ms so we don't violently loop stream restarts
        if (controlThrottleTimer.milliseconds() < 500) return;
        controlThrottleTimer.reset();

        ExposureControl exposureControl = portal.getCameraControl(ExposureControl.class);
        GainControl gainControl = portal.getCameraControl(GainControl.class);

        if (exposureControl != null && gainControl != null) {
            // Enforce Manual Mode if it slipped back into Auto
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                try {
                    exposureControl.setAePriority(false);
                } catch (Exception e) {}
                exposureControl.setMode(ExposureControl.Mode.Manual);
            }

            // Check if the dashboard values actually changed
            if (EXPOSURE_MS != lastAppliedExposure || GAIN != lastAppliedGain) {

                // Get boundaries to prevent camera driver crashes
                long minExp = exposureControl.getMinExposure(TimeUnit.MILLISECONDS);
                long maxExp = exposureControl.getMaxExposure(TimeUnit.MILLISECONDS);
                int minGain = gainControl.getMinGain();
                int maxGain = gainControl.getMaxGain();

                long clampedExp = Math.max(minExp, Math.min(maxExp, EXPOSURE_MS));
                int clampedGain = Math.max(minGain, Math.min(maxGain, GAIN));

                // CRITICAL STEP: Temporarily break dashboard lock to free the firmware registers
                FtcDashboard.getInstance().stopCameraStream();
                sleep(30); // Give the UVC driver a brief moment to unpack the stream pipeline

                // Push your verified values directly to the lens hardware
                boolean expSuccess = exposureControl.setExposure(clampedExp, TimeUnit.MILLISECONDS);
                boolean gainSuccess = gainControl.setGain(clampedGain);
                sleep(30);

                // Re-hook the stream so you can see your live changes instantly on your browser screen
                FtcDashboard.getInstance().startCameraStream(portal, 0);

                // Only cache if the camera successfully accepted the setting change
                if (expSuccess) lastAppliedExposure = EXPOSURE_MS;
                if (gainSuccess) lastAppliedGain = GAIN;
            }
        }
    }
}
