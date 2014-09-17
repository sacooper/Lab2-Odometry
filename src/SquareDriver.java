import lejos.nxt.*;
/*****************************
 * Group 5
 * @author Scott Cooper - 260503452
 * 
 * A class to move the NXT Robot in a 3 tile x 3 tile square
 */
public class SquareDriver extends Thread {
	
	private static final int 
		/** Wheel speed when moving forwards */
		FORWARD_SPEED = 250,
		/** Wheel speed when rotating*/
		ROTATE_SPEED = 150,
		/** Wheel acceleration */
		ACCELERATION = 1500;
	
	private static final double
		/** Distance to travel (3 tiles) */
		DISTANCE = Odometer.TILE_SIZE * 3,
		/** Angle to turn */
		ANGLE = 90.0;
	
	private NXTRegulatedMotor leftMotor, rightMotor;
	private double leftRadius, rightRadius, width;
	
	/******
	 * Instantiate a new SquareDriver
	 * 
	 * @param leftMotor The motor controlling the left wheel
	 * @param rightMotor The motor controlling the right wheel
	 * @param leftRadius The radius of the left wheel
	 * @param rightRadius The radius of the right wheel
	 * @param width The width of the wheel base
	 */
	public SquareDriver(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor,
			double leftRadius, double rightRadius, double width) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
	}

	/*******
	 * Convert a distance to the number of degrees to turn using the wheel radius
	 * 
	 * @param radius The radius of the wheel thats turning
	 * @param distance The distance to travel
	 * @return The amount the wheel should turn in degrees
	 */
	private static int convertDistance(double radius, double distance) {
		// ( D / R) * (360 / 2PI)
		return (int) ((180.0 * distance) / (Math.PI * radius));}

	/******
	 * Convert an angle to turn to the number of degrees the wheel should turn based on
	 * the radius of the wheel and the width of the wheel base
	 * 
	 * @param radius The wheel radius (cm)
	 * @param width The width of the wheel base (cm)
	 * @param angle The angle to turn (degrees)
	 * @return The amount the wheel should turn in degrees
	 */
	private static int convertAngle(double radius, double width, double angle) {
		//(width * angle / radius ) / (2)
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	/************
	 * Run the SquareDriver
	 */
	@Override
	public void run() {
		// reset the motors
		leftMotor.stop(); leftMotor.setAcceleration(ACCELERATION);
		rightMotor.stop(); rightMotor.setAcceleration(ACCELERATION);

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		/*******
		 * 		Should make this shape (square w. right turns)
		 *     -------->
		 *     ^       |
		 *     |       |
		 *     |       v
		 *     <--------
		 */
		for (int i = 0; i < 4; i++) {
			// drive forward three tiles
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			leftMotor.rotate(convertDistance(leftRadius, DISTANCE), true);
			rightMotor.rotate(convertDistance(rightRadius, DISTANCE), false);

			// turn 90 degrees clockwise
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);

			leftMotor.rotate(convertAngle(leftRadius, width, ANGLE), true);
			rightMotor.rotate(-convertAngle(rightRadius, width, ANGLE), false);
		}
	}
}