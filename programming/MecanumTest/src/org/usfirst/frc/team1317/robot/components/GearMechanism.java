package org.usfirst.frc.team1317.robot.components;
import edu.wpi.first.wpilibj.*;
import org.usfirst.frc.team1317.robot.*;
public class GearMechanism implements RobotComponent {

	Solenoid DoorOpener;
	DoubleSolenoid GearPusher;
	Boolean DoorOpen;
	Boolean PistonOut;
	Joystick control;
	
	public GearMechanism(Joystick j)
	{
		DoorOpener = new Solenoid(RobotPorts.DoorSolenoidPort);
		GearPusher = new DoubleSolenoid(RobotPorts.PusherSolenoidPort1,RobotPorts.PusherSolenoidPort2);
		control = j;
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
					//inform the user why we cannot extend the piston.
					System.out.println("Cannot close door with piston extended.");
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

}
