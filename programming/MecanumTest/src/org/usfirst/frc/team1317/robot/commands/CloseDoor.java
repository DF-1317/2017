package org.usfirst.frc.team1317.robot.commands;

import org.usfirst.frc.team1317.robot.components.GearMechanism;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class CloseDoor extends Command {

	final double TimeToMove = 0.2;
	GearMechanism gearMechanism;
	Timer timer = new Timer();
	boolean DoorStartedtoClose=false;
	CloseDoor(GearMechanism gearmechanism)
	{
		gearMechanism = gearmechanism;
	}
	
	@Override
	protected void initialize()
	{
		timer.reset();
		timer.start();
	}
	
	@Override
	protected void execute()
	{
		DoorStartedtoClose=gearMechanism.tryCloseDoor();
	}
	
	@Override
	protected boolean isFinished() {
		return DoorStartedtoClose;
	}
}
