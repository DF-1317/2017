package org.usfirst.frc.team1317.robot.commands;

import org.usfirst.frc.team1317.robot.components.GearMechanism;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class ExtendPiston extends Command {

	final double TimeToMove = 0.2;
	GearMechanism gearMechanism;
	Timer timer = new Timer();
	boolean StartedtoPush=false;
	ExtendPiston(GearMechanism gearmechanism)
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
		StartedtoPush = gearMechanism.trypushGear();	
	}
	
	@Override
	protected boolean isFinished() {
		return StartedtoPush;
	}

}
