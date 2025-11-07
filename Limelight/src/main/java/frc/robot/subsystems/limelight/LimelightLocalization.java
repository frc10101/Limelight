package frc.robot.subsystems.limelight;

import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.Optional;

/** SubsystemBase: MegaTag2 pose with Pigeon2 yaw override (ALWAYS blue-origin). */
public class LimelightLocalization extends SubsystemBase {
  private final NetworkTable table;
  private final Pigeon2 pigeon;

  // Cached NT entries
  private final NetworkTableEntry tv;
  private final NetworkTableEntry botposeBlue;
  private final NetworkTableEntry tlMs;
  private final NetworkTableEntry clMs;

  private Pose2d estimatedPose = new Pose2d();
  private Optional<TimestampedPose> lastVision = Optional.empty();

  public LimelightLocalization(String limelightTableName, Pigeon2 pigeon2) {
    this.pigeon = pigeon2;

    NetworkTableInstance nt = NetworkTableInstance.getDefault();
    this.table = nt.getTable(limelightTableName);

    this.tv = table.getEntry("tv");
    this.botposeBlue = table.getEntry("botpose_wpiblue");   // ALWAYS USE BLUE ORIGIN
    this.tlMs = table.getEntry("tl");
    this.clMs = table.getEntry("cl");
  }

  @Override
  public void periodic() {
    final double now = Timer.getFPGATimestamp();
    final boolean hasTargets = tv.getDouble(0.0) > 0.5;
    final double[] poseArr = botposeBlue.getDoubleArray(new double[0]); // ONLY BLUE

    if (hasTargets && poseArr.length >= 6) {
      final double x = poseArr[0];
      final double y = poseArr[1];
      final double yawDeg = pigeon.getYaw().getValueAsDouble();

      Pose2d llPoseWithGyro = new Pose2d(
          new Translation2d(x, y),
          Rotation2d.fromDegrees(yawDeg)
      );

      double latencySeconds = (tlMs.getDouble(0.0) + clMs.getDouble(0.0)) / 1000.0;
      double measurementTime = now - latencySeconds;

      estimatedPose = llPoseWithGyro;
      lastVision = Optional.of(new TimestampedPose(llPoseWithGyro, measurementTime));
    } else {
      // No vision: keep XY, update heading from gyro
      final double yawDeg = pigeon.getYaw().getValueAsDouble();
      estimatedPose = new Pose2d(estimatedPose.getTranslation(), Rotation2d.fromDegrees(yawDeg));
    }
  }

  public Pose2d getEstimatedPose() { return estimatedPose; }

  public Optional<TimestampedPose> getLastVisionMeasurement() { return lastVision; }

  public void reset(Pose2d newPose) {
    this.estimatedPose = newPose;
    this.lastVision = Optional.empty();
    // If you want strict field-zero alignment:
    // pigeon.setYaw(newPose.getRotation().getDegrees());
  }

  /** Simple time-aligned pose container. */
  public static final class TimestampedPose {
    public final Pose2d pose;
    public final double timestampSec;

    public TimestampedPose(Pose2d pose, double timestampSec) {
      this.pose = pose;
      this.timestampSec = timestampSec;
    }
  }
}
