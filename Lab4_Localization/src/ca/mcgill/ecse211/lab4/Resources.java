package ca.mcgill.ecse211.lab4;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import ca.mcgill.ecse211.lab4.Odometer;

/**
 * This class is used to define static resources in one place for easy access and to avoid cluttering the rest of the
 * codebase. All resources can be imported at once like this:
 * 
 * <p>
 * {@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {

  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.130;

  /**
   * The robot width in centimeters. if turns too much lower, if turns not enoughm go up
   */
  public static final double TRACK = 9.8;

  /**
   * The speed at which the robot moves forward in degrees per second.
   */
  public static final int FORWARD_SPEED = 250;

  /**
   * The speed at which the robot rotates in degrees per second.
   */
  public static final int ROTATE_SPEED = 120;

  /**
   * The motor acceleration in degrees per second squared.
   */
  public static final int ACCELERATION = 2000;

  /**
   * Timeout period in milliseconds.
   */
  public static final int TIMEOUT_PERIOD = 3000;

  /**
   * The tile size in centimeters.
   */
  public static final double TILE_SIZE = 30.48;

  public static final Port usPort = LocalEV3.get().getPort("S2");
  public static final Port lightPort = LocalEV3.get().getPort("S1");
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
  /**
   * The LCD.
   */
  public static final TextLCD LCD = LocalEV3.get().getTextLCD();



//  /**
//   * The odometer.
//   */
//  public static Odometer odometer = Odometer.getOdometer();

}
