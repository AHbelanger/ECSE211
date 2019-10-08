package ca.mcgill.ecse211.lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;
import static ca.mcgill.ecse211.lab4.Resources.*;

public class LightLocalizer extends Thread {
	
	private SampleProvider colorSensor;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private float [] csData;
	private double centerToSensor = 15;
	private double distBack = 15;

	
	public LightLocalizer (SampleProvider sensor, Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, float[] csData){
		this.colorSensor = sensor;
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.csData = csData;
	}
	
	
	public void run(){
		while(true){
		leftMotor.setSpeed(ROTATE_SPEED);
	    rightMotor.setSpeed(ROTATE_SPEED);

	    leftMotor.rotate(-Navigation.convertAngle(WHEEL_RAD, TRACK, 360),true);
	    rightMotor.rotate(Navigation.convertAngle(WHEEL_RAD, TRACK, 360), true);
	    
	    int lineCounter = 0;
		double angleY = 0;
		double angleX = 0;
		double X = 0;
		double Y = 0;
		double Y1 = 0;
		double Y2 = 0;
		double X1 = 0;
		double X2 = 0;
		double dTheta = 0;
		double[] angleArray = new double[5];
		
		while (getSensor() > 70) {
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);
			rightMotor.forward();
			leftMotor.forward();
		}
		
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.rotate(Navigation.convertDistance(WHEEL_RAD, distBack), true);
		rightMotor.rotate(Navigation.convertDistance(WHEEL_RAD, distBack), false);
		
		while(lineCounter < 4 ) {
			leftMotor.setSpeed(ROTATE_SPEED);
		    rightMotor.setSpeed(ROTATE_SPEED);
			rightMotor.forward();
			leftMotor.backward();
			if (getSensor() < 30 ) {
				angleArray[lineCounter] = odometer.getTheta();
				lineCounter++;
				Sound.beep();
			
		   }
		}
		leftMotor.stop(true);
		rightMotor.stop(false);
		Y1 = angleArray[0];
		X1 = angleArray[1];
		Y2 = angleArray[2];
		X2 = angleArray[3];
		X = -centerToSensor *Math.cos((Math.toRadians(Y2-Y1)/2));
		Y = -centerToSensor *Math.cos((Math.toRadians(X2-X1)/2));
		angleX = 270 + (angleArray[1]/2) - angleArray[3]; //Might have to remove this and next 2 lines
		angleY = 180 - (angleArray[2]/2) - angleArray[0];
		dTheta = (angleX + angleY)/2 ;
		
	    odometer.setX(X);
	    odometer.setY(Y);
	    break;
	    
		}
	}
	public float getSensor() {
		colorSensor.fetchSample(csData, 0);
		float sensorValue = csData[0]*100;
		
		if (sensorValue > 30) {
			sensorValue = 30;
		}
		else if (sensorValue < 30) {
			sensorValue = 10;
		}
		return sensorValue;
		}

	
	
			
		
		
		
	}
	


