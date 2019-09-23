package ca.mcgill.ecse211.lab2;


import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;
//import lejos.hardware.sensor.SensorModes;
//import lejos.robotics.SampleProvider;

/**
 * This class specifies the algorithm for correction the odometry mechanism on
 * the EV3 robot. 
 */
public class OdometryCorrection implements Runnable {
  //sets correction frequency, controlling each correction loop at 10ms
  private static final long CORRECTION_PERIOD = 10;
  private static final double THRESHOLD = 1.0;

  private double[] position;


  /*
   * Here is where the odometer correction code should be run.
   */
  public void run() {
    long correctionStart, correctionEnd;



    // keeps track of how many lines detected on x and y axis
    int xCount = -1, yCount = -1;

    double newX, newY;
    while (true) { // while it's detecting lines
      
      correctionStart = System.currentTimeMillis();

      myColorSample.fetchSample(sampleColor, 0);

      //current position
      position = odometer.getXYT();
      double theta = position[2] % 360;

      //to plot the data points
      //      numSamples++;
      //      System.out.println(numSamples + "," + sampleColor[0]*1000);

      //when the sensor detects a line
      if((sampleColor[0] * 1000) < 175 && (sampleColor[0] * 1000) > 100) //change to differential calculation instead of abs value
      {
        Sound.beep();
        if(theta > (0 - THRESHOLD) * Math.PI / 180.0 && theta < (0 + THRESHOLD) * Math.PI / 180.0) //angle is 0: up
        {
          yCount++;
          Sound.beep();
          newY = yCount * TILE_SIZE;// / Math.cos(theta);
          odometer.setY(newY);
          odometer.setTheta(0);
        }
        else if(theta > (90 - THRESHOLD) * Math.PI / 180.0 && theta < (90 + THRESHOLD) * Math.PI / 180.0) //angle is 90: right
        {
          xCount++;
          newX = xCount * TILE_SIZE;// / Math.cos(theta - 90);
          Sound.beep();
          odometer.setX(newX);
          odometer.setTheta((90) * Math.PI / 180.0);
        }
        else if(theta > (180 - THRESHOLD) * Math.PI / 180.0 && theta < (180 + THRESHOLD) * Math.PI / 180.0) //angle is 180: down
        {
          newY = yCount * TILE_SIZE; // / Math.cos(theta - 180);
          Sound.beep();
          odometer.setY(newY);
          odometer.setTheta((180) * Math.PI / 180.0);
          yCount--;
        }
        else if(theta > (270 - THRESHOLD) * Math.PI / 180.0 && theta < (270 + THRESHOLD) * Math.PI / 180.0) //angle is 270: left
        {
          newX = xCount * TILE_SIZE; // / Math.cos(theta - 270);
          Sound.beep();
          odometer.setX(newX);
          odometer.setTheta((270) * Math.PI / 180.0);
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
