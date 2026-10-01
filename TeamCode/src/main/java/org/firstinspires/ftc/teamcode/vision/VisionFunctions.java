package org.firstinspires.ftc.teamcode.vision;

import static android.os.SystemClock.sleep;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import java.util.concurrent.TimeUnit;

// Import the configuration class layout explicitly
import org.firstinspires.ftc.teamcode.MainVariableConfigurations;

public class VisionFunctions {

    private final ElapsedTime controlThrottleTimer = new ElapsedTime();
    private int lastAppliedExposure = -1;
    private int lastAppliedGain = -1;

    public void updateCameraControls(VisionPortal portal) {
        if (portal.getCameraState() != VisionPortal.CameraState.STREAMING) return;

        if (controlThrottleTimer.milliseconds() < 500) return;
        controlThrottleTimer.reset();

        ExposureControl exposureControl = portal.getCameraControl(ExposureControl.class);
        GainControl gainControl = portal.getCameraControl(GainControl.class);

        if (exposureControl != null && gainControl != null) {
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                try {
                    exposureControl.setAePriority(false);
                } catch (Exception e) {}
                exposureControl.setMode(ExposureControl.Mode.Manual);
            }

            // EXPLICIT REFERENCE: Forces live lookup of dashboard changes
            int targetExposure = MainVariableConfigurations.EXPOSURE_MS;
            int targetGain = MainVariableConfigurations.GAIN;

            if (targetExposure != lastAppliedExposure || targetGain != lastAppliedGain) {

                long minExp = exposureControl.getMinExposure(TimeUnit.MILLISECONDS);
                long maxExp = exposureControl.getMaxExposure(TimeUnit.MILLISECONDS);
                int minGain = gainControl.getMinGain();
                int maxGain = gainControl.getMaxGain();

                long clampedExp = Math.max(minExp, Math.min(maxExp, targetExposure));
                int clampedGain = Math.max(minGain, Math.min(maxGain, targetGain));

                FtcDashboard.getInstance().stopCameraStream();
                sleep(30);

                boolean expSuccess = exposureControl.setExposure(clampedExp, TimeUnit.MILLISECONDS);
                boolean gainSuccess = gainControl.setGain(clampedGain);
                sleep(30);

                FtcDashboard.getInstance().startCameraStream(portal, 0);

                if (expSuccess) lastAppliedExposure = targetExposure;
                if (gainSuccess) lastAppliedGain = targetGain;
            }
        }
    }
}
