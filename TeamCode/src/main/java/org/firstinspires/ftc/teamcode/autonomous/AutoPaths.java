package org.firstinspires.ftc.teamcode.autonomous;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
public class AutoPaths {

    private final PoseFactory poseFactory = PoseFactory.degrees();

    public final Pose start = poseFactory.of(56, 8, 90);
    public final Pose end = poseFactory.of(136.98, 27.4034, 90);
    public Path startToEnd() {
        return line(start, end).constant(end);
    }
}
