package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
//import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * the odometryCorrection class is a thread
 * 
 * 
 *
 */
public class OdometryCorrection implements Runnable {

  private static final long CORRECTION_PERIOD = 10; // time between corrections
  private Odometer odometer; 
 
  private double[] currentPosition;
  private int yCount, xCount, xInc, yInc;
  private double newX, newY;
  private int lastColor = 2;
  private int currentColor;
 
  


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
      
      //black line detection
      currentColor = colorSensor.getColorID();
      if (currentColor - lastColor > 5) {
        
        currentPosition = odometer.getXYT();
        Sound.beep();
        
        //Increment value
        yInc = Math.round((float) Math.cos(Math.toRadians(currentPosition[2])));
        xInc = Math.round((float) Math.cos(Math.toRadians(currentPosition[2])));

        yCount += yInc;
        xCount += xInc;
        
        
        //Are we crossing tile boundary from the upper or lower boundary?
        if (xInc < 0) {
          newX = xCount * TILE_SIZE;
        }
        else if (xInc > 0) {
          newX = (xCount - 1) * TILE_SIZE;
        }
        else {
          newX = currentPosition[0];
        }
        
        if (yInc < 0) {
          newY = yCount * TILE_SIZE;
        }
        else if (yInc > 0) {
          newY = (yCount - 1) * TILE_SIZE;
        }
        else {
          newY = currentPosition[1];
        }
    
        odometer.setXYT(newX, newY, currentPosition[2]);

        
      }

      // this ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
    
    
  }
}