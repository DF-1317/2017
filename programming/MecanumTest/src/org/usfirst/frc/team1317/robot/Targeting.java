package org.usfirst.frc.team1317.robot;

import org.usfirst.frc.team1317.robot.components.*;
import java.util.*;

public class Targeting {
	
	final int TargetX =  500;
	final int TargetXError = 3;
	final double DistanceError = 0.03;
	final int WidthAtTarget = 500;
	final int WidthAtFarthestPoint = 100;
	final int FarthestPointInches = 120;
	final double ForwardSpeed = 0.75;
	final double ForwardSpeed2 = 0.5;
	final double ForwardSpeed3 =0.3;
	final double SlidingSpeed = 1.0;
	final double TurningSpeed = 0.5;
	
	
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
	
	public double estimateDistancetoTarget()
	{
		return (WidthAtTarget-(int)currentBoundingBox.get("w"))/(WidthAtTarget-WidthAtFarthestPoint)*FarthestPointInches;
	}
}
