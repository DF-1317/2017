package org.usfirst.frc.team1317.robot;

import edu.wpi.first.wpilibj.hal.PortsJNI;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;
import org.usfirst.frc.team1317.robot.components.*;
import com.kauailabs.navx.frc.*;
import java.util.Map;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String crossingAuto = "Crossing Baseline";
	final String defaultAuto = "Center";
	final String leftAuto = "Left";
	final String rightAuto = "Right";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	final String LeftLoadingStation = "Left";
	final String RightLoadingStation = "Right";
	final String AutoLoadingStation = "Auto";
	String loadingStationSelected;
	String LoadingStation;
	SendableChooser<String> LoadingStationChooser = new SendableChooser<>();
	
	int AutoStep;

	// variables to hold the joystick objects.
	Joystick TurnJoystick;
	Joystick MoveJoystick;
	Joystick OtherJoystick;

	MecanumDriveTrain driveTrain;
	GearMechanism gearMechanism;
	Climber climber;

	AHRS ahrs;

	PIDDriveDistance driveForward;
	PIDTurning turner;

	Timer AutoTimer;

	PacketReader packetReader;
	Targeting targeter;
	
	DriverStation driverStation;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		// some default code for autonomous selection
		chooser.addDefault("Center", defaultAuto);
		chooser.addObject("Left", leftAuto);
		chooser.addObject("Right", rightAuto);
		chooser.addObject("Cross the Baseline", crossingAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		LoadingStationChooser.addDefault("Auto", AutoLoadingStation);
		LoadingStationChooser.addObject("Left Loading Station", LeftLoadingStation);
		LoadingStationChooser.addObject("Right Loading Station", RightLoadingStation);
		SmartDashboard.putData("Loading Station",LoadingStationChooser);

		// initializes the NavX-MXP
		ahrs = new AHRS(SerialPort.Port.kMXP);
		// initializes the joystick objects
		TurnJoystick = new Joystick(RobotPorts.TurnJoystickPort);
		MoveJoystick = new Joystick(RobotPorts.MoveJoystickPort);
		OtherJoystick = new Joystick(RobotPorts.OtherJoystickPort);
		// initializes drivetrain, telling it what joystick to use
		driveTrain = new MecanumDriveTrain(MoveJoystick, TurnJoystick, ahrs);
		// initializes climber with what joystick to control
		climber = new Climber(OtherJoystick);
		// initializes the gear Mechanism, with what joystick to use.
		// change this based on which robot we are deploying code to.
		gearMechanism = new GearMechanismDoubleSolenoid(OtherJoystick);
		AutoStep = 7;
		turner = new PIDTurning(driveTrain, ahrs);
		driveForward = new PIDDriveDistance(driveTrain, ahrs);
		SmartDashboard.putNumber("Number of Solenoids", PortsJNI.getNumSolenoidChannels());
		AutoTimer = new Timer();
		packetReader = new PacketReader();
		targeter = new Targeting(driveTrain);
		driverStation = DriverStation.getInstance();
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
	public void autonomousInit() { // all of the code in this function was
									// automatically added
		autoSelected = chooser.getSelected();
		loadingStationSelected = LoadingStationChooser.getSelected();
		if(loadingStationSelected == AutoLoadingStation)
		{
			if(driverStation.getAlliance()==DriverStation.Alliance.Blue)
			{
				LoadingStation = "Right";
			}
			else
			{
				LoadingStation = "Left";
			}
		}
		else
		{
			LoadingStation = loadingStationSelected;
		}
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		driveTrain.AutoStart();
		gearMechanism.AutoStart();
		climber.AutoStart();
		AutoTimer.reset();
		AutoTimer.start();
		turner.reset();
		AutoStep = 7;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Boolean next = false;
		if(autoSelected == crossingAuto)
		{
			if (AutoTimer.get() < 1.7) {
				driveTrain.drive(0, -0.6, 0);
			}
		}
		else
		{
			if (AutoStep == 0) {
				driveTrain.resetDistance();
				next = true;
			}
			else if (AutoStep == 1) {
				next = alignWithPeg();
			}	
			else if (AutoStep == 2) {
				gearMechanism.openDoor();
				next = true;
			}
			else if (AutoStep == 3) {
				next = gearMechanism.trypushGear();
			}
			else if (AutoStep == 4) {
				gearMechanism.retractGearPiston();
				next = true;
			}
			else if (AutoStep == 5) {
				next = gearMechanism.tryCloseDoor();
			}
			else if (AutoStep == 6) {
				AutoTimer.reset();
				AutoTimer.start();
				next = true;
			}
			else if (AutoStep == 7)
			{
				driveTrain.drive(0, 0.5, 0);
				if (AutoTimer.get()>0.2)
				{
					next = true;
				}
			}
			if (autoSelected == rightAuto && LoadingStation == "Right")
			{
				if (AutoStep ==8)
				{
					next = turner.TurnDegrees(60, 0.75);
				}
				else if (AutoStep == 9)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if (AutoStep == 10)
				{
					if(AutoTimer.get()>0.7)
					{
						next = true;
					}
					else
					{
						driveTrain.drive(0, -0.5, 0);
					}
				}
			}
			if (autoSelected == leftAuto && LoadingStation == "Left")
			{
				if (AutoStep ==8)
				{
					next = turner.TurnDegrees(-60, 0.75);
				}
				else if (AutoStep == 9)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if (AutoStep == 10)
				{
					if(AutoTimer.get()>0.7)
					{
						next = true;
					}
					else
					{
						driveTrain.drive(0, -0.5, 0);
					}
				}
			}
			else if(autoSelected == defaultAuto)
			{
				if(AutoStep == 8)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if(AutoStep == 9)
				{
					if(AutoTimer.get()>1.4)
					{
						next = true;
					}
					else
					{
						if(LoadingStation == "Right")
							driveTrain.drive(1.0, -0.02, 0);
						else
							driveTrain.drive(-1.0, 0, 0);
					}
				}
				else if(AutoStep == 10)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if (AutoStep ==11)
				{
					if(AutoTimer.get()>0.7)
					{
						next = true;
					}
					else
					{
						driveTrain.drive(0, -0.75, 0);
					}
				}
			}
			else if (autoSelected==rightAuto&&LoadingStation == "Left")
			{
				if (AutoStep ==8)
				{
					next = turner.TurnDegrees(60, 0.75);
				}
				else if (AutoStep == 9)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if (AutoStep == 10)
				{
					if(AutoTimer.get()>0.7)
					{
						next = true;
					}
					else
					{
						driveTrain.drive(0, -0.5, 0);
					}
				}
				else if (AutoStep == 11)
				{
					turner.reset();
					next = true;
				}
				else if (AutoStep == 12)
				{
					next = turner.TurnDegrees(-90, 0.5);
				}
				else if (AutoStep == 13)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next =true;
				}
				else if (AutoStep == 14)
				{
					if(AutoTimer.get()>0.7)
					{
						next = true;
					}
					else
					{
						driveTrain.drive(0, -0.5, 0);
					}
				}
			}
			else if (autoSelected==leftAuto&&LoadingStation == "Right")
			{
				if (AutoStep ==8)
				{
					next = turner.TurnDegrees(-60, 0.75);
				}
				else if (AutoStep == 9)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if (AutoStep == 10)
				{
					if(AutoTimer.get()>0.7)
					{
						next = true;
					}
					else
					{
						driveTrain.drive(0, 0.5, 0);
					}
				}
				else if (AutoStep == 11)
				{
					turner.reset();
					next = true;
				}
				else if (AutoStep == 12)
				{
					next = turner.TurnDegrees(90, 0.5);
				}
				else if (AutoStep == 13)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next =true;
				}
				else if (AutoStep == 14)
				{
					if(AutoTimer.get()>0.7)
					{
						next = true;
					}
					else
					{
						driveTrain.drive(0, 0.5, 0);
					}
				}
			}
			if (next)
				AutoStep++;
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
		// update the drivetrain values with joystick
		// On the far right Joystick, button 3 switches to mode 0, button 4
		// switches to mode 1, and button 5 switches to mode 2.
		// On the far right Joystick, button 2 toggles between throttle mode and
		// full speed mode
		// On the far right Joystick, the throttle controls the speed.
		// On the far right Joystick, button 11 toggles throttle lock.
		// In Mode 0, joystick 2 controls movement direction and joystick 1
		// controls turning
		// In Mode 1, joystick 2 controls movement direction and twisting the
		// joystick controls turning
		// In Mode 2, joystick 1 controls the left wheels of the robot and
		// joystick 2 controls the right wheels
		driveTrain.TeleopUpdate(); // uses the joysticks to control the
									// drivetrain

		// all of the controls for the pneumatics are on the far left joystick.
		// The trigger turns toggles the gear pusher.
		// Button 2 toggles the door
		// Button 9 turns on manual override for five seconds on the mechanism's
		// safety feature.
		gearMechanism.TeleopUpdate();// uses the joysticks to control the gear
										// mechanism.

		// Moving joystick 0 forward and back controls the climber motor.
		climber.TeleopUpdate(); // uses the joysticks to control the climber.
		SmartDashboard.putNumber("Acceleration X NavX",ahrs.getWorldLinearAccelX());
		SmartDashboard.putNumber("Acceleration Y NavX",ahrs.getWorldLinearAccelY());

	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

		
		  if (AutoStep == 0) 
		  {
			  driveForward.resetDistance();
			  AutoStep++; 
			  } 
		  if (MoveJoystick.getTrigger()) 
		  {
			  driveForward.driveForward(12, 0.5, 0);
		  } 
		 if (TurnJoystick.getTrigger()) 
		 { 
			 turner.TurnToDegrees(60, 0.5); 
		 }
		 if (TurnJoystick.getRawButton(2))
		 {
			 ahrs.zeroYaw(); 
		 } 
		 if(MoveJoystick.getRawButton(2))
		 {
			 driveForward.resetDistance();
		 }
		 

		driveTrain.TestUpdate();
		climber.TestUpdate();
		gearMechanism.TestUpdate();
		packetReader.getPacket();
	}
	
	@Override
	public void disabledPeriodic()
	{
		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.putData("Loading Station",LoadingStationChooser);
		
	}
	
	public boolean alignWithPeg()
	{
		Map<String,Object> boundingBox = packetReader.getPacket();
		targeter.setCurrentBoundingBox(boundingBox);
		return targeter.adjustCourse();
	}

}
