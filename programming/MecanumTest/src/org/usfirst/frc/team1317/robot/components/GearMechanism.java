package org.usfirst.frc.team1317.robot.components;
import edu.wpi.first.wpilibj.*;
import org.usfirst.frc.team1317.robot.*;
public class GearMechanism implements RobotComponent {
	
    Compressor GearCompressor;
	Solenoid DoorOpener;
	DoubleSolenoid GearPusher;
	Boolean DoorOpen;
	Boolean PistonOut;
	Joystick control;
	Boolean ManualOverride;
	byte counter;
	
	public GearMechanism(Joystick j)
	{
		//creates new solenoids
		DoorOpener = new Solenoid(RobotPorts.DoorSolenoidPort,RobotPorts.DoorSolenoidPort2);
		GearPusher = new DoubleSolenoid(RobotPorts.PusherSolenoidPort1,RobotPorts.PusherSolenoidPort2);
		//saves the joystick that was input
		control = j;
		//sets values to default
		ManualOverride = false;
		counter = 0;
		//these may need to be changed depending on how we start.
		DoorOpen = false;
		PistonOut = false;
		GearCompressor = new Compressor();
		
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
		//when button 2 is pressed
		
		if(control.getRawButton(2))
		{
			//if the door is opne close the door, unless the piston is out
			if(DoorOpen) {
				tryCloseDoor();
			}
			//if the door is closed open the door.
			else {
				openDoor();
			}
		}
		
		//when the trigger is pressed
		if(control.getTrigger())
		{
			//if the piston is out, bring the piston in.
			if (PistonOut)
			{
				retractGearPiston();
			}
			//if the piston is in
			else
			{
				this.trypushGear();
			}	
		}
		
		if(Timer.getMatchTime()>120)
		{
			GearCompressor.stop();
		}
	}

	public void openDoor()
	{
		DoorOpener.set(true);
		DoorOpen = true;
	}
	
	public Boolean tryCloseDoor()
	{
		if (!PistonOut) {
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
				DoorOpen = false;
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
		GearPusher.set(DoubleSolenoid.Value.kReverse);
		PistonOut = false;
	}
	
	public Boolean trypushGear()
	{
		//push the piston out if the door is open.
		if (DoorOpen){
			GearPusher.set(DoubleSolenoid.Value.kForward);
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
	
	public void ManualOverideControl()
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
