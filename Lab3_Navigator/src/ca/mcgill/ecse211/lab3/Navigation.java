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
	public static int increment;
	private static double[] robotPosition = new double[3];
	public static double[] nextWayPoint = new double[2];

	/*
	 * @param leftMotor
	 * @param rightMotor
	 * @param WR
	 * @param WR
	 * @param track
	 * @param positionWaypoints
	 * @param us_Distance
	 * @param obstacle_Avoidance
	 * @throws OdometerExceptions
	 */
	
	/** This method is the main method of this class, it calculates the necessary distances 
	 * and turning angles and calls the appropriate methods */
	public static void navigationControl(double[] positionWaypoints, SampleProvider us_Distance, boolean obstacle_Avoidance) {
		/* Here we get the odometer instance for use in the calculations, and begin a for loop which 
		 * calculates the necessary operations for every waypoint
		 */
		for(increment = 0; increment < 5; increment++) {
			//System.out.println("This is the #"+increment+" leg");
			//Robot position determined
			robotPosition = odometer.getXYT();
			//System.out.println("Current X position is: "+robotPosition[0]+"     Current Y position is: "+robotPosition[1]+"       Current Theta is: "+robotPosition[2]);
			//Next waypoint is set to the nextWayPoint variable
			nextWayPoint[0] = positionWaypoints[increment*2] * 30.48 - 30.48;
			nextWayPoint[1] = positionWaypoints[increment*2 + 1] * 30.48 - 30.48;
			//DeltaX and DeltaY are determined
			deltaX = nextWayPoint[0] - robotPosition[0];
			deltaY = nextWayPoint[1] - robotPosition[1];
			//System.out.println("Delta X is: "+deltaX);
			//System.out.println("Delta Y is: "+deltaY);
			//Turning angle is determined
			turnToTheta = Math.atan(deltaX/deltaY);
			turnToTheta = Math.toDegrees(turnToTheta);
			//The next 4 if statements are used to account for certain
			//situations in which the angle calculation isnt totally correct
			if(deltaX > 0 && deltaY < 0) {
				turnToTheta = turnToTheta + 180;
			}
			if(deltaX < 0 && deltaY < 0) {
				turnToTheta = turnToTheta - 180;
			}
			if(turnToTheta < 0) {
				turnToTheta = turnToTheta + 360;
			}
			if(deltaX == 0) {
				if(deltaY > 0) {
					turnToTheta = 0;
				}
				else if(deltaY < 0) {
					turnToTheta = 180;
				}
			}
			//Distance we need to travel is determined
			distance = Math.sqrt(Math.pow(deltaX, 2)+Math.pow(deltaY, 2));
			currentTheta = robotPosition[2];
			//Turnto class is called first, followed by the travel to class
			turnTo(turnToTheta, currentTheta);
			//The ultrasonic instance and obstacle avoidance boolean are passed here as well
			travelTo(distance, us_Distance, obstacle_Avoidance);
		}
		
	}
	/** The travelTo method determined whether obstacle avoidance is necessary, and directs the robot */
	public static void travelTo(double distance, SampleProvider us_Distance, boolean obstacle_Avoidance) {
		//If the obstacle avoidance boolean is true, the obstacle avoidance method is run with the us sensor passed
		if(obstacle_Avoidance) {
			ObstacleAvoidance.obstacle_Driver(distance, us_Distance);
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
