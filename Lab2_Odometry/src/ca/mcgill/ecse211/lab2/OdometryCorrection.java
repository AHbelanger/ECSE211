package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
//import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * the odometryCorrection class is a thread
 * 
 * @author Sabrina
 *
 */
public class OdometryCorrection implements Runnable {

  private static final long CORRECTION_PERIOD = 10; // time between corrections
  private Odometer odometer; 

  // creating the light sensor
  private static final Port colorSampler = LocalEV3.get().getPort("S1"); // get the port for the light sensor
  private SensorModes colorSamplerSensor = new EV3ColorSensor(colorSampler); // get instance of sensor
  private SampleProvider colorSensorValue = colorSamplerSensor.getMode("Red"); // get sample provider
  private float[] colorSensorData = new float[colorSensorValue.sampleSize()]; // create data buffer

    private float oldValue = 0;
    private int counterX; // counts how many lines in x
    private int counterY; // counts how many lines in y
    private double theta; // angle


  /**
   *  constructor for the odometer
   *  ensures that the thread runs safely
   */
  public OdometryCorrection() {
    this.odometer = Odometer.getOdometer();
  }

  /**
   * where the run method will execute
   * needs this to run since it is in a thread
   * when a line is detected, the robot will adjust the position
   */
  public void run() {
    long correctionStart, correctionEnd;  

    while (true) {
      correctionStart = System.currentTimeMillis();
      
      //fetching the values from the color sensor
      colorSensorValue.fetchSample(colorSensorData, 0);
      
      //getting the value returned from the sensor, and multiply it by 1000 to scale
      float value = colorSensorData[0]*1000;
      
      //computing the derivative at each point
      float diff = value - oldValue;
      
      //storing the current value, to be able to get the derivative on the next iteration
      oldValue = value;
      
      //if the derivative value at a given point is less than -50, this means that a black line is detected
      if(diff < -50) {
          
          //robot beeps
          Sound.beep();
          
          //get the status of the robot by the counter used in squareDriver, this counter keeps track of the orientation of the robot
          //int status = SquareDriver.getSquareCount();
          
          theta = odometer.getTheta() * 180 / Math.PI;
          /*
           * The Y and X counter keeps track of how many horizontal and vertical black lines the robot detected respectively.
           * This counter is used to computer the Y and X values respect to the origin (first intersection).
           * theta tells us if the robot is moving forward horizontally or vertically.
           */
          
          if( (theta <= 360 && theta >= 315) || (theta >=0 && theta <= 45)){
              odometer.setY(counterY * TILE_SIZE);
              counterY++;
          }else if(theta > 45 && theta <= 135){
              odometer.setX(counterX * TILE_SIZE);
              counterX++;
          }else if(theta > 135 && theta <= 225){
              counterY--;
              odometer.setY(counterY * TILE_SIZE);
          }else if(theta > 225 && theta < 315){
              counterX--;
              odometer.setX(counterX * TILE_SIZE);
          }
          
          try {
              Thread.sleep(500);
          } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
      }

      // this ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
  }
}


