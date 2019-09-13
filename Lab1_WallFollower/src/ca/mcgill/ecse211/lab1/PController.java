package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class PController extends UltrasonicController {

  private static final int MOTOR_SPEED = 200;
  private int lasterror;
  private final int filterDistance = 50;    //difference between previous and current errors to signal a false positive


  public PController() {
    LEFT_MOTOR.setSpeed(MOTOR_SPEED); // Initialize motor rolling forward
    RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }

  @Override
  public void processUSData(int distance) {
    filter(distance);

    // TODO: process a movement based on the us distance passed in (P style)
    int errorCM = this.distance - BAND_CENTER;  //offset between current position and ideal distance from wall (in cm)
    //check if the reported error suddenly changed - ignore value if so (false positive)
    if(errorCM - lasterror > this.filterDistance)
    {
        lasterror = errorCM;
        return;
    }
    if(Math.abs(errorCM) <= BAND_WIDTH) //straight (in dead band)
    {
      LEFT_MOTOR.setSpeed(MOTOR_SPEED);
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
    }
    else if(errorCM < 0)                                //too close to wall - swerve right
    {
        LEFT_MOTOR.setSpeed(MOTOR_SPEED + (BAND_CENTER - Math.abs(this.distance)) * 11.0f); //constant higher because the distance closer to the wall is constrained (0-bandcenter)
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
    }
    else {                                  //too far from wall - swerve left
        LEFT_MOTOR.setSpeed(MOTOR_SPEED);    
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED + this.distance * 1.5f);    //smaller constant as distance can get much higher (bandcenter-255)
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
    }
    lasterror = errorCM; //record last error
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
