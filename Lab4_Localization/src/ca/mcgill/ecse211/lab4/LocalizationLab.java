package ca.mcgill.ecse211.lab4;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.Button;
import static ca.mcgill.ecse211.lab4.Resources.*;

public class LocalizationLab{

	  public static void main(String[] args) {
		  
		  SensorModes usSensor = new EV3UltrasonicSensor(usPort); 
		  SampleProvider usDistance = usSensor.getMode("Distance"); 
		  
		  EV3ColorSensor colorSensor = new EV3ColorSensor (lightPort);
		  SampleProvider colorProvider = colorSensor. getMode("Red");
		  
		  float[] csData = new float[colorProvider.sampleSize()]; // csData is the buffer in which data are

		  final TextLCD t = LocalEV3.get().getTextLCD();
		  t.drawString("Left = Rising Edge", 0, 0);
		  t.drawString("Right = Falling Edge", 0, 1);
		  int option = Button.waitForAnyPress();
		  t.clear();
		  
		  Odometer odometer = new Odometer(leftMotor, rightMotor);
		  Navigation navigation = new Navigation(odometer, leftMotor, rightMotor);
		  OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, t);
		  
		  odometer.start();
	      odometryDisplay.start();
	      
	      UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer (usDistance, odometer, leftMotor, rightMotor);
	      LightLocalizer lightLocalizer = new LightLocalizer (colorProvider, odometer, leftMotor, rightMotor, csData); //colorSensor
	      
	      
	      if(option == Button.ID_RIGHT){
	      usLocalizer.fallingEdge();
	      navigation.turnTo(0);
	      
	      }
	      else{
	    	 usLocalizer.risingEdge();
	    	 navigation.turnTo(0);
	    	 //lightLocalizer.start();
	      }
	      t.clear();
	      t.drawString("Press any button", 0, 0);
		  t.drawString("for LightLocalizer", 0, 1);
		  
		  Button.waitForAnyPress();
		  t.clear();
		  navigation.turnTo(Math.PI/4);
		  lightLocalizer.start();
		  navigation.travelTo(0, 0);
		  navigation.turnTo(0);
	      
	      while (Button.waitForAnyPress() != Button.ID_ESCAPE);
	      System.exit(0);
		  
		  
		  
	  }
}
