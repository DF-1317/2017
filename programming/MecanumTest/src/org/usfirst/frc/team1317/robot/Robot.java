package org.usfirst.frc.team1317.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

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
	
	//what ports the joysticks are in
	final int TurnJoystickPort = 1;
	final int MoveJoystickPort = 2;
	
	//variables to hold the joystick objects.
	Joystick TurnJoystick;
	Joystick MoveJoystick;
	
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
	

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//some default code for autonomous selection
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
		//Initializes motor objects
		FLMotor = new Jaguar (FLMotorPort);
		FRMotor = new Jaguar (FRMotorPort);
		BLMotor = new Jaguar (BLMotorPort);
		BRMotor = new Jaguar (BRMotorPort);
		FRMotor.setInverted(true);
		BRMotor.setInverted(true);
		//initializes the joystick object
		TurnJoystick = new Joystick(TurnJoystickPort);
		MoveJoystick = new Joystick(MoveJoystickPort);
		//initializes the drivetrain with motors
		Drivetrain = new RobotDrive(FLMotor,BLMotor,FRMotor,BRMotor);
		//sets the drive mode to the default
		driveMode=0;
		//originally all the buttons are not pressed
		oldMode0ButtonState=false;
		oldMode1ButtonState=false;
		oldMode2ButtonState=false;
		throttleOn= true;
		oldButton2State = false;
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() { //all of the code in this function was automatically added
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() { //all the code in this was automatically added
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
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
			speedMultiplier = 1;
		
		if (currentMode0ButtonState == true)
			//switch to mode 0 (two joystick mecanum)
			driveMode = 0;
		//If the button to turn on mode 1 is pressed, but wasn't before
		else if (currentMode1ButtonState == true)
			//switch to mode 1 (one joystick mecanum)
			driveMode = 1;
		//If the button to turn on mode 1 is pressed, but wasn't before
		else if (currentMode2ButtonState == true)
			//switch mode 2 
			driveMode = 2;
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

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

