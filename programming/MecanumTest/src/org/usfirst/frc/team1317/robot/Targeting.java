package org.usfirst.frc.team1317.robot;

import org.usfirst.frc.team1317.robot.components.*;
import java.util.*;

public class Targeting {
	
	final int TargetX =  500;
	final int TargetXError = 3;
	final double DistanceError = 0.03;
	final int WidthAtTarget = 500;
	final int WidthAtFarthestPoint = 20;
	final int FarthestPointInches = 122;
	final double ForwardSpeed = -0.75;
	final double ForwardSpeed2 = -0.5;
	final double ForwardSpeed3 = -0.3;
	final double SlidingSpeed = 1.0;
	final double TurningSpeed = 0.5;
	
	
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
			if((int)boundingBox.get("x")==0&&(int)boundingBox.get("y")==0 && (int)boundingBox.get("w")==0)
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
			if(liftNumber == LeftLift)
			{
				driveTrain.drive(-0.3, 0, 0);
			}
			else if (liftNumber == RightLift)
			{
				driveTrain.drive(0.3, 0, 0);
			}
			else if (liftNumber == CenterLift)
			{
				if(CenterCounter>150)
				{
					driveTrain.drive(0.4, 0, 0);
				}
				else if (CenterCounter>450)
				{
					driveTrain.drive(-0.4, 0, 0);
				}
				else if (CenterCounter>600)
				{
					CenterCounter=0;
				}
				else
				{
					driveTrain.drive(-0.4, 0, 0);
					
				}
			}
			return false;
		}
		else
		{
			int xNow = (int)currentBoundingBox.get("x") + (int)currentBoundingBox.get("w")/2;
			double distance = estimateDistancetoTarget();
			if (Math.abs(distance)>DistanceError&&distance>0)
			{
				if(distance<6)
				{
					forward = ForwardSpeed3;
				}
				if(distance<14)
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
			}
			else if (xNow>TargetX)
			{
				sliding = -SlidingSpeed;
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
		return (WidthAtTarget-(int)currentBoundingBox.get("w"))/(WidthAtTarget-WidthAtFarthestPoint)*FarthestPointInches;
	}
	
	public void setLiftTarget(byte target)
	{
		liftNumber = target;
	}
}
