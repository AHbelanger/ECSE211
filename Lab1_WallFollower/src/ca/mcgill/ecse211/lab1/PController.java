package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class PController extends UltrasonicController {

  
  public double error; // computing the error
  int pConstant = 5;
  int diff;
  private static final int maxCorrection = 350;


  public PController() {
    LEFT_MOTOR.setSpeed(MOTOR_HIGH); // Start robot moving forward
    RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();

  }

  @Override
  public void processUSData(int distance) {
    filter(distance);
    error = BAND_CENTER_P - distance; 
    /*error is the distance we want minus the distance we have 
     * the error must be within 
     *  
     *  */
    
    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
 
    if (Math.abs(error) <= BAND_WIDTH_P){ // if it is within the range, keep going forward
      LEFT_MOTOR.setSpeed(MOTOR_HIGH); // Start robot moving forward
      RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    }
    else if (error < 0) { // if current distance is too far from the wall
      // rotate left wheel slower
      diff = calcCorrection(error);
      LEFT_MOTOR.setSpeed(MOTOR_HIGH-diff*2); // Start robot moving forward //INITIALLY AT 4
      RIGHT_MOTOR.setSpeed(MOTOR_HIGH + diff*5);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    }
    else  { // if current distance is too close to the wall
      // if it deviates too far, rotate the left wheels faster/ slow down the right wheels
      diff = calcCorrection(error);
      LEFT_MOTOR.setSpeed(MOTOR_HIGH + diff*5); // Start robot moving forward //INITIALLY AT 4
      RIGHT_MOTOR.setSpeed(MOTOR_HIGH - diff*2);
      LEFT_MOTOR.forward();
      RIGHT_MOTOR.forward();
    }
  }
    
    private int calcCorrection(double errorVal) {
      errorVal = Math.abs(errorVal);

      int speedCorrection = (int) (pConstant * errorVal);

      if (speedCorrection >= maxCorrection)
          speedCorrection = 70;

      return speedCorrection;

  }
    
  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
