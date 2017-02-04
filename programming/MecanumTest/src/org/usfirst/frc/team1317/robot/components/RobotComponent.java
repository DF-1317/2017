package org.usfirst.frc.team1317.robot.components;

/** This class represents a robot component.
 * The functions are meant to simulate the functions of the iterative robot.
 * @author JonathanB05
 */
public interface RobotComponent {
	public void AutoStart();
	public void AutoUpdate();
	public void TeleopUpdate();
	public void TestUpdate();
}
