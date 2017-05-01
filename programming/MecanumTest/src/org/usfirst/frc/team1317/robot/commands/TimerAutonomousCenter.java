package org.usfirst.frc.team1317.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team1317.robot.components.*;

public class TimerAutonomousCenter extends CommandGroup {

	public TimerAutonomousCenter(MecanumDriveTrain drivetrain, GearMechanism gearMechanism)
	{
		addSequential(new DriveForward(2.49,-0.3,drivetrain));
		addSequential(new Wait(0.5));
		addSequential(new DeployGear(drivetrain,gearMechanism));
	}
	
}
