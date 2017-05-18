package org.usfirst.frc.team1317.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team1317.robot.components.*;
import org.usfirst.frc.team1317.robot.*;

public class VisionTrackingAutonomousCenter extends CommandGroup {
	
	public VisionTrackingAutonomousCenter(Robot robot, MecanumDriveTrain drivetrain, GearMechanism gearMechanism)
	{
		addSequential(new DriveForward(0.3,-0.3,drivetrain));
		addSequential(new AlignWithPeg(robot));
		addSequential(new Wait(0.5));
		addSequential(new DeployGear(drivetrain,gearMechanism));
		addSequential(new StopMotors(drivetrain));
		this.setInterruptible(true);
	}
	
	public void Stop()
	{
		end();
	}

}
