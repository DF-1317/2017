package org.usfirst.frc.team1317.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team1317.robot.components.*;
import org.usfirst.frc.team1317.robot.*;

public class VisionTrackingAutonomousCenter extends CommandGroup {
	
	public VisionTrackingAutonomousCenter(Robot robot, MecanumDriveTrain drivetrain, GearMechanism gearMechanism)
	{
		addSequential(new AlignWithPeg(robot));
		addSequential(new DeployGear(drivetrain,gearMechanism));
	}

}
