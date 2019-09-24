package ca.mcgill.ecse211.lab2;


import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;
//import lejos.hardware.sensor.SensorModes;
//import lejos.robotics.SampleProvider;

/**
 * 
 * the OdometryCorrection class specifies the code for the EV3 robot
 * when the robot hits a line, it counts it
 * the values of x and y are SET at each black line
 * @author Sabrina
 *
 */
public class OdometryCorrection implements Runnable {
  //correction frequency period of 10
  private static final long CORRECTION_PERIOD = 10;


  private double[] position;

  /*
   *where the odometry correction should run
   */
  public void run() {
    long correctionStart, correctionEnd;
    
    int xCount = -1, yCount = -1; // counts how many lines in x and y

    double newX, newY;
    while (true) { // while the robot is detecting lines

      correctionStart = System.currentTimeMillis();

      myColorSample.fetchSample(sampleColor, 0);

      //get current position
      position = odometer.getXYT();
      double theta = position[2] % 360;

      //when the sensor detects a line
      if((sampleColor[0] * 1000) < 175 && (sampleColor[0] * 1000) > 100) {
        
        if((theta <= 45.0 && theta >= 0)  || (theta >= 323 && theta < 360)) { // angle is between 45 and 0 and 325 to 360
          // y augment x stays the same
          Sound.beep();
          yCount++;
          newY = yCount * TILE_SIZE;
          odometer.setY(newY);
          odometer.setTheta(0);
        }
        else if(theta >= 70 && theta < 150) { //if theta is higher than 70 and lower than 150
          // x augments y stay the same
        Sound.beep();
          xCount++;
          newX = xCount * TILE_SIZE;
          odometer.setX(newX);
          odometer.setTheta(90); 
        }
        else if(theta >= 150 && theta < 230) {// angle is higher than 150 and lower than 230
          // y lowers, x stays the same
          Sound.beep();
          newY = yCount * TILE_SIZE;           
          odometer.setY(newY);
          odometer.setTheta(180);
          yCount--;
        }
        else if(theta >= 230 && theta < 324) {//angle is higher than 230 and lower than 324
          // y stays the same x lowers
          Sound.beep();
          newX = xCount * TILE_SIZE; 
          odometer.setX(newX);
          odometer.setTheta(270);
          xCount--;
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
