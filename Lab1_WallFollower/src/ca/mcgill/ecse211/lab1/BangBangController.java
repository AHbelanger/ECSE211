package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class BangBangController extends UltrasonicController {
  
  public static final int DELTASPEED = 50; // BANG BANG constant
  public double error; // computing the error

  public BangBangController() {
    LEFT_MOTOR.setSpeed(MOTOR_HIGH); // Start robot moving forward
    RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();

  }

  @Override
  public void processUSData(int distance) {
    filter(distance);
    error = BAND_CENTER - distance; 
    /*error is the distance we want minus the distance we have 
     * the error must be within 
     *  
     *  */
    
    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
 
    if (Math.abs(error) <= BAND_WIDTH){ // if it is within the range, keep going forward
      LEFT_MOTOR.setSpeed(MOTOR_HIGH); // Start robot moving forward
      RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    }
    else if (error < 0) { // if current distance is too far from the wall
      // rotate left wheel slower
      LEFT_MOTOR.setSpeed(MOTOR_HIGH-DELTASPEED*5); // Start robot moving forward //INITIALLY AT 4
      RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    }
    else  { // if current distance is too close to the wall
      // if it deviates too far, rotate the left wheels faster/ slow down the right wheels
      LEFT_MOTOR.setSpeed(MOTOR_HIGH);
      RIGHT_MOTOR.setSpeed(MOTOR_HIGH-DELTASPEED*5);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    }
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
