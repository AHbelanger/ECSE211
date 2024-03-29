package ca.mcgill.ecse211.lab3;

import java.text.DecimalFormat;
import static ca.mcgill.ecse211.lab3.Resources.*;

public class LCDInfo implements Runnable {

  private double[] position;
  private final long DISPLAY_PERIOD = 25;
  private long timeout = Long.MAX_VALUE;

  public LCDInfo() {}

  public void run() {

    LCD.clear();

    long updateStart, updateEnd;

    long tStart = System.currentTimeMillis();

    do {
      updateStart = System.currentTimeMillis();

      // Retrieve x, y and Theta information
      position = odometer.getXYT();

      // Print x,y, and theta information
      DecimalFormat numberFormat = new DecimalFormat("######0.00");
      LCD.drawString("X: " + numberFormat.format(position[0] + TILE_SIZE), 0, 0);
      LCD.drawString("Y: " + numberFormat.format(position[1] + TILE_SIZE), 0, 1);
      LCD.drawString("T: " + numberFormat.format(position[2] + TILE_SIZE), 0, 2);

      // this ensures that the data is updated only once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < DISPLAY_PERIOD) {
        try {
          Thread.sleep(DISPLAY_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } while ((updateEnd - tStart) <= timeout);

  }

}
