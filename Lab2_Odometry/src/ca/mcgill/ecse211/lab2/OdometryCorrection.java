package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import java.util.ArrayList;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
//import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection implements Runnable {

  private static final long CORRECTION_PERIOD = 10; // time between corrections
  private Odometer odometer; 

  // creating the light sensor
  private static final Port colorSampler = LocalEV3.get().getPort("S1"); // get the port for the light sensor
  private SensorModes colorSamplerSensor = new EV3ColorSensor(colorSampler); // get instance of sensor
  private SampleProvider colorSensorValue = colorSamplerSensor.getMode("Red"); // get sample provider
  private float[] colorSensorData = new float[colorSamplerSensor.sampleSize()]; // create data buffer

  //  private float oldValue = 0;
  //  private int counterX; // counts how many lines in x
  //  private int counterY; // counts how many lines in y
  //  private double theta; // angle

  private static int counter = 0; //counts how many times the light sensor has seen a line
  private static int SQUARE_SIDES = 4;
  private static ArrayList<Double> increments = new ArrayList<Double>(); //will hold our increment values


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

    //I initialize the array with the appropriate values
    for (int i=0; i< (SQUARE_SIDES-1)*4; i++) {

      if (i<(SQUARE_SIDES-1)) { //first side
        increments.add(i*TILE_SIZE);
      } else if (i<(SQUARE_SIDES-1)*2) { //second side
        increments.add((i % 3)*TILE_SIZE);
      } else if (i<(SQUARE_SIDES-1)*3) { //third side
        increments.add((SQUARE_SIDES - 2 - i%3)*TILE_SIZE);
      } else if (i<(SQUARE_SIDES-1)*4) { //last side
        increments.add((SQUARE_SIDES - 2 - i%3)*TILE_SIZE);
      }
    }

    // TODO Trigger correction (When do I have information to correct?)
    while (true) {
      correctionStart = System.currentTimeMillis();


      //fetching the values from the color sensor
      colorSensorValue.fetchSample(colorSensorData, 0);

      if (colorSensorData[0] <= 0.35) { // if we are over a black line

        Sound.beep(); // alerts us that we are over a line

        if (counter < (SQUARE_SIDES - 1) || (counter >= (SQUARE_SIDES - 1)*2 && counter < (SQUARE_SIDES-1)*3)) { 
          //We adjust Y on the first and third side of the square
          odometer.setY(increments.get(counter));
        } else if ((counter >= (SQUARE_SIDES - 1) && counter < (SQUARE_SIDES -1)*2) || (counter >= (SQUARE_SIDES -1)*3 && counter< (SQUARE_SIDES - 1)*4)){
          //We adjust X
          odometer.setX(increments.get(counter));
        }
        counter++;
      }

      // TODO Update odometer with new calculated (and more accurate) values, eg:
      //odometer.setXYT(0.3, 19.23, 5.0);

      // this ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
  }
}


