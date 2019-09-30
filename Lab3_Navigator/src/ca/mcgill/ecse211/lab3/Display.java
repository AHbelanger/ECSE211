package ca.mcgill.ecse211.lab3;

import java.text.DecimalFormat;
import static ca.mcgill.ecse211.lab3.Resources.*;

/**
 * This class is used to display the content of the odometer variables (x, y, Theta)
 */
public class Display implements Runnable {

  private double[] pos;
  private final long DISPLAY_PERIOD = 100;

  /**
   * This is the class constructor
   * 
   * @param odoData
   * @throws OdometerExceptions 
   */
  public Display() {
  }

  public void run() {
    
    LCD.clear();
    
    long updateStart, updateEnd;

    long tStart = System.currentTimeMillis();
    do {
      updateStart = System.currentTimeMillis();

      // Retrieve x, y and Theta information
      pos = odometer.getXYT();
      
      // Print x,y, and theta information
      DecimalFormat numberFormat = new DecimalFormat("######0.00");
      LCD.drawString("X: " + numberFormat.format(pos[0]), 0, 0);
      LCD.drawString("Y: " + numberFormat.format(pos[1]), 0, 1);
      LCD.drawString("T: " + numberFormat.format(pos[2]), 0, 2);
 
      // this ensures that the data is updated only once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < DISPLAY_PERIOD) {
        try {
          Thread.sleep(DISPLAY_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } while ((updateEnd - tStart) <= Long.MAX_VALUE);

  }

}
