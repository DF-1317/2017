package org.usfirst.frc.team1317.robot.components;
import edu.wpi.first.wpilibj.*;
/**
 * Represents the Climber on the robot
 * 
 *
 */
public class Climber implements RobotComponent {
	
	//Motor port(s) for the climbing mechanism
	final int CMotorPort = 5;
	
	//variable(s) to hold motor controller objects.
	Jaguar CMotor;
	
	Joystick OtherJoystick;
	
	
	public Climber(Joystick j)
	{
		//This method should initailize everything (similar to RobotInit())
		CMotor = new Jaguar (CMotorPort);
		OtherJoystick=j;
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
		double forward = OtherJoystick.getY();
		CMotor.set(forward);

	}

	@Override
	public void TestUpdate() {
		// TODO Auto-generated method stub

	}

}
