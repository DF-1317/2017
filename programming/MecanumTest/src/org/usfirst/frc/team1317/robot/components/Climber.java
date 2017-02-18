package org.usfirst.frc.team1317.robot.components;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team1317.robot.*;
import com.ctre.*;
/**
 * This code represents the Climber on the robot
 * This code will most likely be changed such that buttons will be used instead of a joystick
 *
 */
public class Climber implements RobotComponent {
	
	//variable(s) to hold motor controller objects and determine what joystick will be used.
	CANTalon CMotor;
	
	Joystick OtherJoystick;
	
	public Climber(Joystick j)
	{
		//This method should initialize everything (similar to RobotInit())
		CMotor = new CANTalon (RobotPorts.CMotorPort);
		OtherJoystick=j;
		SmartDashboard.putNumber("Climber Voltage", CMotor.getOutputVoltage());
		SmartDashboard.putNumber("Climber Current", CMotor.getOutputCurrent());
	}

	//This method is called at the start of Autonomous
	@Override
	public void AutoStart() {
		// TODO Auto-generated method stub

	}

	//this method is called every 20 milliseconds during Autonomous
	@Override
	public void AutoUpdate() {
		// TODO Auto-generated method stub

	}

	//This method is called every 20 milliseconds during Teleop
	@Override
	public void TeleopUpdate() {
		// TODO Auto-generated method stub
		// controls the motor using the 'OtherJoystick'
		double throttle = -OtherJoystick.getThrottle();
		double speedMultiplier = 0.45*throttle+0.55;
		double forward = speedMultiplier * OtherJoystick.getY();
		CMotor.set(forward);
		if (CMotor.getOutputCurrent()>40)
		{
			CMotor.set(0);
		}
		SmartDashboard.putNumber("Climber Voltage", CMotor.getOutputVoltage());
		SmartDashboard.putNumber("Climber Current", CMotor.getOutputCurrent());

	}

	@Override
	public void TestUpdate() {
		// TODO Auto-generated method stub

	}

}
