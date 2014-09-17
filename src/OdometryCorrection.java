import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

/*******************
 * Group 5
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {

	private static final long CORRECTION_PERIOD = 10;
	private static final int LINE_VALUE = 280, THRESHOLD = 100;
	private Odometer odometer;
    private ColorSensor cs = new ColorSensor(SensorPort.S1);
    private Option<Double> lastX, lastY;
    public static int counter;
    private Option<Integer> lastColor;
	/*******
	 * Instantiate a new OdometryCorrection with the odometer
	 * it is to correct
	 * 
	 * The assumption is made that the 
	 * @param odometer
	 */
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		counter = 0;
		this.lastX = Option.none();
		this.lastY = Option.none();
		this.lastColor = Option.none();}

	// run method (required for Thread)
	public void run() {
		long correctionStart;
		while (true) {
			correctionStart = System.currentTimeMillis();
			int newColor = cs.getNormalizedLightValue();
//            if(cs.getNormalizedLightValue() < LINE_VALUE){	//On a line (basic)
			if (lastColor.isNone()) lastColor.toSome(newColor); // Check if we just hit a line (rising edge)
			else if (newColor - lastColor.getElseNull() < THRESHOLD){
				lastColor.toSome(newColor);
            	synchronized(this){
            		// Make sure 0 <= theta < 360            		
            		double theta = odometer.getTheta() % 360; 
            		
            		// Determine if we crossed a line that helps us correct X or helps us correct Y
            		if (theta < 45 || theta >= 315){
            			// Correct Y going "up"
            			if (lastY.isNone()) lastY.toSome(odometer.getY());
            			else{ try{
            				double y = lastY.get();
            				y += Odometer.TILE_SIZE;
            				odometer.setY(y);
            				lastY.toSome(y);}
            			 catch(Option.InvalidOption e){}} // Shouldn't ever happen
            		}else if (theta >= 135 && theta < 225){
            			// Correct Y going "down"
            			if (lastY.isNone()) lastY.toSome(odometer.getY());
            			else{ try{
            				double y = lastY.get();
            				y -= Odometer.TILE_SIZE;
            				odometer.setY(y);
            				lastY.toSome(y);} 
            			catch(Option.InvalidOption e){}} // Shouldn't ever happen
            		}else if (theta >=45 && theta < 135){
            			// Correct X going "right"
            			if (lastX.isNone()) lastX.toSome(odometer.getX());
            			else{ try{
            				double x = lastX.get();
            				x += Odometer.TILE_SIZE;
            				odometer.setX(x);
            				lastY.toSome(x);} 
            			catch(Option.InvalidOption e){}} // Shouldn't ever happen
            		}else{
            			// Correct X going "left"
            			if (lastX.isNone()) lastX.toSome(odometer.getX());
            			else{ try{
            				double x = lastX.get();
            				x += Odometer.TILE_SIZE;
            				odometer.setX(x);
            				lastY.toSome(x);} catch(Option.InvalidOption e){}} // Shouldn't ever happen
            		}}}

			// this ensure the odometry correction occurs only once every period
			long diff =  System.currentTimeMillis() - correctionStart;
			if (diff < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD - diff);
				} catch (InterruptedException e) {}}}}
	
	public int getCounter(){return counter;}
}