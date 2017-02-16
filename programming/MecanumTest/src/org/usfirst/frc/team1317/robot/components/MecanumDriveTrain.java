package org.usfirst.frc.team1317.robot.components;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.internal.HardwareTimer;

import org.usfirst.frc.team1317.robot.*;
import com.ctre.*;
import com.kauailabs.navx.frc.*;

public class MecanumDriveTrain implements RobotComponent {

	public static double turningConstant =0.03;
	public static double sidewaysConstant = 0.6;
	
	
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
	Boolean oldTurnButton11State;
	
	//object to control the drivetrain.
	RobotDrive Drivetrain;

	//the joysticks that control the motor
	Joystick TurnJoystick;
	Joystick MoveJoystick;
	
	//used for throttle control
	double speedMultiplier;
	double oldSpeedMultiplier;
	
	Boolean throttleLock;
	
	Boolean motorsReversed;
	
	//variables that can help the robot move a certain distance.
	Accelerometer accel;
	double distancetravelled;
	double velocity;
	double oldVelocity;
	double oldAcceleration;
	double lastTime;
	HardwareTimer timer;
	
	AHRS gyro;
	
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
		oldTurnButton11State = false;
		accel = new BuiltInAccelerometer();
		distancetravelled = 0;
		timer = new HardwareTimer();
		lastTime = timer.getFPGATimestamp();
		gyro = new AHRS(SerialPort.Port.kMXP);
		motorsReversed = false;
		System.out.println("NavX-MXP firmware: "+ gyro.getFirmwareVersion());
	}
	
	//This method is called at the start of Autonomous
	@Override
	public void AutoStart() {
		// TODO Auto-generated method stub

	}

	//this method is called every 20 milliseconds during Autonomous
	@Override
	public void AutoUpdate() {
		

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
		Boolean currentTurnButton11 = TurnJoystick.getRawButton(11);
		//If the button 2 is pressed, but wasn't before (i.e. just pressed)
		if (currentButton2State == true && oldButton2State ==false)
		{
			//toggle whether the throttle is on or not
			toggleThrottleControl();
		}
		
		//if button 11 is pressed but was not the last time.
		if (currentButton11State == true && oldButton11State == false)
		{
			//toggles the throttle lock
			throttleLock = !throttleLock;
			//notifies the use which mode is on
			if (throttleLock == true)
			{
				System.out.println("Throttle Lock is On");
			}
			else
			{
				System.out.println("Throttle Lock is Off");
			}
		}
		
		if(currentTurnButton11 == true && oldTurnButton11State==false)
		{
			motorsReversed = !motorsReversed;
		}
		//if the throttle is not locked and is on.
		if(throttleOn && !throttleLock)
		{
			//scales the throttle so that it ranges from .1 to 1.0 instead of -1.0 to 1.0
			double throttle = -MoveJoystick.getThrottle();
			speedMultiplier = 0.45*throttle+0.55;
		}
		else if (!throttleOn)
			//if there is no throttle, we can go at full speed.
			speedMultiplier = 1.0;
				
		//if the button to turn on mode 0 is on
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
			if(!motorsReversed) {
				Drivetrain.mecanumDrive_Cartesian(speedMultiplier*MoveJoystick.getX(), speedMultiplier*MoveJoystick.getY(), speedMultiplier*TurnJoystick.getX(), 0);
			}
			else {
				Drivetrain.mecanumDrive_Cartesian(-speedMultiplier*MoveJoystick.getX(), -speedMultiplier*MoveJoystick.getY(), -speedMultiplier*TurnJoystick.getX(), 0);
			}
				
		}
		else if (driveMode == 1){
			//use one joystick to move the robot. Forward, backward, and side to side move the robot in that direction
			//twisting the joystick makes the robot twist
			if(!motorsReversed) {
				Drivetrain.mecanumDrive_Cartesian(speedMultiplier*MoveJoystick.getX(), speedMultiplier*MoveJoystick.getY(), speedMultiplier*MoveJoystick.getTwist(), 0);
			}
			else {
				Drivetrain.mecanumDrive_Cartesian(-speedMultiplier*MoveJoystick.getX(), -speedMultiplier*MoveJoystick.getY(), -speedMultiplier*MoveJoystick.getTwist(), 0);
			}
		}
		else {
			//drive the robot with tank drive. Use one joystick to control the left motor and one to control the right motor.
			Drivetrain.tankDrive(speedMultiplier*MoveJoystick.getY(), speedMultiplier*TurnJoystick.getY());
		}
		
		//the current button states are now the old button states.
		oldButton2State = currentButton2State;
		oldButton11State = currentButton11State;
		oldTurnButton11State = currentTurnButton11;
	}

	@Override
	public void TestUpdate() {
		// TODO Auto-generated method stub

	}
	
	//I'll have to see which axis of the accelerometer is forward and which is sideways
	//This method can also be used to drive backward if a negative distance is inputed.
	//I think there are some things that need fixed. Like the first time this runs it will not use the correct time for lastTime.
	//(although the acceleration may be zero at that point which would make that effect neglible)
	//I might also want to look into using PID control.
	//This method would be called every 20 milliseconds in the autonomous code.
	//It will make the robot drive forward the distance specified(approximately)
	public boolean DriveForward(double distance, double speed, double heading) {
		//gets the current time
		double currentTime = timer.getFPGATimestamp();
		//calculate the amount of time that has passed since this function was last called.
		double changeInTime = currentTime - lastTime;
		//gets the current acceleration
		double currentAcceleration = accel.getX();
		//calculates the change in velocity by averaging the current acceleration and the old acceleration, converting the acceleration to ft/s^2, and multiplying by change in time
		double changeInVelocity = (oldAcceleration+currentAcceleration)*32.174/2 *changeInTime;
		//adds the change in velocity to the velocity
		velocity+=changeInVelocity;
		//calculates the change in position from the velocity by averaging the current and old velocity and multiplying by the change in time.
		double changeInPosition = (oldVelocity + velocity)/2 * changeInTime;
		//adds the change in position to the distance traveled
		distancetravelled += changeInPosition;
		//sets the old times to what the times are now
		oldVelocity = velocity;
		oldAcceleration = currentAcceleration;
		lastTime = currentTime;
		//if we have already traveled far enough return that we have traveled that far.
		if (distancetravelled>=distance) {
			//stop moving
			Drivetrain.mecanumDrive_Cartesian(0, 0, 0, 0);
			//tell the code that we are done.
			return true;
		}
		else
		{
			//if we have not got there yet keep driving.
			//I will need to multiply the acceleration by a variable.
			//drives forward at the correct speed and corrects for drifting sideways and turning.
			Drivetrain.mecanumDrive_Cartesian(-turningConstant*accel.getZ(), speed, -turningConstant*(gyro.getYaw()-heading), 0); //the input after speed should be a gyro sensors angle multiplied by a constant (the robot should turn in the opposite direction than it is currently driving so it drives straight)
			return false;
		}
	}
	//stops the drivetrain, resets the distance traveled, stops the motors and sets the velocity to zero.
	public void resetDistance() {
		distancetravelled = 0;
		Drivetrain.mecanumDrive_Cartesian(0, 0, 0, 0);
		oldVelocity = 0;
		oldAcceleration = 0;
		velocity = 0;
		lastTime=timer.getFPGATimestamp();
	}
	
	public Boolean turnDegrees(double degrees, double speed)
	{
		return false;
	}
	
	public Boolean turntoAngle(double degrees, double speed)
	{
		if(degrees>gyro.getYaw())
		{
			Drivetrain.mecanumDrive_Cartesian(0, 0, speed, 0);
			return false;
		}
		else if (degrees==gyro.getYaw())
		{
			return true;
		}
		else
		{
			Drivetrain.mecanumDrive_Cartesian(0, 0, -speed, 0);
			return false;
		}
	}
	
	public Boolean alignWithPeg()
	{
		return false;
	}
	
	void toggleThrottleControl()
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
	
	public void drive(double x,double y, double rotation)
	{
		Drivetrain.mecanumDrive_Cartesian(x, y, rotation, 0);
	}
	

}
