
package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The odometer class keeps track of the robot's (x, y, theta) position. It implements runnable and determines the x,
 * and theta values based on the magnitude and speed of the revolutions of the wheels.
 */

public class Odometer implements Runnable {

  public static final double WB = TRACK; // wheelbase
  public static final double WR = WHEEL_RAD; // wheel radius

  // Motor-related variables
  public static int lastTachoL; // Tacho L at last sample
  public static int lastTachoR; // Tacho R at last sample
  public static int nowTachoL = 0; // Current Tacho L
  public static int nowTachoR = 0; // Current Tacho R

  /**
   * The x-axis position in cm.
   */
  private volatile double x;

  /**
   * The y-axis position in cm.
   */
  private volatile double y; // y-axis position

  /**
   * The orientation in degrees.
   */
  private volatile double theta; // Head angle

  /**
   * The (x, y, theta) position as an array
   */
  private double[] position;

  // Thread control tools
  /**
   * Fair lock for concurrent writing
   */
  private static Lock lock = new ReentrantLock(true);

  /**
   * Indicates if a thread is trying to reset any position parameters
   */
  private volatile boolean isResetting = false;

  /**
   * Lets other threads know that a reset operation is over.
   */
  private Condition doneResetting = lock.newCondition();

  private static Odometer odo; // Returned as singleton

  /**
   * The odometer update period in ms.
   */
  private static final long ODOMETER_PERIOD = 25;


  /**
   * This is the default constructor of this class. It initiates all motors and variables once.It cannot be accessed
   * externally.
   */
  private Odometer() {
    setXYT(0, 0, 0);
  }

  /**
   * Returns the Odometer Object. Use this method to obtain an instance of Odometer.
   * 
   * @return the Odometer Object
   */
  public synchronized static Odometer getOdometer() {
    if (odo == null) {
      odo = new Odometer();
    }

    return odo;
  }

  /**
   * This method is where the logic for the odometer will run. It gets the current tacho counts and computes the wheel
   * displacements. Also, it computes the vehicle displacement and change in heading. From this, it computes the
   * displacement in terms of x and y.
   */
  public void run() {
    long updateStart, updateEnd;

    while (true) {
      updateStart = System.currentTimeMillis();


      double distL, distR, deltaD, deltaT, dX, dY;

      // get the current tacho count for left/right motor
      nowTachoL = leftMotor.getTachoCount();
      nowTachoR = rightMotor.getTachoCount();

      // compute wheel displacements
      distR = Math.PI * WR * (nowTachoR - lastTachoR) / 180;
      distL = Math.PI * WR * (nowTachoL - lastTachoL) / 180;

      // set current tacho as last tacho
      lastTachoL = nowTachoL;
      lastTachoR = nowTachoR;

      // compute vehicle displacement
      deltaD = 0.5 * (distL + distR);

      // compute change in heading
      deltaT = Math.toDegrees(((distL - distR) / WB));

      // update heading
      odo.update(0, 0, deltaT);

      // compute X and Y component of displacement
      dX = deltaD * Math.sin(Math.toRadians(odo.getXYT()[2]));
      dY = deltaD * Math.cos(Math.toRadians(odo.getXYT()[2]));

      // update x and y
      odo.update(dX, dY, 0);

      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done
        }
      }
    }
  }


  // IT IS NOT NECESSARY TO MODIFY ANYTHING BELOW THIS LINE

  /**
   * Returns the Odometer data.
   * <p>
   * Writes the current position and orientation of the robot onto the odoData array. {@code odoData[0] =
   * x, odoData[1] = y; odoData[2] = theta;}
   * 
   * @param position the array to store the odometer data
   * @return the odometer data.
   */
  public double[] getXYT() {
    double[] position = new double[3];
    lock.lock();
    try {
      while (isResetting) { // If a reset operation is being executed, wait until it is over.
        doneResetting.await(); // Using await() is lighter on the CPU than simple busy wait.
      }

      position[0] = x;
      position[1] = y;
      position[2] = theta;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return position;
  }

  /**
   * Adds dx, dy and dtheta to the current values of x, y and theta, respectively. Useful for odometry.
   * 
   * @param dx
   * @param dy
   * @param dtheta
   */
  public void update(double dx, double dy, double dtheta) {
    lock.lock();
    isResetting = true;
    try {
      x += dx;
      y += dy;
      theta = (theta + (360 + dtheta) % 360) % 360; // keeps the updates within 360 degrees
      isResetting = false;
      doneResetting.signalAll(); // Let the other threads know we are done resetting
    } finally {
      lock.unlock();
    }

  }

  /**
   * Overrides the values of x, y and theta. Use for odometry correction.
   * 
   * @param x the value of x
   * @param y the value of y
   * @param theta the value of theta in degrees
   */
  public void setXYT(double x, double y, double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      this.y = y;
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites x. Use for odometry correction.
   * 
   * @param x the value of x
   */
  public void setX(double x) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites y. Use for odometry correction.
   * 
   * @param y the value of y
   */
  public void setY(double y) {
    lock.lock();
    isResetting = true;
    try {
      this.y = y;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public double getTheta() {
    double result;

    synchronized (lock) {
      result = theta;
    }

    return result;
  }
  
  public double getX() {
    double result;

    synchronized (lock) {
      result = x;
    }

    return result;
  }
  
  public double getY() {
    double result;

    synchronized (lock) {
      result = y;
    }

    return result;
  }

  /**
   * Overwrites theta. Use for odometry correction.
   * 
   * @param theta the value of theta
   */
  public void setTheta(double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

}


