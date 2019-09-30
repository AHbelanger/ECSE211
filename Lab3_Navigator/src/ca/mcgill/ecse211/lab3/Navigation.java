package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import lejos.robotics.SampleProvider;
/** Navigation class calculates the distances and turning angles needed for navigation */
public class Navigation {
	/* Declaring all the necessary variables */
	private static double deltaX;
	private static double deltaY;
	public static double turnToTheta;
	private static double currentTheta;
	private static double deltaTheta;
	private static double distance;
	public static int index;
	private static double[] pos = new double[3];
	private static double nextX, nextY;
	
	
	/** This method is the main method of this class, it calculates the necessary distances 
	 * and turning angles and calls the appropriate methods */
	public static void navigationControl(double[] Waypoints, SampleProvider ultrasonicDistance, boolean avoidObstacle) {
		
	    //Go to each waypoint
		for(index = 0; index < 5; index++) {
			
		    //Get the current position of robot
			pos = odometer.getXYT();
			
			//Get next X coordinate
			nextX = Waypoints[index* 2] * TILE_SIZE - TILE_SIZE;
			
			//Get next Y coordinate
			nextY = Waypoints[index* 2 + 1] * TILE_SIZE - TILE_SIZE;
			
			//Compute deltaX/deltaY 
			deltaX = nextX - pos[0];
			deltaY = nextY - pos[1];
			
			//Compute angle turn 
			turnToTheta = Math.toDegrees(Math.atan(deltaX / deltaY));
			
			//if positive x and negative y
			if (deltaX > 0 && deltaY < 0) {
				turnToTheta += 180;
			}
			//if negative x and negative y
			if (deltaX < 0 && deltaY < 0) {
				turnToTheta -= 180;
			}
			//change negative angle to get opposite positive
			if (turnToTheta < 0) {
				turnToTheta += 360;
			}
			//if only moving straight or backwards
			if (deltaX == 0) {
				if(deltaY > 0) {
					turnToTheta = 0;
				}
				else if(deltaY < 0) {
					turnToTheta = 180;
				}
			}
			//Distance we need to travel is determined
			distance = Math.sqrt(Math.pow(deltaX, 2)+Math.pow(deltaY, 2));
			currentTheta = pos[2];
			//Turnto class is called first, followed by the travel to class
			turnTo(turnToTheta, currentTheta);
			//The ultrasonic instance and obstacle avoidance boolean are passed here as well
			travelTo(distance, ultrasonicDistance, avoidObstacle);
		}
		
	}
	/** The travelTo method determined whether obstacle avoidance is necessary, and directs the robot */
	public static void travelTo(double distance, SampleProvider ultrasonicDistance, boolean avoidObstacle) {
		//If the obstacle avoidance boolean is true, the obstacle avoidance method is run with the us sensor passed
		if(avoidObstacle) {
			ObstacleAvoidance.obstacle_Driver(distance, ultrasonicDistance);
		}
		else {
		ObstacleAvoidance.drive(distance);
		}
	}
	
	/** turnTo method calculates the deltaTheta required and calls the turning method in driver */
	public static void turnTo(double turnToTheta, double currentTheta) {
		if(currentTheta >= 355 || currentTheta <= 5) {
			currentTheta = 0;
		}
		deltaTheta  = turnToTheta - currentTheta;
		if(deltaTheta > 180) {
			deltaTheta = deltaTheta - 360;
		}
		else if(deltaTheta < -180) {
			deltaTheta = deltaTheta + 360;
		}
		//System.out.println("CurrentTheta is: "+currentTheta);
		//System.out.println("turnToTheta is: "+turnToTheta);
		//System.out.println("DeltaTheta is"+deltaTheta);
		if (deltaTheta < 0) {
			//System.out.println("Turning left!");
			ObstacleAvoidance.turn(deltaTheta);
		}
		else if (deltaTheta > 0) {
			//System.out.println("Turning right!");
			ObstacleAvoidance.turn(deltaTheta);
		}
		
	}
}
