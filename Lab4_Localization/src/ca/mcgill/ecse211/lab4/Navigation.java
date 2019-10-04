package ca.mcgill.ecse211.lab4;
import lejos.hardware.sensor.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;
import lejos.hardware.Button;
import lejos.hardware.lcd.TextLCD;


public class Navigation {

	private static final int FORWARD_SPEED = 250;//250
	  private static final int ROTATE_SPEED = 150; //150
	  public static final double TILE_LENGTH = 30.48;
	  private static final long DISPLAY_PERIOD = 250;
	public static Object lock;
	 static double x;
	 static double y;
	 static double theta;
	 static int leftMotorTachoCount;
	 static int rightMotorTachoCount;
	 static int lastTachoL;
	 static int lastTachoR;
	 static int nowTachoL;
	 static int nowTachoR;
	 static double prevXc = 0;
     static double prevYc = 0;
     int isNavigating = 0;
	 public static TextLCD t = LocalEV3.get().getTextLCD();
	 private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	 
	 public Navigation(Odometer odometer,EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		 this.odometer = odometer;
		 this.leftMotor = leftMotor;
		 this.rightMotor = rightMotor;
}

	  

	   

	public void travelTo (double x, double y){
		double thetaHeading = 0;
	
		double eX = x - odometer.getX();
		double eY = y - odometer.getY();
		double distance = Math.sqrt(Math.pow(eX, 2) + Math.pow(eY, 2) );
		if (eX == 0 && eY > 0){
			thetaHeading = Math.PI/2;
			turnTo(thetaHeading);																																																	
			leftMotor.setSpeed(FORWARD_SPEED);
		    rightMotor.setSpeed(FORWARD_SPEED);
		    leftMotor.forward();
		    rightMotor.forward();
		    leftMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), true);
		    rightMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), false);


		}
		else if (eX == 0 && eY < 0){
			thetaHeading = -Math.PI/2;
			turnTo(thetaHeading);
			leftMotor.setSpeed(FORWARD_SPEED);
		    rightMotor.setSpeed(FORWARD_SPEED);
		    leftMotor.forward();
		    rightMotor.forward();
		    leftMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), true);
		    rightMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), false);
	

		}
		else if (eX > 0){
			thetaHeading = (Math.atan((eY)/(eX)));
			turnTo(thetaHeading);
			leftMotor.setSpeed(FORWARD_SPEED);
		    rightMotor.setSpeed(FORWARD_SPEED);
		    leftMotor.forward();
		    rightMotor.forward();
		    leftMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), true);
		    rightMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), false);
		
		}
		else if (eY == 0 && eX < 0){
			thetaHeading = Math.PI;
			turnTo(thetaHeading);
			leftMotor.setSpeed(FORWARD_SPEED);
		    rightMotor.setSpeed(FORWARD_SPEED);
		    leftMotor.forward();
		    rightMotor.forward();
		    leftMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), true);
		    rightMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), false);
		

		}
		
	
		else if (eX < 0 && eY > 0){
			thetaHeading = (Math.atan((eY)/(eX))) + Math.PI;
			turnTo(thetaHeading);
			leftMotor.setSpeed(FORWARD_SPEED);
		    rightMotor.setSpeed(FORWARD_SPEED);
		    leftMotor.forward();
		    rightMotor.forward();
		    leftMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), true);
		    rightMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), false);
		   
		}
		else if (eX < 0 && eY < 0){
			thetaHeading = (Math.atan((eY)/(eX))) - Math.PI;
			turnTo(thetaHeading);
			leftMotor.setSpeed(FORWARD_SPEED);
		    rightMotor.setSpeed(FORWARD_SPEED);
		    leftMotor.forward();
		    rightMotor.forward();
		    leftMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), true);
		    rightMotor.rotate(convertDistance(LocalizationLab.WHEEL_RADIUS, distance), false);
		    
		}
		



	}

	public void turnTo (double theta){
		double error = theta - odometer.getTheta();
		double minimalAngle;
		if (error >= -(Math.PI) && error <= (Math.PI)){
			minimalAngle = error;
			leftMotor.setSpeed(ROTATE_SPEED);
		      rightMotor.setSpeed(ROTATE_SPEED);
		     //if(error < 0){

		      leftMotor.rotate(convertAngle(LocalizationLab.WHEEL_RADIUS, LocalizationLab.TRACK, (minimalAngle*180/Math.PI)), true); //+
		      rightMotor.rotate(-convertAngle(LocalizationLab.WHEEL_RADIUS, LocalizationLab.TRACK, (minimalAngle*180/Math.PI) ), false);//-
		     // }
		      //else{
		    	//  leftMotor.rotate(convertAngle(WHEEL_RADIUS, TRACK, minimalAngle), true);
			   //   rightMotor.rotate(-convertAngle(WHEEL_RADIUS, TRACK, minimalAngle ), false);

		    //  }

		}
		else if (error < -Math.PI){
			minimalAngle = error + 2*Math.PI;
			leftMotor.setSpeed(ROTATE_SPEED);
		      rightMotor.setSpeed(ROTATE_SPEED);

		      leftMotor.rotate(convertAngle(LocalizationLab.WHEEL_RADIUS, LocalizationLab.TRACK, Math.toDegrees(minimalAngle)), true);
		      rightMotor.rotate(-convertAngle(LocalizationLab.WHEEL_RADIUS, LocalizationLab.TRACK, Math.toDegrees(minimalAngle)), false);
		}
		else if (error > Math.PI){
			minimalAngle = error - 2*Math.PI;
			leftMotor.setSpeed(ROTATE_SPEED);
		      rightMotor.setSpeed(ROTATE_SPEED);

		      leftMotor.rotate(convertAngle(LocalizationLab.WHEEL_RADIUS, LocalizationLab.TRACK, Math.toDegrees(minimalAngle)), true);
		      rightMotor.rotate(-convertAngle(LocalizationLab.WHEEL_RADIUS, LocalizationLab.TRACK, Math.toDegrees(minimalAngle)), false);
		}



	}

	public boolean isNavigating(){
		



		return true;
	}


	  public static int convertDistance(double radius, double distance) {
		    return (int) ((180.0 * distance) / (Math.PI * radius));
		  }

		  public static int convertAngle(double radius, double width, double angle) {
		    return convertDistance(radius, Math.PI * width * angle / 360.0);
		  }

}
