package ca.mcgill.ecse211.lab4;

import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;
import static ca.mcgill.ecse211.lab4.Resources.*;

public class LightLocalizer extends Thread {

  private SampleProvider colorSensor;
  private float[] csData;
  private double centerToSensor = 15;
  private double distBack = 15;


  public LightLocalizer(SampleProvider sensor, float[] csData) {
    this.colorSensor = sensor;
    this.csData = csData;
  }

  /**
   * This method scans the sequence of 4 black lines to make the robot travel to point (0,0) and have an orientation of
   * 0 degrees.
   */
  public void run() {

    while (true) {
      rightMotor.setSpeed(ROTATE_SPEED);
      leftMotor.setSpeed(ROTATE_SPEED);

      // rotate robot
      rightMotor.rotate(Navigation.convertAngle(WHEEL_RAD, TRACK, 360), true);
      leftMotor.rotate(-Navigation.convertAngle(WHEEL_RAD, TRACK, 360), true);

      double[] arrayOfAngles = new double[5];


      leftMotor.setSpeed(ROTATE_SPEED);
      rightMotor.setSpeed(ROTATE_SPEED);
      leftMotor.rotate(Navigation.convertDistance(WHEEL_RAD, distBack), true);
      rightMotor.rotate(Navigation.convertDistance(WHEEL_RAD, distBack), false);

      // Pass over sequence of 4 lines
      for (int i = 0; i < 4; i++) {
        // change speed of wheels
        rightMotor.setSpeed(ROTATE_SPEED);
        leftMotor.setSpeed(ROTATE_SPEED);
        // move wheels forward
        rightMotor.forward();
        leftMotor.backward();
        if (getSensor() < 30) {
          arrayOfAngles[i] = odometer.getTheta(); // get angle for each i
          Sound.beep(); // beep when black line is detected
        }
      }

      // stop motor
      leftMotor.stop(true);
      rightMotor.stop(false);

      // update values
      odometer.setX(-centerToSensor * Math.cos((Math.toRadians(arrayOfAngles[2] - arrayOfAngles[0]) / 2)));
      odometer.setY(-centerToSensor * Math.cos((Math.toRadians(arrayOfAngles[3] - arrayOfAngles[1]) / 2)));
      break; // quit loop

    }
  }

  // get color of board
  public float getSensor() {
    colorSensor.fetchSample(csData, 0);
    float sensorValue = csData[0] * 100;

    if (sensorValue > 30) {
      sensorValue = 30;
    } else if (sensorValue < 30) {
      sensorValue = 10;
    }
    return sensorValue;
  }
}


