package ca.mcgill.ecse211.lab3;

import lejos.hardware.Button;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import static ca.mcgill.ecse211.lab3.Resources.*;


public class Main {
	
    //Map Options
//	public static final double[] Waypoints = {1,3,2,2,3,3,3,2,2,1}; //Map1
//	public static final double[] Waypoints = {2,2,1,3,3,3,3,2,2,1}; //Map2
//	public static final double[] Waypoints = {2,1,3,2,3,3,1,3,2,2}; //Map3
//  public static final double[] Waypoints = {1,2,2,3,2,1,3,2,3,3}; //Map4
    public static final double[] Waypoints = {3,2,2,2,2,3,3,1}; //Demo


	
	/**
	 * The Main class starts the robot and determines which mode to run on.
	 * Also, it decides which map to use.
	 */
	public static void main(String[] args) {
		
		@SuppressWarnings("resource")
        SensorModes ultrasonicSensor = new EV3UltrasonicSensor(ultrasonicPort);
		final SampleProvider ultrasonicDistance = ultrasonicSensor.getMode("Distance");
		
		//Clear display
		LCD.clear();
			
		//Display two modes
		LCD.drawString("<Run    | Run>", 0, 0);
		LCD.drawString(" Without| With", 0, 1);
		LCD.drawString(" Block  | Block", 0, 2);
		LCD.drawString(" Avoid  | Avoid", 0, 3);
		
		//User clicks on desired mode
	    int buttonChoice;
		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		//Start the Odometer thread
		Thread odometerThread = new Thread(odometer);
		odometerThread.start();
		//Start the display thread
		Thread odometerDisplayThread = new Thread(new LCDInfo());
		odometerDisplayThread.start();
		
		//User determines if he wishes to use obstacle avoidance or not
		final boolean avoidObstacle = buttonChoice == Button.ID_LEFT ? false : true;
		
		//Start the navigation thread
		new Thread() {
			public void run() {
				Navigation.navigationControl(Waypoints, ultrasonicDistance, avoidObstacle);
			}
		}.start();
		
		//Exit program if escape key is pressed
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

}
