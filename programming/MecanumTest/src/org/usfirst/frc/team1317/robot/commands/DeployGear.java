package org.usfirst.frc.team1317.robot.commands;

import org.usfirst.frc.team1317.robot.components.GearMechanism;
import org.usfirst.frc.team1317.robot.components.MecanumDriveTrain;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DeployGear extends CommandGroup {
	
	DeployGear(MecanumDriveTrain driveTrain, GearMechanism gearMechanism)
	{
		addSequential( new OpenDoor(gearMechanism));
		addSequential( new Wait(0.5));
		addSequential( new ExtendPiston(gearMechanism));
		addSequential( new Wait (1.2));
		addSequential( new DriveForward(0.9,0.2,driveTrain));
		addSequential( new RetractPiston(gearMechanism));
		addSequential( new Wait (0.5));
		addSequential( new CloseDoor(gearMechanism));
		setInterruptible(true);	
	}

}
