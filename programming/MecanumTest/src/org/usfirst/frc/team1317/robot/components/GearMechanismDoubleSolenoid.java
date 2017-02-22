package org.usfirst.frc.team1317.robot.components;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team1317.robot.*;
public class GearMechanismDoubleSolenoid implements GearMechanism {
	
    Compressor GearCompressor;
	DoubleSolenoid DoorOpener;
	DoubleSolenoid GearPusher;
	Boolean DoorOpen;
	Boolean PistonOut;
	Joystick control;
	Boolean ManualOverride;
	byte counter;
	Boolean oldButton2State;
	Boolean oldTriggerState;
	
	public GearMechanismDoubleSolenoid(Joystick j)
	{
		//creates new solenoids
		DoorOpener = new DoubleSolenoid(RobotPorts.doorSolenoidPortCompetition, RobotPorts.doorSolenoidPortCompetition2);
		GearPusher = new DoubleSolenoid(RobotPorts.PusherSolenoidPortCompetition, RobotPorts.PusherSolenoidPortCompetition2);
		//saves the joystick that was input
		control = j;
		//sets values to default
		ManualOverride = false;
		counter = 0;
		//these may need to be changed depending on how we start.
		DoorOpen = false;
		PistonOut = false;
		GearCompressor = new Compressor();
		oldButton2State = false;
		oldTriggerState = false;
		putDataToSmartDashboard();
		
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
		Boolean currentButton2 = control.getRawButton(2);
		Boolean currentTrigger = control.getTrigger();
		Boolean currentButton3 = control.getRawButton(3);
		Boolean currentButton4 = control.getRawButton(4);
		
		if(currentButton3) GearPusher.set(DoubleSolenoid.Value.kForward);
		if(currentButton4) GearPusher.set(DoubleSolenoid.Value.kReverse);
		
		ManualOverrideControl();
		//when button 2 is pressed
		if(currentButton2 && !oldButton2State)
		{
			//if the door is opne close the door, unless the piston is out
			if(DoorOpener.get()==DoubleSolenoid.Value.kForward) {
				tryCloseDoor();
			}
			//if the door is closed open the door.
			else {
				openDoor();
			}
			putDataToSmartDashboard();
		}
		
		//when the trigger is pressed
		if(currentTrigger && !oldTriggerState)
		{
			//if the piston is out, bring the piston in.
			if (GearPusher.get()==DoubleSolenoid.Value.kForward)
			{
				retractGearPiston();
			}
			//if the piston is in
			else
			{
				trypushGear();
			}
			putDataToSmartDashboard();
		}
		
		if(Timer.getMatchTime()>120)
		{
			GearCompressor.stop();
		}
		
		oldButton2State = currentButton2;
		oldTriggerState = currentTrigger;
	}

	@Override
	public void openDoor()
	{
		DoorOpener.set(DoubleSolenoid.Value.kForward);
		DoorOpen = true;
	}
	
	@Override
	public Boolean tryCloseDoor()
	{
		if (GearPusher.get()==DoubleSolenoid.Value.kForward) {
			DoorOpener.set(DoubleSolenoid.Value.kReverse);
			DoorOpen = false;
			return true;
		}
		else 
		{
			//if manual override is set close the door anyway.
			if (ManualOverride)
			{
				DoorOpener.set(DoubleSolenoid.Value.kReverse);
				DoorOpen = true;
				System.out.println("Manually overriden.");
				return true;
			}
			else
			{
				//inform the user why we cannot extend the piston.
				System.out.println("Cannot close door with piston extended.");
				return false;
			}
		}
	}
	
	@Override
	public void retractGearPiston()
	{
		GearPusher.set(DoubleSolenoid.Value.kReverse);
		System.out.println("retracted piston");
		PistonOut = false;
	}
	
	@Override
	public Boolean trypushGear()
	{
		//push the piston out if the door is open.
		if (DoorOpener.get()==DoubleSolenoid.Value.kForward){
			GearPusher.set(DoubleSolenoid.Value.kForward);
			PistonOut = true;
			return true;
		}
		else
		{
			if(ManualOverride)
			{
				GearPusher.set(DoubleSolenoid.Value.kForward);
				PistonOut = false;
				System.out.println("Manually Overridden");
				return true;
			}
			else {
			//inform the user why we cannot extend the piston.
			System.out.println("Piston cannot be extended with door closed.");
			return false;
			}
		}
	}

	@Override
	public void TestUpdate() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void ManualOverrideControl()
	{
		//increment the counter when it is manual override
		if(ManualOverride)
		{
			counter++;
		}
		
		//when the counter is greater than 250 (about 5 seconds)
		if(counter>250)
		{
			//turn manual override off.
			counter = 0;
		}
		//turn on manual override with button 9
		if(control.getRawButton(9))
		{
			ManualOverride = true;
			counter = 0;
		}
		
	}
	
	void putDataToSmartDashboard()
	{
		Boolean pistonout;
		Boolean dooropened;
		if(DoorOpener.get()==DoubleSolenoid.Value.kForward)
		{
			dooropened = true;
		}
		else
		{
			dooropened = false;
		}
		if(GearPusher.get()==DoubleSolenoid.Value.kForward)
		{
			pistonout = true;
		}
		else
		{
			pistonout = false;
		}
		SmartDashboard.putBoolean("Door Opened", dooropened);
		SmartDashboard.putBoolean("Gear Pusher out", pistonout);
	}

}