package org.usfirst.frc.team1317.robot.components;

import edu.wpi.first.wpilibj.*;
import org.usfirst.frc.team1317.robot.*;
import com.ctre.*;

public class MecanumDriveTrain implements RobotComponent {

	
	//variables to hold motor controller objects.
	CANTalon FLMotor;
	CANTalon FRMotor;
	CANTalon BLMotor;
	CANTalon BRMotor;
	
	//variable to hold what drive mode we are in
	//0 is mecanum with two joysticks. One for turning and one for moving.
	//1 is mecanum with one joystick. Twisting the joystick turns the robot.
	//2 is tank drive.
	byte driveMode;
	Boolean throttleOn;
		
	//variable to hold what buttons are just recently pushed.
	Boolean oldButton2State;
	Boolean oldButton11State;
	
	//object to control the drivetrain.
	RobotDrive Drivetrain;

	//the joysticks that control the motor
	Joystick TurnJoystick;
	Joystick MoveJoystick;
	
	//used for throttle control
	double speedMultiplier;
	double oldSpeedMultiplier;
	
	Boolean throttleLock;
	
	public MecanumDriveTrain(Joystick move, Joystick turn)
	{
		//Initializes motor objects
		FLMotor = new CANTalon (RobotPorts.FLMotorPort);
		FRMotor = new CANTalon (RobotPorts.FRMotorPort);
		BLMotor = new CANTalon (RobotPorts.BLMotorPort);
		BRMotor = new CANTalon (RobotPorts.BRMotorPort);
		FRMotor.setInverted(true);
		BRMotor.setInverted(true);
		Drivetrain = new RobotDrive(FLMotor,BLMotor,FRMotor,BRMotor);
		//sets the drive mode to the default
		driveMode=0;
		MoveJoystick=move;
		TurnJoystick=turn;
		//by default the throttle is on
		throttleOn= true;
		//by default throttle lock is off
		throttleLock = false;
		//none of the buttons were originally pressed.
		oldButton2State = false;
		oldButton11State = false;
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
		//stores whether the buttons are currently pressed or not
		Boolean currentMode0ButtonState = MoveJoystick.getRawButton(3);
		Boolean currentMode1ButtonState = MoveJoystick.getRawButton(4);
		Boolean currentMode2ButtonState = MoveJoystick.getRawButton(5);
		Boolean currentButton2State = MoveJoystick.getRawButton(2);
		Boolean currentButton11State = MoveJoystick.getRawButton(11);
		//If the button to turn on mode 0 is pressed, but wasn't before (i.e. just pressed)
		if (currentButton2State == true && oldButton2State ==false)
		{
			throttleOn = !throttleOn;
			//the below user notifies the user what mode he is in.
			if (throttleOn)
			{
				System.out.println("Throttle control is on.");
				if (throttleLock)
				{
					speedMultiplier = oldSpeedMultiplier;
				}
			}
			else
			{
				System.out.println("Throttle control is off.");
				oldSpeedMultiplier = speedMultiplier; 
			}
		}
		
		if (currentButton11State == true && oldButton11State == false)
		{
			throttleLock = !throttleLock;
			if (throttleLock == true)
			{
				System.out.println("Throttle Lock is On");
			}
			else
			{
				System.out.println("Throttle Lock is Off");
			}
		}
		if(throttleOn && !throttleLock)
		{
			//scales the throttle so that it ranges from .1 to 1.0 instead of -1.0 to 1.0
			double throttle = -MoveJoystick.getThrottle();
			speedMultiplier = 0.45*throttle+0.55;
		}
		else if (!throttleOn)
			//if there is no throttle, we can go at full speed.
			speedMultiplier = 1.0;
				
		if (currentMode0ButtonState == true)
		{
			//switch to mode 0 (two joystick mecanum)
			driveMode = 0;
			System.out.println("Switched to two Joystick Mecanum Mode");
		}
		//If the button to turn on mode 1 is pressed
		else if (currentMode1ButtonState == true)
		{
			//switch to mode 1 (one joystick mecanum) and notify the user
			driveMode = 1;
			System.out.println("Switched to one Joystick Mecanum");
		}
		//If the button to turn on mode 2 is pressed
		else if (currentMode2ButtonState == true)
		{
			//switch mode 2 (tank drive) and notify the user
			driveMode = 2;
			System.out.println("Switched to two Joystick Tank Drive");
		}
		if(driveMode == 0) {
			//use one joystick to move the robot forward and sideways. Move the other joystick sideways to turn
			Drivetrain.mecanumDrive_Cartesian(speedMultiplier*MoveJoystick.getX(), speedMultiplier*MoveJoystick.getY(), speedMultiplier*TurnJoystick.getX(), 0);
		}
		else if (driveMode == 1){
			//use one joystick to move the robot. Forward, backward, and side to side move the robot in that direction
			//twisting the joystick makes the robot twist
			Drivetrain.mecanumDrive_Cartesian(speedMultiplier*MoveJoystick.getX(), speedMultiplier*MoveJoystick.getY(), speedMultiplier*MoveJoystick.getTwist(), 0);
		}
		else {
			//drive the robot with tank drive. Use one joystick to control the left motor and one to control the right motor.
			Drivetrain.tankDrive(speedMultiplier*MoveJoystick.getY(), speedMultiplier*TurnJoystick.getY());
		}
		oldButton2State = currentButton2State;
		oldButton11State = currentButton11State;
	}

	@Override
	public void TestUpdate() {
		// TODO Auto-generated method stub

	}

}
