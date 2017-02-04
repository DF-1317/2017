package org.usfirst.frc.team1317.robot.components;
import edu.wpi.first.wpilibj.*;
import org.usfirst.frc.team1317.robot.*;
public class GearMechanism implements RobotComponent {

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
		DoorOpener = new Solenoid(RobotPorts.DoorSolenoidPort);
		GearPusher = new DoubleSolenoid(RobotPorts.PusherSolenoidPort1,RobotPorts.PusherSolenoidPort2);
		//saves the joystick that was input
		control = j;
		//sets values to default
		ManualOverride = false;
		counter = 0;
		//these may need to be changed depending on how we start.
		DoorOpen = false;
		PistonOut = false;
		
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
				if (!PistonOut) {
					DoorOpener.set(false);
					DoorOpen = false;
				}
				else 
				{
					//if manual override is set open the door anyway.
					if (ManualOverride)
					{
						DoorOpener.set(false);
						DoorOpen = false;
						System.out.println("Manually overriden.");
					}
					else
					{
						//inform the user why we cannot extend the piston.
						System.out.println("Cannot close door with piston extended.");
					}
				}
			}
			//if the door is closed open the door.
			else {
				DoorOpener.set(true);
				DoorOpen = true;
			}
		}
		
		//when the trigger is pressed
		if(control.getTrigger())
		{
			//if the piston is out, bring the piston in.
			if (PistonOut)
			{
				GearPusher.set(DoubleSolenoid.Value.kReverse);
				PistonOut = false;
			}
			//if the piston is in
			else
			{
				//push the piston out if the door is open.
				if (DoorOpen){
					GearPusher.set(DoubleSolenoid.Value.kForward);
				}
				else
				{
					if(ManualOverride)
					{
						GearPusher.set(DoubleSolenoid.Value.kReverse);
						PistonOut = false;
						System.out.println("Manually Overridden");
					}
					//inform the user why we cannot extend the piston.
					System.out.println("Piston cannot be extended with door closed.");
				}
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
