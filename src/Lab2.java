import lejos.nxt.*;

/*******************
 * Group 5
 * @author Scott Cooper - 260503452
 */
public class Lab2 {
	private static final int
		SQUARE = Button.ID_RIGHT,
		FLOAT = Button.ID_LEFT;
	
	private static final double
		RADIUSL = 2.16,
		RADIUSR = 2.16,
		WIDTH = 15.5;
	
	public static void main(String[] args) {
		// some objects that need to be instantiated
		Odometer odometer = new Odometer();
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
		
		int option = 0;
		OdometerDisplay.printMainMenu();
		// Wait for button press
		while (option != FLOAT && option != SQUARE) option = Button.waitForAnyPress();	

		switch(option){
		case FLOAT:
			for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.B, Motor.C }) {
//				motor.forward();
				motor.flt();}
			
			// start only the odometer and the odometry display
			odometer.start();
			break;
		case SQUARE:
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			odometer.start();
			odometryCorrection.start();

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			new SquareDriver(Motor.A, Motor.B, RADIUSL, RADIUSR, WIDTH).start();
		default:
			LCD.drawString("Error - Invalid Button", 0, 1);
			System.exit(-1);
			break;}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}