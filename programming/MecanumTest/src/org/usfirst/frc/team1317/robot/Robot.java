package org.usfirst.frc.team1317.robot;

import edu.wpi.first.wpilibj.hal.PortsJNI;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

import org.usfirst.frc.team1317.robot.commands.*;
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
	final String centerAuto = "Center";
	final String leftAuto = "Left";
	final String rightAuto = "Right";
	final double trackerPeriod = 0.0;
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	final String LeftLoadingStation = "Left";
	final String RightLoadingStation = "Right";
	final String AutoLoadingStation = "Auto";
	String loadingStationSelected;
	String LoadingStation;
	SendableChooser<String> LoadingStationChooser = new SendableChooser<>();
	
	final boolean VisionTrackingOn = true;
	final boolean VisionTrackingOff = false;
	boolean VisionTracking;
	SendableChooser<Boolean> VisionTrackingChooser = new SendableChooser<>();
	
	boolean AutoCommandMode = true;
	
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
	Timer TrackingTimer;
	
	boolean lastAlignment=false;

	PacketReader packetReader;
	Targeting targeter;
	
	DriverStation driverStation;
	
	Command AutonomousCommand;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		// some default code for autonomous selection
		chooser.addObject("Center", centerAuto);
		chooser.addObject("Left", leftAuto);
		chooser.addObject("Right", rightAuto);
		chooser.addDefault("Cross the Baseline", crossingAuto);
		SmartDashboard.putData("Auto choices", chooser);
		
		LoadingStationChooser.addDefault("Auto", AutoLoadingStation);
		LoadingStationChooser.addObject("Left Loading Station", LeftLoadingStation);
		LoadingStationChooser.addObject("Right Loading Station", RightLoadingStation);
		SmartDashboard.putData("Loading Station",LoadingStationChooser);
		
		VisionTrackingChooser.addDefault("Vision Tracking On", VisionTrackingOn);
		VisionTrackingChooser.addObject("Vision Tracking Off", VisionTrackingOff);
		SmartDashboard.putData("Vision Tracking",VisionTrackingChooser);

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
		AutoStep = -4;
		turner = new PIDTurning(driveTrain, ahrs);
		driveForward = new PIDDriveDistance(driveTrain, ahrs);
		AutoTimer = new Timer();
		packetReader = new PacketReader();
		targeter = new Targeting(driveTrain);
		driverStation = DriverStation.getInstance();
		TrackingTimer = new Timer();
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
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		loadingStationSelected = LoadingStationChooser.getSelected();
		VisionTracking = VisionTrackingChooser.getSelected();
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
		System.out.println("Auto selected: " + autoSelected);
		if(AutoCommandMode)
		{
			autonomousInitCommand();
		}
		else
		{
			autonomousInitSteps();
		}
		driveTrain.AutoStart();
		gearMechanism.AutoStart();
		climber.AutoStart();
	}
	
	private void autonomousInitCommand() {
		//if the crossing auto is selected
		if(autoSelected == crossingAuto)
		{
			//drive forward at 60% speed for 2 seconds
			AutonomousCommand = new DriveForward(2.0,-0.6,driveTrain);
		}
		else if(VisionTracking)
		{
			if(autoSelected == centerAuto)
			{
				targeter.setLiftTarget(targeter.CenterLift);
			}
			else if(autoSelected == leftAuto)
			{
				targeter.setLiftTarget(targeter.LeftLift);
			}
			else if(autoSelected == rightAuto)
			{
				targeter.setLiftTarget(targeter.RightLift);
			}
			AutonomousCommand = new VisionTrackingAutonomousCenter(this,driveTrain,gearMechanism);
		}
		else
		{
			if(autoSelected == centerAuto)
			{
				AutonomousCommand = new TimerAutonomousCenter(driveTrain, gearMechanism);
			}
		}
		if(AutonomousCommand != null)
			AutonomousCommand.start();
		else
		{
			AutoCommandMode=false;
			autonomousInitSteps();
		}
		TrackingTimer.reset();
		TrackingTimer.start();
	}
	
	private void autonomousInitSteps() {
		AutoTimer.reset();
		AutoTimer.start();
		turner.reset();
		AutoStep =-4;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		if(AutoCommandMode)
		{
			autonomousPeriodicCommand();
		}
		else
		{
			autonomousPeriodicStep();
		}
		driveTrain.AutoUpdate();
		gearMechanism.AutoUpdate();
		climber.AutoUpdate();
	}
	
	private void autonomousPeriodicCommand() {
		Scheduler.getInstance().run();
	}
	private void autonomousPeriodicStep(){
		Boolean next = false;
		if(autoSelected == crossingAuto)
		{
			if (AutoTimer.get() < 2) {
				driveTrain.drive(0, -0.6, 0);
			}
			else
			{
				driveTrain.drive(0, 0, 0);
			}
		}
		else
		{
			if (AutoStep == -4) {
				driveTrain.resetDistance();
				next = true;
				driveTrain.ResetLineUpSteps();
				AutoTimer.reset();
				AutoTimer.start();
			}
			else if (AutoStep == -3)
			{
				if(autoSelected==rightAuto||autoSelected == leftAuto)
				{
					if(VisionTracking)
					{
						if(AutoTimer.get()<=0.1)
						{
							driveTrain.drive(0, 0.4, 0);
						}
						else
						{
							next = true;
							driveTrain.drive(0, 0, 0);
						}
					}
				}
				else
				{
					next = true;
				}
			}
			else if (AutoStep == -2)
			{
				if(VisionTracking)
				{
					if(autoSelected == leftAuto)
					{
						next = turner.TurnToDegrees(60, 0.9);
					}
					else if(autoSelected == rightAuto)
					{
						next = turner.TurnToDegrees(-60, 0.9);
					}
					else
					{
						next = true;
					}
				}
				else
				{
					next = true;
				}
			}
			else if (AutoStep == -1) {
				if(VisionTracking)
				{
					next = alignWithPeg();
				}
				else
				{
					if(autoSelected==centerAuto)
					{
						next = driveTrain.lineUpWithCenterPeg();
					}
					else if(autoSelected==leftAuto)
					{
						if(ahrs.isConnected())
						{
							next = driveTrain.lineUpWithLeftPeg(turner);
						}
						else{
							if(AutoTimer.get()<0.5)
							{
								driveTrain.drive(0, 0, 0.5);
							}
							else
							{
								next =true;
							}
						}
					}
					else if(autoSelected==rightAuto)
					{
						if(ahrs.isConnected())
						{
							next = driveTrain.lineUpWithRightPeg(turner);
						}
						else
						{
							if(AutoTimer.get()<0.5)
							{
								driveTrain.drive(0, 0, -0.5);
							}
							else
							{
								next =true;
							}
						}
					}
				}
				if(next)
				{
					driveTrain.drive(0, 0, 0);
					AutoTimer.reset();
					AutoTimer.start();
				}
			}
			else if (AutoStep == 0) {
				if(AutoTimer.get()>0.5)
				{
					gearMechanism.openDoor();
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
			}
			else if (AutoStep == 1)
			{
				if(AutoTimer.get()>0.5)
				{
					next = true;
				}
				else
				{
					driveTrain.drive(0.0, 0.0, 0.0);
				}
			}
			else if (AutoStep == 2) {
				next = gearMechanism.trypushGear();
				next = gearMechanism.trypushGear();
				if(next)
				{
					AutoTimer.reset();
					AutoTimer.start();
				}
			}
			else if (AutoStep == 3)
			{
				if(AutoTimer.get()>1.2)
				{
					next = true;
				}
			}
			else if (AutoStep == 4) {
				AutoTimer.reset();
				AutoTimer.start();
				next = true;
			}
			else if (AutoStep == 5)
			{
				driveTrain.drive(0, 0.2, 0);
				if (AutoTimer.get()>0.9)
				{
					next = true;
					driveTrain.drive(0, 0, 0);
					AutoTimer.reset();
					AutoTimer.start();
				}
			}
			else if (AutoStep == 6) {
				gearMechanism.retractGearPiston();
				next = true;
				AutoTimer.reset();
				AutoTimer.start();
			}
			else if (AutoStep == 7) {
				if(AutoTimer.get()>0.5)
				{
					next = gearMechanism.tryCloseDoor();
				}
			}
			/*if (autoSelected == rightAuto && LoadingStation == "Right")
			{
				if (AutoStep ==8)
				{
					if(ahrs.isConnected())
						next = turner.TurnToDegrees(0, 0.9);
					else
					{
						if(AutoTimer.get()<0.5)
						{
							driveTrain.drive(0, 0, 0.5);
						}
						else
						{
							next =true;
						}
					}
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
						driveTrain.drive(0, 0, 0);
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
					if(ahrs.isConnected())
						next = turner.TurnToDegrees(0, 0.9);
					else
					{
						if(AutoTimer.get()<0.5)
						{
							driveTrain.drive(0, 0, -0.5);
						}
						else
						{
							next =true;
						}
					}
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
						driveTrain.drive(0, 0, 0);
					}
					else
					{
						driveTrain.drive(0, -0.5, 0);
					}
				}
			}
			else if(autoSelected == centerAuto)
			{
				if(AutoStep == 8)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if(AutoStep == 9)
				{
					if(AutoTimer.get()>1.7)
					{
						next = true;
						driveTrain.drive(0, 0, 0);
					}
					else
					{
						if(LoadingStation == "Right")
							driveTrain.drive(1.0, -0.02, 0);
						else
							driveTrain.drive(-1.0, 0.02, 0);
					}
				}
				else if(AutoStep == 10)
				{
					if(ahrs.isConnected())
					{
						next = turner.TurnToDegrees(0, 0.9);
					}
					else
					{
						next = true;
					}
				}
				else if(AutoStep == 11)
				{
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if (AutoStep ==12)
				{
					if(AutoTimer.get()>3)
					{
						next = true;
						driveTrain.drive(0, 0, 0);
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
					if(ahrs.isConnected())
						next = turner.TurnToDegrees(0, 0.9);
					else
					{
						if(AutoTimer.get()<0.5)
						{
							driveTrain.drive(0, 0, 0.5);
						}
						else
						{
							driveTrain.drive(0, 0, 0);
							next =true;
						}
					}
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
						driveTrain.drive(0, 0, 0);
					}
					else
					{
						driveTrain.drive(0, -0.5, 0);
					}
				}
				else if (AutoStep == 11)
				{
					turner.reset();
					AutoTimer.reset();
					AutoTimer.start();
					next = true;
				}
				else if (AutoStep == 12)
				{
					if(ahrs.isConnected())
					{
						next = turner.TurnDegrees(-90, 0.5);
					}
					else
					{
						if(AutoTimer.get()<1.5)
						{
							driveTrain.drive(0, 0, -0.5);
						}
						else
						{
							next = true;
						}
					}
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
						driveTrain.drive(0, 0, 0);
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
					if(ahrs.isConnected())
						next = turner.TurnToDegrees(0, 0.9);
					else
					{
						if(AutoTimer.get()<0.5)
						{
							driveTrain.drive(0, 0, -0.5);
						}
						else
						{
							next =true;
						}
					}
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
						driveTrain.drive(0, 0, 0);
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
					if (ahrs.isConnected())
					{
						next = turner.TurnDegrees(90, 0.9);
					}
					else
					{
						if(AutoTimer.get()<1.5)
						{
							driveTrain.drive(0, 0, 0.5);
						}
						else
						{
							next = true;
						}
					}
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
						driveTrain.drive(0, 0, 0);
						next = true;
					}
					else
					{
						driveTrain.drive(0, 0.75, 0);
					}
				}
			}
			*/
			if (next)
				AutoStep++;
		}
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
		if(MoveJoystick.getRawButton(7)==true)
		{
			ahrs.zeroYaw();
		}

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
		SmartDashboard.putData("Vision Tracking",VisionTrackingChooser);
		packetReader.getPacket();
		
	}
	
	public boolean alignWithPeg()
	{
		if(TrackingTimer.get()>=trackerPeriod)
		{
			Map<String,Object> boundingBox = packetReader.getPacket();
			targeter.setCurrentBoundingBox(boundingBox);
			TrackingTimer.reset();
			TrackingTimer.start();
			lastAlignment = targeter.adjustCourse();
		}
		return lastAlignment;
	}

}
