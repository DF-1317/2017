package org.usfirst.frc.team1317.robot;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.usfirst.frc.team1317.robot.components.*;
import java.lang.Math;
import com.kauailabs.navx.frc.AHRS;


public class PIDTurning implements PIDOutput {

	MecanumDriveTrain DriveTrain;
	AHRS gyroSensor;
	PIDController turnController;
	static final double kP = 0.01;
	static final double kI = 0.00;
	static final double kD = 0.00;
	static final double kToleranceDegrees = 5.0;
	
	double TurnRate;
	double OriginalDegrees;
	double endingDegrees;
	
	boolean firstTime =true;
	
	public PIDTurning(MecanumDriveTrain drive, AHRS gyro)
	{
		DriveTrain = drive;
		gyroSensor = gyro;
		turnController = new PIDController(kP,kI,kD,0.0,gyro,this);
		turnController.setInputRange(-180.0F, 180.0F);
		turnController.setOutputRange(-1.0, 1.0);
		turnController.setAbsoluteTolerance(kToleranceDegrees);
		turnController.setContinuous(true);
		TurnRate = 0.0;
		LiveWindow.addActuator("Drive System", "Rotate Contoller", turnController);
	}
	
	public Boolean TurnToDegrees(double degrees, double speed)
	{
		turnController.setSetpoint(degrees);
		turnController.enable();
		if (turnController.onTarget())
		{
			turnController.disable();
			DriveTrain.drive(0, 0, 0);
			return true;
		}
		else
		{
			DriveTrain.drive(0, 0, speed*TurnRate);
			return false;
		}
	}
	
	public Boolean TurnDegrees(double degrees, double speed)
	{
		if(firstTime == true)
		{
			OriginalDegrees = gyroSensor.getYaw();
			endingDegrees = OriginalDegrees + degrees;
			firstTime =false;
			
		}
		turnController.setSetpoint(endingDegrees);
		turnController.enable();
		if (turnController.onTarget())
		{
			turnController.disable();
			return true;
		}
		else
		{
			DriveTrain.drive(0, 0, speed*TurnRate);
			return false;
		}
	}
	
	@Override
	public void pidWrite(double output) {
		TurnRate = output;
	}
	
	public void reset()
	{
		firstTime = true;
	}

	public void stop() {
		DriveTrain.drive(0.0, 0.0, 0.0);
	}

}
