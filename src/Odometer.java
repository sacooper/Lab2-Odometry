import lejos.nxt.*;

/********************
 * Group 5
 * @author Scott Cooper - 260503452
 * @author Liqiang Ding - 260457392
 */
public class Odometer extends Thread {		
	// odometer update period, in ms
	private static final int ODOMETER_PERIOD = 25;

	/* Wheel based and wheel radius passed in during initialization
	 * - Changed to this method to allow for only changing the parameters
	 * 	 once */
	private final double 		WHEEL_BASE,
		WHEEL_RADIUS;
	
	// Tile size (difference between lines
	protected static final double TILE_SIZE = 30.48;
	
	/**X, coordinate, Y coordinate, and how much robot has rotated */
	private double x, y, theta;
	
	/*****
	 * Instantiate a new odometer. 
	 * 
	 * @param wheelBase The wheelbase of the robot
	 * @param radius The wheel radius of the robot
	 */
	public Odometer(double wheelBase, double radius) {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		this.WHEEL_BASE = wheelBase;
		this.WHEEL_RADIUS = radius;
		
		
		Lab2.LEFT_MOTOR.resetTachoCount();
		Lab2.RIGHT_MOTOR.resetTachoCount();
		
	    LCD.clear();
	    LCD.drawString("Odometer Demo",0,0,false);
	    LCD.drawString("Current X  ",0,4,false);
	    LCD.drawString("Current Y  ",0,5,false);
	    LCD.drawString("Current T  ",0,6,false);
	}

	/***
	 * Run the odometer
	 */
	public void run() {
		long previousTachoL = 0,	// Tacho L at last sample
			 previousTachoR = 0,	// Tacho R at last sample
			 currentTachoL = 0,		// Current tacho L
			 currentTachoR = 0;		// Current tacho R
		
		// Constant used in calculating distance save calculation be declaring once
		final double PI_R_180 = Math.PI * WHEEL_RADIUS / 180.0;
		for(int i = 0; true; i++) {
			long updateStart = System.currentTimeMillis();
			
			double leftDistance, rightDistance, deltaDistance, deltaTheta, dX, dY;
			
			// Get current tacho count
			currentTachoL = Lab2.LEFT_MOTOR.getTachoCount();
			currentTachoR = Lab2.RIGHT_MOTOR.getTachoCount();
			
			// Calculate left and right distances based on change in tachometer
			leftDistance = PI_R_180 * (currentTachoL - previousTachoL);
			rightDistance = PI_R_180 * (currentTachoR - previousTachoR);
			
			previousTachoL = currentTachoL;
			previousTachoR = currentTachoR;
			
			// Calculate change in distance and theta based on distance traveled
			deltaDistance = 0.5 * (leftDistance + rightDistance);
			deltaTheta = (leftDistance - rightDistance) / WHEEL_BASE;

			synchronized (this) {
				// don't use the variables x, y, or theta anywhere but here
				theta += deltaTheta;
				
				dX = deltaDistance * Math.sin(theta);
				dY = deltaDistance * Math.cos(theta);
				
				x += dX;
				y += dY;}

			// Update display every 10 iterations of the odometer
			if (i%10==0) OdometerDisplay.print(x,  y,  theta);
			
			// this ensures that the odometer only runs once every period
			long diff = System.currentTimeMillis() - updateStart;
			
			if (diff < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - diff);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	// Getters and Setters for the parameters
	
	synchronized public void setX(double x) {this.x = x;}
	
	synchronized public double getX() {return x;}

	synchronized public void setY(double y) {this.y = y;}
	
	synchronized public double getY() {return y;}

	synchronized public void setTheta(double theta) {this.theta = theta;}
	
	synchronized public double getTheta() {return theta;}
}