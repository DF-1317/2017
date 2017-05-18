package org.usfirst.frc.team1317.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team1317.robot.*;

public class AlignWithPeg extends Command {

	Robot Targeter;
	boolean isAligned = false;
	AlignWithPeg(Robot robot)
	{
		super("AlignWithPeg");
		Targeter = robot;
	}
	
	@Override
	protected void initialize()
	{
		isAligned = false;
	}
	
	@Override
	protected void execute()
	{
		isAligned = Targeter.alignWithPeg();
	}
	@Override
	protected boolean isFinished() {
		return isAligned;
	}

}
