package org.usfirst.frc.team1317.robot.components;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team1317.robot.*;
public class GearMechanismSingleSolenoid implements GearMechanism {
	
    Compressor GearCompressor;
	Solenoid DoorOpener;
	Solenoid GearPusher;
	Boolean DoorOpen;
	Boolean PistonOut;
	Joystick control;
	Boolean ManualOverride;
	byte counter;
	Boolean oldButton2State;
	Boolean oldTriggerState;
	
	public GearMechanismSingleSolenoid(Joystick j)
	{
		//creates new solenoids
		DoorOpener = new Solenoid(RobotPorts.doorSolenoidPortPractice);
		GearPusher = new Solenoid(RobotPorts.PusherSolenoidPortPractice);
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
		SmartDashboard.putBoolean("Door Opened", DoorOpener.get());
		SmartDashboard.putBoolean("Gear Pusher out", GearPusher.get());
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
		ManualOverrideControl();
		//when button 2 is pressed
		if(currentButton2 && !oldButton2State)
		{
			//if the door is opne close the door, unless the piston is out
			if(DoorOpener.get()) {
				tryCloseDoor();
			}
			//if the door is closed open the door.
			else {
				openDoor();
			}
			SmartDashboard.putBoolean("Door Opened", DoorOpener.get());
		}
		
		//when the trigger is pressed
		if(currentTrigger && !oldTriggerState)
		{
			//if the piston is out, bring the piston in.
			if (GearPusher.get())
			{
				retractGearPiston();
			}
			//if the piston is in
			else
			{
				trypushGear();
			}
			SmartDashboard.putBoolean("Gear Pusher out", GearPusher.get());
		}
		
		if(Timer.getMatchTime()>120)
		{
			GearCompressor.stop();
		}
		
		oldButton2State = currentButton2;
		oldTriggerState = currentTrigger;
	}

	public void openDoor()
	{
		DoorOpener.set(true);
		DoorOpen = true;
	}
	
	public Boolean tryCloseDoor()
	{
		if (!GearPusher.get()) {
			DoorOpener.set(false);
			DoorOpen = false;
			return true;
		}
		else 
		{
			//if manual override is set open the door anyway.
			if (ManualOverride)
			{
				DoorOpener.set(true);
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
	
	public void retractGearPiston()
	{
		GearPusher.set(false);
		System.out.println("retracted piston");
		PistonOut = false;
	}
	
	public Boolean trypushGear()
	{
		//push the piston out if the door is open.
		if (DoorOpener.get()){
			GearPusher.set(true);
			PistonOut = true;
			return true;
		}
		else
		{
			if(ManualOverride)
			{
				GearPusher.set(false);
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

}
