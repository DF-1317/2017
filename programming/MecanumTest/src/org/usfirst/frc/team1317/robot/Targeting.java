package org.usfirst.frc.team1317.robot;

import org.usfirst.frc.team1317.robot.components.*;
import java.util.*;

public class Targeting {
	
	final double TargetX = 137.5; 
	final int TargetXError = 5;
	final double DistanceError = 0.03;
	final int WidthAtTarget = 187;
	final int WidthAtFarthestPoint = 50;
	final int FarthestPointInches = 104;
	final double ForwardSpeed = -0.2;
	final double ForwardSpeed2 = -0.15;
	final double ForwardSpeed3 = -0.1;
	final double SlidingSpeed = 0.4;
	final double TurningSpeed = 0.5;
	final double TurningCorrectionSpeed = 0.1;
	
	
	private byte liftNumber;
	final byte LeftLift = 0;
	final byte CenterLift = 1;
	final byte RightLift = 2;
	
	int CenterCounter = 0;

	
	MecanumDriveTrain driveTrain;
	Map<String,Object> currentBoundingBox;

	public Targeting(MecanumDriveTrain driving)
	{
		driveTrain = driving;
	}
	
	public void setCurrentBoundingBox(Map<String,Object> boundingBox)
	{
		if(boundingBox != null)
		{
			if((double)boundingBox.get("x")==0.0&&(double)boundingBox.get("y")==0.0 && (double)boundingBox.get("w")==0.0)
			{
				return;
			}
			currentBoundingBox = boundingBox;
		}
	}
	
	public Boolean adjustCourse()
	{
		double turning=0;
		double sliding=0;
		double forward=0;
		Boolean DoneAligningX=false;
		Boolean DoneMovingForward=false;
		if (currentBoundingBox == null)
		{
			System.out.println("Searching for Target...");
			if(liftNumber == LeftLift)
			{
				driveTrain.drive(-0.3, -0.2, 0);
			}
			else if (liftNumber == RightLift)
			{
				driveTrain.drive(0.3, -0.2, 0);
			}
			else if (liftNumber == CenterLift)
			{
				if(CenterCounter>600)
				{
					CenterCounter=0;
				}
				else if (CenterCounter>450)
				{
					driveTrain.drive(-0.2, 0, 0);
				}
				else if(CenterCounter>150)
				{
					driveTrain.drive(0.2, 0, 0);
				}
				else
				{
					driveTrain.drive(-0.2, 0, 0);
				}
				CenterCounter++;
			}
			return false;
		}
		else
		{
			double xNow = (double)currentBoundingBox.get("x") + (double)currentBoundingBox.get("w")/2.0;
			double distance = estimateDistancetoTarget();
			if (distance>DistanceError)
			{
				if(distance<6.0)
				{
					forward = ForwardSpeed3;
				}
				if(distance<14.0)
				{
					forward = ForwardSpeed2;
				}
				else
				{
					forward=ForwardSpeed;
				}
			}
			else
			{
				DoneMovingForward = true;
			}
			if(Math.abs(xNow-TargetX)<=TargetXError)
			{
				turning = 0;
				sliding = 0;
				DoneAligningX = true;
			}
			else if (xNow<TargetX)
			{
				sliding = SlidingSpeed;
				turning = -TurningCorrectionSpeed;
			}
			else if (xNow>TargetX)
			{
				sliding = -SlidingSpeed;
				turning = TurningCorrectionSpeed;
			}
			driveTrain.drive(sliding, forward, turning);
			if(DoneAligningX&&DoneMovingForward)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	public double estimateDistancetoTarget()
	{
		return (WidthAtTarget-(double)currentBoundingBox.get("w"))/(WidthAtTarget-WidthAtFarthestPoint)*FarthestPointInches;
	}
	
	public void setLiftTarget(byte target)
	{
		liftNumber = target;
	}
}
