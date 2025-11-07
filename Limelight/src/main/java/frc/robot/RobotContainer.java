// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.subsystems.limelight.LimelightLocalization;

public class RobotContainer {

  private final Pigeon2 pigeon = new Pigeon2(0); // TODO: set can ID

  public final LimelightLocalization llLoc = 
    new LimelightLocalization("limelight", pigeon);

  public RobotContainer() {
  configureBindings();
  }

  private void configureBindings() {}

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }

  public Pose2d getCurrentFieldPose() {
    return llLoc.getEstimatedPose();
  }
}
