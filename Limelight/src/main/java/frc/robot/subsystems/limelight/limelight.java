package frc.robot.subsystems.limelight;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix6.hardware.Pigeon2;


public class Limelight extends SubsystemBase {

    Pigeon2 pigeon = new Pigeon2(1);



  public Limelight() {
    // Initialization code here
    final int[] validIDs = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};

    LimelightHelpers.SetFiducialIDFiltersOverride("limelight-jeff", validIDs);

    double currentYaw = pigeon.getYaw().getValue().getDegrees();
    LimelightHelpers.setRobotOrientation("limelight-jeff", yawDegrees, 0.0, 0.0, 0.0, 0.0, 0.0);

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    }
}