package ca.mcgill.ecse211.lab4;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;



public class UltrasonicLocalizer {
	
	private SampleProvider usSensor;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distanceThreshold = 60;
	private int noiseFilter = 20;
	
	
	
	
	public UltrasonicLocalizer (SampleProvider sensor, Odometer odometer, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor){
		this.usSensor = sensor;
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	public void fallingEdge(){
		double alpha = 0;
		double beta = 0;
		double deltaTheta = 0;
		
		while (getDistance() < distanceThreshold) {
			rotateClockwise();
			}

		while (getDistance() > distanceThreshold - noiseFilter) {
			rotateClockwise();
			}
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			alpha = odometer.getTheta() * 180/Math.PI;
			Sound.beep();
			
	    while(getDistance() < distanceThreshold) {
			rotateCounterClockwise();
			}

	    while (getDistance() > distanceThreshold - noiseFilter) {
			rotateCounterClockwise();
			}
			leftMotor.stop(true);
			rightMotor.stop(false);
			beta = odometer.getTheta() * 180/Math.PI;
			Sound.beep();
			leftMotor.stop(true);
		    rightMotor.stop(true);
		
			deltaTheta = headingToBe(alpha, beta);
			Sound.beepSequence();
			
			odometer.setTheta(odometer.getTheta() + Math.toRadians(deltaTheta));
			
			
		
	}
	
	public void risingEdge(){
		double alpha = 0;
		double beta = 0;
		double deltaTheta = 0;
		while (getDistance() > noiseFilter) {
			rotateCounterClockwise();
			}
			Sound.buzz();
		
		while (getDistance() < distanceThreshold) {
			rotateCounterClockwise();
			}
			leftMotor.stop(true);
			rightMotor.stop(false);
			alpha = odometer.getTheta() * 180/Math.PI;
			Sound.beep();	
		
		while(getDistance() > noiseFilter) {
			rotateClockwise();
			}
			Sound.buzz();
		
		while (getDistance() < distanceThreshold) {
			rotateClockwise();
			}
			leftMotor.stop(true);
			rightMotor.stop(false);
			beta = odometer.getTheta() * 180/Math.PI;
			Sound.beep();
			
		deltaTheta = headingToBe(alpha, beta);
		Sound.beepSequence();
		leftMotor.stop(true);
	    rightMotor.stop(true);
		odometer.setTheta(odometer.getTheta() + Math.toRadians(deltaTheta));
		
	}
	

	
	public float getDistance() {
		float[] usData = new float[usSensor.sampleSize()];
		usSensor.fetchSample(usData, 0);
		float distance = usData[0]*100;
		
		if (distance > distanceThreshold) {
			distance = distanceThreshold;
		}
				
		return distance;
	}
	
	public void rotateClockwise() {
		leftMotor.setSpeed(LocalizationLab.ROTATION_SPEED);
		rightMotor.setSpeed(LocalizationLab.ROTATION_SPEED);
		leftMotor.backward();
		rightMotor.forward();
	}

	public void rotateCounterClockwise() {
		leftMotor.setSpeed(LocalizationLab.ROTATION_SPEED);
		rightMotor.setSpeed(LocalizationLab.ROTATION_SPEED);
		leftMotor.forward();
		rightMotor.backward();
	}
	
	public double headingToBe(double alpha, double beta) {
		double deltaTheta = 0;
		if (alpha < beta) {
			deltaTheta = 45 - ((alpha + beta)/2);
		}
		else {
			deltaTheta = 225 - ((alpha + beta)/2);
		}
		return deltaTheta;
	}
	
	}
	

		
	


