package ca.mcgill.ecse211.lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
  // robot position
  private double x;
  private double y;
  private double theta;
  private int leftMotorTachoCount;
  private int rightMotorTachoCount;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private int lastTachoL;
  private int lastTachoR;
  public int nowTachoL;
  public int nowTachoR;
  

  private static final long ODOMETER_PERIOD = 25; /*odometer update period, in ms*/

  private Object lock; /*lock object for mutual exclusion*/

  // default constructor
  public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
    this.x = 0.0;
    this.y = 0.0;
    this.theta = 0.0;
    this.leftMotorTachoCount = 0;
    this.rightMotorTachoCount = 0;
    
    this.lastTachoL = 0;
    this.lastTachoR = 0;
    lock = new Object();
  }

  // run method (required for Thread)
  public void run() {
    long updateStart, updateEnd;
    
    double distL, distR, deltaD, deltaT, headingTheta, dX, dY, deltaTinDEG;
    leftMotor.resetTachoCount();
    rightMotor.resetTachoCount();
    lastTachoL=leftMotor.getTachoCount();
    lastTachoR=rightMotor.getTachoCount();

    while (true) {
      updateStart = System.currentTimeMillis();
      // TODO put (some of) your odometer code here
      
        nowTachoL = leftMotor.getTachoCount();      		// get tacho counts
		nowTachoR = rightMotor.getTachoCount();
		distL = Math.PI * LocalizationLab.WHEEL_RADIUS * (nowTachoL-lastTachoL)/180;		// compute L and R wheel displacements
		distR = Math.PI * LocalizationLab.WHEEL_RADIUS * (nowTachoR-lastTachoR)/180;
		lastTachoL = nowTachoL;								// save tacho counts for next iteration
		lastTachoR = nowTachoR;
		deltaD = 0.5 * (distL + distR);							// compute vehicle displacement
		deltaT = (distL - distR)/LocalizationLab.TRACK;
		//headingTheta = Math.toRadians(getTheta()) + deltaT;
		dX = deltaD * Math.sin(theta);
	    dY = deltaD * Math.cos(theta);
	    //deltaTinDEG = Math.toDegrees(deltaT);
		
      
	    

      synchronized (lock) {
        /**
         * Don't use the variables x, y, or theta anywhere but here! Only update the values of x, y,
         * and theta in this block. Do not perform complex math
         * 
         */
         // TODO replace example value
    	  theta = theta + deltaT;
    	  if (this.theta > 2*Math.PI) {
              // Wrap around using Modular arithmetic if theta exceeds 360
              this.theta -= 2*Math.PI;
            } else if (this.theta < 0) {
              this.theta += 2*Math.PI;
            }
          x = x + dX;
          y = y + dY;
      }

      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done here because it is not
          // expected that the odometer will be interrupted by
          // another thread
        }
      }
    }
  }

  public void getPosition(double[] position, boolean[] update) {
    // ensure that the values don't change while the odometer is running
    synchronized (lock) {
      if (update[0])
        position[0] = x;
      if (update[1])
        position[1] = y;
      if (update[2])
        position[2] = theta * 180/Math.PI;
    }
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

  public double getTheta() {
    double result;

    synchronized (lock) {
      result = theta;
    }

    return result;
  }

  // mutators
  public void setPosition(double[] position, boolean[] update) {
    // ensure that the values don't change while the odometer is running
    synchronized (lock) {
      if (update[0])
        x = position[0];
      if (update[1])
        y = position[1];
      if (update[2])
        theta = position[2];
    }
  }

  public void setX(double x) {
    synchronized (lock) {
      this.x = x;
    }
  }

  public void setY(double y) {
    synchronized (lock) {
      this.y = y;
    }
  }

  public void setTheta(double theta) {
    synchronized (lock) {
      this.theta = theta;
    }
  }

  /**
   * @return the leftMotorTachoCount
   */
  public int getLeftMotorTachoCount() {
    return leftMotorTachoCount;
  }

  /**
   * @param leftMotorTachoCount the leftMotorTachoCount to set
   */
  public void setLeftMotorTachoCount(int leftMotorTachoCount) {
    synchronized (lock) {
      this.leftMotorTachoCount = leftMotorTachoCount;
    }
  }

  /**
   * @return the rightMotorTachoCount
   */
  public int getRightMotorTachoCount() {
    return rightMotorTachoCount;
  }

  /**
   * @param rightMotorTachoCount the rightMotorTachoCount to set
   */
  public void setRightMotorTachoCount(int rightMotorTachoCount) {
    synchronized (lock) {
      this.rightMotorTachoCount = rightMotorTachoCount;
    }
  }
}
