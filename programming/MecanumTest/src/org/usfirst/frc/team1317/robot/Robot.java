package org.usfirst.frc.team1317.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;
import org.usfirst.frc.team1317.robot.components.*;

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
	
	//what ports the joysticks are in
	final int TurnJoystickPort = 1;
	final int MoveJoystickPort = 2;
	final int OtherJoystickPort = 3;
	
	//variables to hold the joystick objects.
	Joystick TurnJoystick;
	Joystick MoveJoystick;
	Joystick OtherJoystick;
	
	MecanumDriveTrain driveTrain;
	GearMechanism gearMechanism;
	Climber climber;
	
		

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
		
		
		//initializes the joystick objects
		TurnJoystick = new Joystick(TurnJoystickPort);
		MoveJoystick = new Joystick(MoveJoystickPort);
		OtherJoystick = new Joystick(OtherJoystickPort);
		//initializes drivetrain, telling it what joystick to use
		driveTrain = new MecanumDriveTrain(MoveJoystick, TurnJoystick);
		//initializes climber with what joystick to control
		climber = new Climber(OtherJoystick);
		//initializes the gear Mechanism, with what joystick to use.
		gearMechanism = new GearMechanism(OtherJoystick);
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
		driveTrain.AutoStart();
		gearMechanism.AutoStart();
		climber.AutoStart();
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
		driveTrain.AutoUpdate();
		gearMechanism.AutoUpdate();
		climber.AutoUpdate();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		//update the drivetrain values with joystick
		//On the far right Joystick, button 3 switches to mode 0, button 4 switches to mode 1, and button 5 switches to mode 2.
		//On the far right Joystick, button 2 toggles between throttle mode and full speed mode
		//On the far right Joystick, the throttle controls the speed.
		//In Mode 0, joystick 2 controls movement direction and joystick 1 controls turning
		//In Mode 1, joystick 2 controls movement direction and twisting the joystick controls turning
		//In Mode 2, joystick 1 controls the left wheels of the robot and joystick 2 controls the right wheels
		driveTrain.TeleopUpdate(); //uses the joysticks to control the drivetrain
		//all of the controls for the pneumatics are on the far left joystick.
		//The trigger turns toggles the gear pusher.
		//Button 2 toggles the door
		gearMechanism.TeleopUpdate();//uses the joysticks to control the gear mechanism. 
		//Moving joystick 0 forward and back controls the climber motor.
		climber.TeleopUpdate(); //uses the joysticks to control the climber.
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		driveTrain.TestUpdate();
		climber.TestUpdate();
		gearMechanism.TestUpdate();
	}
}

