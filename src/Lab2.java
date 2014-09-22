import lejos.nxt.*;

/*******************
 * Group 5
 * @author Scott Cooper - 260503452
 * @author Liqiang Ding - 260457392
 * <br>
 * Entry point for Lab 2. The user either chooses to float the motors,
 * to test the odometry (this occurs without correction), or drives the
 * robot in a 3 tile x 3 tile square.
 */
public class Lab2 {
	
	// Better names for buttons
	private static final int
		SQUARE = Button.ID_RIGHT,
		FLOAT = Button.ID_LEFT;
	
	// Wheel radius and wheel base (wheel radii where equal)
	private static final double
		RADIUS = 2.13,			
		WIDTH = 16.;			
	
	// Motors. Declared ONLY here to prevent needing to change it in multiple places
	public static final NXTRegulatedMotor
		LEFT_MOTOR = Motor.A,
		RIGHT_MOTOR = Motor.C;
	
	public static void main(String[] args) {
		Odometer odometer = new Odometer(WIDTH, RADIUS);
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
		
		int option = 0;
		OdometerDisplay.printMainMenu();
		// Wait for button press
		while (option != FLOAT && option != SQUARE) option = Button.waitForAnyPress();	

		switch(option){
		case FLOAT:
			for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.B, Motor.C }) {
				motor.forward();
				motor.flt();}
			
			// start only the odometer (and the odometry display)
			odometer.start();
			break;
		case SQUARE:
			// start the odometer, the odometeryCorrection, (and the odemetry display)
			odometer.start();
			odometryCorrection.start();

			// create a new squareDriver and start it
			new SquareDriver(LEFT_MOTOR, RIGHT_MOTOR, RADIUS, RADIUS, WIDTH).start();
			break;
		default:
			LCD.drawString("Error - Invalid Button", 0, 1);
			System.exit(-1);
			break;}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}