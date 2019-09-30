package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import lejos.robotics.SampleProvider;

/** Navigation class calculates the distances and turning angles needed for navigation */
public class Navigation {
  /* Declaring all the necessary variables */
  private static double deltaX;
  private static double deltaY;
  public static double newTheta;
  private static double currentTheta;
  private static double deltaTheta;
  private static double distance;
  public static int index;
  private static double[] pos = new double[3];
  private static double nextX, nextY;


  /**
   * This method is the main method of this class, it calculates the necessary distances and turning angles and calls
   * the appropriate methods
   */
  public static void navigationControl(double[] Waypoints, SampleProvider ultrasonicDistance, boolean avoidObstacle) {

    // Go to each waypoint
    for (index = 0; index < 5; index++) {

      // Get the current position of robot
      pos = odometer.getXYT();

      // Get next X coordinate
      nextX = Waypoints[index * 2] * TILE_SIZE - TILE_SIZE;

      // Get next Y coordinate
      nextY = Waypoints[index * 2 + 1] * TILE_SIZE - TILE_SIZE;

      // Compute deltaX/deltaY
      deltaX = nextX - pos[0];
      deltaY = nextY - pos[1];

      // Compute angle turn
      newTheta = Math.toDegrees(Math.atan(deltaX / deltaY));

      // if positive x and negative y
      if (deltaX > 0 && deltaY < 0) {
        newTheta += 180;
      }
      // if negative x and negative y
      if (deltaX < 0 && deltaY < 0) {
        newTheta -= 180;
      }
      // change negative angle to get opposite positive
      if (newTheta < 0) {
        newTheta += 360;
      }
      // if moving straight
      if (deltaX == 0 && deltaY > 0) {
        newTheta = 0;
      }
      // if moving backwards
      if (deltaX == 0 && deltaY < 0) {
        newTheta = 180;
      }

      //Compute robot's orientation
      turnTo(newTheta, pos[2]);
      //Compute distance to travel
      travelTo(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)), ultrasonicDistance, avoidObstacle);
    }

  }

  /**
   * The travelTo method takes 3 inputs @param distance, @param ultrasonicDistance, @param avoidObstacle
   * and determines the distance the robot must travel. It takes into account whether the robot
   * must avoid any obstacles or not.
   */
  public static void travelTo(double distance, SampleProvider ultrasonicDistance, boolean avoidObstacle) {
    if (avoidObstacle) {
      ObstacleAvoidance.driveWithObstacle(distance, ultrasonicDistance);
    } 
    else {
      ObstacleAvoidance.drive(distance);
    }
  }

  /**
   * The turnTo method computes the change in angle necessary for the robot and takes as inputs @param newTheta
   * and @param currentTheta. It then calls the turning method of the ObstacleAvoidance.
   */
  public static void turnTo(double newTheta, double currentTheta) {
    if (currentTheta >= 355 || currentTheta <= 5) {
      currentTheta = 0;
    }
    deltaTheta = newTheta - currentTheta;
    if (deltaTheta > 180) {
      deltaTheta = deltaTheta - 360;
    } else if (deltaTheta < -180) {
      deltaTheta = deltaTheta + 360;
    }

    if (deltaTheta < 0) {
      // System.out.println("Turning left!");
      ObstacleAvoidance.turn(deltaTheta);
    } else if (deltaTheta > 0) {
      // System.out.println("Turning right!");
      ObstacleAvoidance.turn(deltaTheta);
    }

  }
}
