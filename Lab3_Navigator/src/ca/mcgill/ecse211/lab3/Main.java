package ca.mcgill.ecse211.lab3;

import lejos.hardware.Button;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import ca.mcgill.ecse211.lab3.Resources;


public class Main {
	
	/*Initializing any variables this class may need */
	public static final double WHEEL_RAD = 2.18;
	public static final double TRACK = 10.1;
//	public static final double[] positionWaypoints = {1,3,2,2,3,3,3,2,2,1}; //Map1
//	public static final double[] positionWaypoints = {2,2,1,3,3,3,3,2,2,1}; //Map2
	public static final double[] positionWaypoints = {2,1,3,2,3,3,1,3,2,2}; //Map3
//  public static final double[] positionWaypoints = {1,2,2,3,2,1,3,2,3,3}; //Map4

	public static boolean obstacle_Avoidance = false;
	public static TextLCD lcd = Resources.lcd;
	
	
	
	/**Main Class, runs upon robot start*/
	public static void main(String[] args) {
		int buttonChoice;
		/* Setting up the Odometer, display, and US Sensor */
		Odometer odometer = Odometer.getOdometer();
		
		Display odometryDisplay = new Display(lcd);
		
		@SuppressWarnings("resource")
		SensorModes us_Sensor = new EV3UltrasonicSensor(Resources.us_Port);
		final SampleProvider us_Distance = us_Sensor.getMode("Distance");
		
		do {
			/* Hear we clear the display, and set up the menu options we want */
			lcd.clear();
			
			lcd.drawString("< Left | Right>", 0, 0);
			lcd.drawString("       |       ", 0, 1);
			lcd.drawString("Run Nav| Run w/", 0, 2);
			lcd.drawString("w/o bb | bb", 0, 3);
			
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		
		/* Begin the Odometer and Odometer Correction threads */
		Thread odoThread = new Thread(odometer);
		odoThread.start();
		Thread odoDisplayThread = new Thread(odometryDisplay);
		odoDisplayThread.start();
		
		/* If the user decides to run obstacle avoidance, this boolean will be set to true for use later on */
		if (buttonChoice == Button.ID_RIGHT) {
			obstacle_Avoidance = true;
		}
		
		/* This new threads starts the navigation thread. The obstacle avoidance boolean variable and Ultrasonic sensor instance is also passed */
		new Thread() {
			public void run() {
				Navigation.navigationControl(positionWaypoints, us_Distance, obstacle_Avoidance);
			}
		}.start();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		
		System.exit(0);
	}

}
