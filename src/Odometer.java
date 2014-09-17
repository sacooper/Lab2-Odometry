import lejos.nxt.*;

/********************
 * Group 5
 * @author Scott Cooper - 260503452
 */
public class Odometer extends Thread {	
	private static final NXTRegulatedMotor 
		leftMotor = Motor.A,
		rightMotor = Motor.C;
	
	// odometer update period, in ms
	private static final int ODOMETER_PERIOD = 25;

	private static final double 		WHEEL_BASE = 15,
		WHEEL_RADIUS = 2.16;
	
	protected static final double TILE_SIZE = 30.48;
	
	private long 
		// Tacho L at last sample
		previousTachoL,
		// Tacho R at last sample
		previousTachoR,
		// Current tacho L
		currentTachoL,
        // Current tacho R
		currentTachoR;
	
	/**X, coordinate, Y coordinate, and how much robot has rotated */
	private double x, y, theta;
	
	/*****
	 * Instantiate a new odometer. 
	 */
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		
		
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();
		
		previousTachoL = previousTachoR = currentTachoL = currentTachoR = 0;
		
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
		for(int i = 0; true; i++) {
			long updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
			double leftDistance, rightDistance, deltaDistance, deltaTheta, dX, dY;
			currentTachoL = leftMotor.getTachoCount();
			currentTachoR = rightMotor.getTachoCount();
			
			final double PI_R_180 = Math.PI * WHEEL_RADIUS / 180.;
			leftDistance = PI_R_180 * (currentTachoL - previousTachoL);
			rightDistance = PI_R_180 * (currentTachoR - previousTachoR);
			
			previousTachoL = currentTachoL;
			previousTachoR = currentTachoR;
			
			deltaDistance = 0.5 * (leftDistance + rightDistance);
			deltaTheta = (leftDistance - rightDistance) / WHEEL_BASE;

			synchronized (this) {
				// don't use the variables x, y, or theta anywhere but here!
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