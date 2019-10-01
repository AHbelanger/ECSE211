
package ca.mcgill.ecse211.lab3;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import static ca.mcgill.ecse211.lab3.Resources.*;

/**
 * This class is used to drive the robot on the demo floor.
 */
public class ObstacleAvoidance {
  private static int ultrasonicDistanceMeasured;

  /**
   * The method driveWithObstacle takes @param distance and @param ultrasonicDistance as inputs 
   * and drives the robot while searching for the obstacle. When it reaches the obstacle, it avoids it 
   * by going around it.
   */
  public static void driveWithObstacle(double distance, SampleProvider ultrasonicDistance) {
    // Stop both motors and reset their acceleration
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.setAcceleration(500);
    rightMotor.setAcceleration(500);
    
    //sleep robot
    try {
      Thread.sleep(600);
    } 
    catch (Exception e) {
    }

    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);
    
    //Move robot forward
    leftMotor.rotate(convertDistance(WHEEL_RAD, distance), true);
    rightMotor.rotate(convertDistance(WHEEL_RAD, distance), true);
    
    //sleep robot
    try {
      Thread.sleep(100);
    } 
    catch (Exception e) {
    }
    
    boolean isNavigating = true;
    float[] sample = new float[ultrasonicDistance.sampleSize()];
    
    //Infinite loop until it meets the obstacle
    while (isNavigating) {
      //Get the distance from sensor
      ultrasonicDistance.fetchSample(sample, 0);
      ultrasonicDistanceMeasured = (int) (sample[0] * 100);

      //break loop when both left/right speeds reach 0. This means robot has stop.
      if (leftMotor.getRotationSpeed() == 0 && rightMotor.getRotationSpeed() == 0) {
        break;
      }

      //When robot reaches distance under 8 cm from robot, execute the obstacle avoidance.
      if (ultrasonicDistanceMeasured < 8) {
        rightMotor.setSpeed(10);
        leftMotor.setSpeed(10);

        leftMotor.setSpeed(ROTATE_SPEED);
        rightMotor.setSpeed(ROTATE_SPEED);
        leftMotor.rotate(convertAngle(WHEEL_RAD, TRACK, 90), true);
        rightMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, 90), false);

        leftMotor.setSpeed(FORWARD_SPEED);
        rightMotor.setSpeed(FORWARD_SPEED);
        leftMotor.rotate(convertDistance(WHEEL_RAD, 20), true);
        rightMotor.rotate(convertDistance(WHEEL_RAD, 20), false);

        leftMotor.setSpeed(ROTATE_SPEED);
        rightMotor.setSpeed(ROTATE_SPEED);
        leftMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, 90), true);
        rightMotor.rotate(convertAngle(WHEEL_RAD, TRACK, 90), false);

        leftMotor.setSpeed(FORWARD_SPEED);
        rightMotor.setSpeed(FORWARD_SPEED);
        leftMotor.rotate(convertDistance(WHEEL_RAD, 30), true);
        rightMotor.rotate(convertDistance(WHEEL_RAD, 30), false);

        isNavigating = false;
        
        //Index from the navigation class is decreased by 1 so that computation on same waypoint.
        Navigation.index = Navigation.index - 1;
        break;
      }
    }
  }

  /**
   *The method drive takes @param distance as input and travels for @param distance cm. 
   */
  public static void drive(double distance) {
    // Stop both motors and reset their acceleration
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.setAcceleration(100);
    rightMotor.setAcceleration(100);

    //sleep robot
    try {
      Thread.sleep(500);
    } 
    catch (Exception e) {
    }

    rightMotor.synchronizeWith(new RegulatedMotor[] {leftMotor});
    rightMotor.startSynchronization();

    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);

    leftMotor.rotate(convertDistance(WHEEL_RAD, distance), true);
    rightMotor.rotate(convertDistance(WHEEL_RAD, distance), false);

    rightMotor.endSynchronization();

    leftMotor.waitComplete();
    rightMotor.waitComplete();
  }

  /**
   * The turn method takes @param theta as input and tells the robot to change its orientation 
   * by theta angle. 
   */
  public static void turn(double theta) {
    // Stop both motors and reset their acceleration
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.setAcceleration(500);
    rightMotor.setAcceleration(500);

    //sleep robot
    try {
      Thread.sleep(500);
    } 
    catch (Exception e) {
    }

    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);

    leftMotor.rotate(convertAngle(WHEEL_RAD, TRACK, theta), true);
    rightMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, theta), false);
  }

  /**
   * The method convertDistance takes @param radius and @param distance as inputs and
   * returns the rotation needed by each wheel to cover the distance. 
   */
  private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }
}
