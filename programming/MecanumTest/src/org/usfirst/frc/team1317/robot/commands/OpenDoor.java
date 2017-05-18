package org.usfirst.frc.team1317.robot.commands;

import org.usfirst.frc.team1317.robot.components.GearMechanism;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class OpenDoor extends Command {

	final double TimeToMove = 0.2;
	GearMechanism gearMechanism;
	Timer timer = new Timer();
	OpenDoor(GearMechanism gearmechanism)
	{
		super("OpenDoor");
		gearMechanism = gearmechanism;
		setInterruptible(true);
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
		gearMechanism.openDoor();
	}
	
	@Override
	protected boolean isFinished() {
		if(timer.get()>TimeToMove)
		{
			return true;
		}
		return false;
	}

}
