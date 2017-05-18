package org.usfirst.frc.team1317.robot.commands;

import org.usfirst.frc.team1317.robot.components.MecanumDriveTrain;

import edu.wpi.first.wpilibj.command.Command;

public class StopMotors extends Command {

	MecanumDriveTrain driveTrain;
	StopMotors(MecanumDriveTrain driveTrain)
	{
		super("StopMotor");
		this.driveTrain = driveTrain;
	}
	
	@Override
	protected void execute() {
		driveTrain.drive(0.0, 0.0, 0.0);
	}
	
	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}

}
