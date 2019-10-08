package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import lejos.hardware.Button;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class Main {


  /**
   * The Main class starts the robot and determines which mode to run on. Also, it decides which map to use.
   */
  public static void main(String[] args) {

    SensorModes usSensor = new EV3UltrasonicSensor(usPort);
    SampleProvider usDistance = usSensor.getMode("Distance");

    EV3ColorSensor colorSensor = new EV3ColorSensor(lightPort);
    SampleProvider colorProvider = colorSensor.getMode("Red");

    float[] csData = new float[colorProvider.sampleSize()]; // csData is the buffer in which data are

    // Clear display
    LCD.clear();

    // Display two modes
    LCD.drawString("<Rise   | Fall>", 0, 0);
    LCD.drawString(" Edge   | Edge", 0, 1);
    LCD.drawString("        | ", 0, 2);
    LCD.drawString("        | ", 0, 3);

    // User clicks on desired mode
    int buttonChoice;
    do {
      buttonChoice = Button.waitForAnyPress();
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

    // Start the Odometer thread
    Thread odometerThread = new Thread(odometer);
    odometerThread.start();
    // Start the display thread
    Thread odometerDisplayThread = new Thread(new LCDInfo());
    odometerDisplayThread.start();

    //
    UltrasonicLocalizer ultrasonicLocalizer = new UltrasonicLocalizer(usDistance);
    LightLocalizer lightLocalizer = new LightLocalizer(colorProvider, csData); // colorSensor

    Navigation navigation = new Navigation();

    // falling edge method
    if (buttonChoice == Button.ID_RIGHT) {
      ultrasonicLocalizer.fallingEdge();
    } 
    // rising edge method
    else {
      ultrasonicLocalizer.risingEdge();
    }
    
    navigation.turnTo(0);

    Button.waitForAnyPress();
    
    // sets where the robot needs to travel
    navigation.turnTo(Math.PI / 4);
    lightLocalizer.start();
    navigation.travelTo(0, 0);
    navigation.turnTo(0);

    while (Button.waitForAnyPress() != Button.ID_ESCAPE);
    System.exit(0);
  }
}
