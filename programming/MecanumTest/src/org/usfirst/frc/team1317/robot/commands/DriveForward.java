package org.usfirst.frc.team1317.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team1317.robot.components.*;

public class DriveForward extends Command {

	double TargetTime =0;
	double Speed = 0;
	Timer timer = new Timer();
	MecanumDriveTrain driving;
	DriveForward(double time, double speed, MecanumDriveTrain driveTrain)
	{
		TargetTime = time;
		Speed = speed;
		driving = driveTrain;
	}
	
	// Called just before this Command runs the first time
	@Override
	protected void initialize() {
		timer.reset();
		timer.start();
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		driving.drive(0.0, Speed, 0.0);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		if(timer.get()>=TargetTime)
			return true;
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
		driving.drive(0.0, 0.0, 0.0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
		driving.drive(0.0, 0.0, 0.0);
	}
}
