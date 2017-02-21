package org.usfirst.frc.team1317.robot.components;

public interface GearMechanism extends RobotComponent {
	public void AutoStart();
	public void AutoUpdate();
	public void TeleopUpdate();
	public void openDoor();
	public Boolean tryCloseDoor();
	public void retractGearPiston();
	public Boolean trypushGear();
	public void TestUpdate();
	public void ManualOverrideControl();

}
