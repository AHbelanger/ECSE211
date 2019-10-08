package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;

/**
 * Navigation class calculates the distances and turning angles needed for navigation
 * @author Sabrina
 *
 */
public class Navigation {

  // motor related values
  static int leftMotorTachoCount;
  static int rightMotorTachoCount;
  static int lastTachoL;
  static int lastTachoR;
  static int nowTachoL;
  static int nowTachoR;
  
  // position and angle
  static double x;
  static double y;
  static double theta;
  

  /**
   * The travelTo method takes 2 inputs @param x, @param y, 
   * and determines the distance the robot must travel. 
   */
  public void travelTo (double x, double y){
    double thetaToGo= 0;

    double toX = x - odometer.getX(); // x side
    double toY = y - odometer.getY(); // y side
    double dist = Math.sqrt(Math.pow(toX, 2) + Math.pow(toY, 2) ); // pythagore distance

    // if x side is equal to 0 and y side is bigger than 0
    if (toX == 0 && toY > 0){
      thetaToGo = Math.PI/2;
      turnTo(thetaToGo);                                                                                                                                                                                                    
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();
      
      leftMotor.rotate(convertDistance(WHEEL_RAD, dist), true);
      rightMotor.rotate(convertDistance(WHEEL_RAD, dist), false);

    }
    
    // if the x side is bigger than 0
    else if (toX > 0){
      thetaToGo = (Math.atan((toY)/(toX)));
      turnTo(thetaToGo);
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();
      
      leftMotor.rotate(convertDistance(WHEEL_RAD, dist), true);
      rightMotor.rotate(convertDistance(WHEEL_RAD, dist), false);

    }
    
    // if the y side is 0 and the x side is less than 0
    else if (toY == 0 && toX < 0){
      thetaToGo = Math.PI;
      turnTo(thetaToGo);
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();
      
      leftMotor.rotate(convertDistance(WHEEL_RAD, dist), true);
      rightMotor.rotate(convertDistance(WHEEL_RAD, dist), false);

    }
    
    // if x side is equal to 0 and y side less than 0
    else if (toX == 0 && toY < 0){
      thetaToGo = -Math.PI/2;
      turnTo(thetaToGo);
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();
      
      leftMotor.rotate(convertDistance(WHEEL_RAD, dist), true);
      rightMotor.rotate(convertDistance(WHEEL_RAD, dist), false);


    }
    
    // if x side is less than 0 and y side is less than 0
    else if (toX < 0 && toY < 0){
      thetaToGo = (Math.atan((toY)/(toX))) - Math.PI;
      turnTo(thetaToGo);
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();
      
      leftMotor.rotate(convertDistance(WHEEL_RAD, dist), true);
      rightMotor.rotate(convertDistance(WHEEL_RAD, dist), false);
    }
    
    // is x side is less than 0 and y side is bigger than 0
    else if (toX < 0 && toY > 0){
      thetaToGo = (Math.atan((toY)/(toX))) + Math.PI;
      turnTo(thetaToGo);
      leftMotor.setSpeed(FORWARD_SPEED);
      rightMotor.setSpeed(FORWARD_SPEED);
      leftMotor.forward();
      rightMotor.forward();
      
      leftMotor.rotate(convertDistance(WHEEL_RAD, dist), true);
      rightMotor.rotate(convertDistance(WHEEL_RAD, dist), false);

    }
  }

 
   /**
   *  The turnTo method takes in the theta
   *  computes the error
   *  then moves the robot
   *  
   */
  public void turnTo (double theta){
    double error = theta - odometer.getTheta();
    double minAngle; // minimum angle
    
    // if error is bigger than pi or less than or equal than pi
    if (error >= -(Math.PI) && error <= (Math.PI)){
      minAngle = error;
      
      
      leftMotor.setSpeed(ROTATE_SPEED);
      rightMotor.setSpeed(ROTATE_SPEED);      
     
      leftMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, (minAngle*180/Math.PI)), true); //+
      rightMotor.rotate(convertAngle(WHEEL_RAD, TRACK, (minAngle*180/Math.PI) ), false);//-
  
    }
    
    // if error is less than pi (not including it
    else if (error < -Math.PI){
      minAngle = error + 2*Math.PI;
      
      // move robot
      leftMotor.setSpeed(ROTATE_SPEED);
      rightMotor.setSpeed(ROTATE_SPEED);
     
      leftMotor.rotate(convertAngle(WHEEL_RAD, TRACK, Math.toDegrees(minAngle)), true);
      rightMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, Math.toDegrees(minAngle)), false);
    }
    
    else if (error > Math.PI){
      minAngle = error - 2*Math.PI;
    
      leftMotor.setSpeed(ROTATE_SPEED);
      rightMotor.setSpeed(ROTATE_SPEED);
      leftMotor.rotate(convertAngle(WHEEL_RAD, TRACK, Math.toDegrees(minAngle)), true);
      rightMotor.rotate(-convertAngle(WHEEL_RAD, TRACK, Math.toDegrees(minAngle)), false);
    }

  }


  // convert the angle
  public static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }

  // convert the distance
  public static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

 
}