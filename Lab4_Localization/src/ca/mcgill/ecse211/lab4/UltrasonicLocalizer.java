package ca.mcgill.ecse211.lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;
import static ca.mcgill.ecse211.lab4.Resources.*;

/**
 * this class takes care of the two
 * localization methods
 * rising edge and falling edge
 *
 */
public class UltrasonicLocalizer {

  private int Threshold = 50;
  private int noiseFilter = 20;

  private SampleProvider usSensor;


  /**
   * UltrasonicLocalizer takes input
   * @param sensor
   */
  public UltrasonicLocalizer (SampleProvider sensor){
    this.usSensor = sensor;
  }

  /**
   * falling edge method
   * 
   */
  public void fallingEdge(){
    // defining the angles
    double alpha = 0;
    double beta = 0;
    double deltaTheta = 0;

    // while distance is smaller than threshold
    while (getDistance() < Threshold- noiseFilter) {
      rotateClockwise();
    }

    leftMotor.stop(true);
    rightMotor.stop(false);

    // get angle
    alpha = odometer.getTheta() * 180/Math.PI;
    Sound.beep();

    // while distance is bigger than threshold
    while (getDistance() > Threshold - noiseFilter) {
      rotateCounterClockwise();
    }
    
    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // get angle
    beta = odometer.getTheta() * 180/Math.PI;
    Sound.beep();
    
    leftMotor.stop(true);
    rightMotor.stop(true);

    // angle is 
    deltaTheta = heading(alpha, beta);
    Sound.beepSequence();

    // set angle to
    odometer.setTheta(odometer.getTheta() + Math.toRadians(deltaTheta));
  }

  
  /**
   * rising edge method
   */
  public void risingEdge(){
    double alpha = 0;
    double beta = 0;
    double deltaTheta = 0;
    
    // while distance is smaller than threshold
    while (getDistance() < Threshold- noiseFilter) {
      rotateCounterClockwise();
    }
    
    leftMotor.stop(true);
    rightMotor.stop(false);
   
    alpha = odometer.getTheta() * 180/Math.PI;
    Sound.beep();	



    while (getDistance() > Threshold - noiseFilter) {
      rotateClockwise();
    }
    leftMotor.stop(true);
    rightMotor.stop(false);
    beta = odometer.getTheta() * 180/Math.PI;
    Sound.beep();

    deltaTheta = heading(alpha, beta);
    Sound.beepSequence();
    leftMotor.stop(true);
    rightMotor.stop(true);
    odometer.setTheta(odometer.getTheta() + Math.toRadians(deltaTheta));

  }



  public float getDistance() {
    float[] usData = new float[usSensor.sampleSize()];
    usSensor.fetchSample(usData, 0);
    float distance = usData[0]*100;

    if (distance > Threshold) {
      distance = Threshold;
    }

    return distance;
  }

  /**
   * method to rotate the robot counter clockwise
   */
  public void rotateCounterClockwise() {
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.forward();
    rightMotor.backward();
  }

  /**
   * method to rotate the robot clockwise
   */
  public void rotateClockwise() {
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);
    leftMotor.backward();
    rightMotor.forward();
  }



  public double heading(double alpha, double beta) {
    double deltaTheta = 0;
    if (alpha < beta) {
      deltaTheta = 45 - ((alpha + beta)/2);
    }
    else {
      deltaTheta = 225 - ((alpha + beta)/2);
    }
    return deltaTheta;
  }

}