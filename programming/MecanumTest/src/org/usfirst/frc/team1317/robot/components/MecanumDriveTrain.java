package org.usfirst.frc.team1317.robot.components;

import edu.wpi.first.wpilibj.*;

public class MecanumDriveTrain implements RobotComponent {

	//constants for what ports the motors are in
	final int FLMotorPort = 3;
	final int FRMotorPort = 4;
	final int BLMotorPort = 1;
	final int BRMotorPort = 2;
	
	//variables to hold motor controller objects.
	Jaguar FLMotor;
	Jaguar FRMotor;
	Jaguar BLMotor;
	Jaguar BRMotor;
	
	//variable to hold what drive mode we are in
	//0 is mecanum with two joysticks. One for turning and one for moving.
	//1 is mecanum with one joystick. Twisting the joystick turns the robot.
	//2 is tank drive.
	byte driveMode;
	Boolean throttleOn;
		
	//variable to hold what buttons are just recently pushed.
	Boolean oldMode0ButtonState;
	Boolean oldMode1ButtonState;
	Boolean oldMode2ButtonState;
		
	Boolean oldButton2State;
	
	//object to control the drivetrain.
	RobotDrive Drivetrain;

	Joystick TurnJoystick;
	Joystick MoveJoystick;
	
	public MecanumDriveTrain(Joystick move, Joystick turn)
	{
		//Initializes motor objects
		FLMotor = new Jaguar (FLMotorPort);
		FRMotor = new Jaguar (FRMotorPort);
		BLMotor = new Jaguar (BLMotorPort);
		BRMotor = new Jaguar (BRMotorPort);
		FRMotor.setInverted(true);
		BRMotor.setInverted(true);
		Drivetrain = new RobotDrive(FLMotor,BLMotor,FRMotor,BRMotor);
		//sets the drive mode to the default
		driveMode=0;
		MoveJoystick=move;
		TurnJoystick=turn;
		//originally all the buttons are not pressed
		oldMode0ButtonState=false;
		oldMode1ButtonState=false;
		oldMode2ButtonState=false;
		throttleOn= true;
		oldButton2State = false;
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
		//If the button to turn on mode 0 is pressed, but wasn't before (i.e. just pressed)
		if (currentButton2State == true && oldButton2State ==false)
		{
			throttleOn = !throttleOn; 
		}
						
		double speedMultiplier;
						
		if(throttleOn)
			{
				double throttle = -MoveJoystick.getThrottle();
				speedMultiplier = 0.45*throttle+0.55;
			}
			else
				speedMultiplier = 1.0;
				
		if (currentMode0ButtonState == true)
		{
			//switch to mode 0 (two joystick mecanum)
			driveMode = 0;
			System.out.println("Switched to two Joystick Mecanum Mode");
		}
		//If the button to turn on mode 1 is pressed, but wasn't before
		else if (currentMode1ButtonState == true)
		{
			//switch to mode 1 (one joystick mecanum)
			driveMode = 1;
			System.out.println("Switched to one Joystick Mecanum");
		}
		//If the button to turn on mode 1 is pressed, but wasn't before
		else if (currentMode2ButtonState == true)
		{
			//switch mode 2 
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
		oldMode0ButtonState = currentMode0ButtonState;
		oldMode1ButtonState = currentMode1ButtonState;
		oldMode2ButtonState = currentMode1ButtonState;
		oldButton2State = currentButton2State;
		
	}

	@Override
	public void TestUpdate() {
		// TODO Auto-generated method stub

	}

}
