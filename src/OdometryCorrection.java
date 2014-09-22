import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.Color;

/*******************
 * Group 5
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	
	/**********
	 * Class to contain the correction information */
	private static final class Correction{
		/****
		 * Enum to contain if we are increasing or decreasing the correction */
		public static enum Change{PLUS, MINUS};
		
		// An option of the last value we had
		private Option<Double> last;
		
		// What type of change we last had
		private Option<Change> change;
		
		/**
		 * Instantiate a new correction with values of "none"
		 * for the last value and in which direction we're correcting
		 */
		public Correction(){
			last = Option.none();
			change = Option.none();}

		// Getters and setters for the type of correction and last value (may be "none")
		public Option<Change> getChange() {return change;}

		public void setChange(Change change) {this.change.toSome(change);}
		
		public Option<Double> getLast(){return this.last;}
		
		public void setLast(Double last){this.last.toSome(last);}}
	
	// Correction period and how long to wait before looking for another line
	private static final long CORRECTION_PERIOD = 10, WAIT=500;
	
	// The threshold of the difference in values that constitutes seeing a lien
	private static final int THRESHOLD = 10;
	
	/* The difference in the last value. This is required because the rotation point is
	 * NOT at the color sensor, thus seeing a line doesn't necessarily mean that the
	 * robot is at the same spot as when it saw the line going the other direction */
	private static final double CHANGE = 11;
	
	// Member variables for the correction
	private Odometer odometer;
    private ColorSensor cs;
    private Correction x, y;
    
    // Counter of how many lines we have seen
    public static int counter;
    
    /* The last color we saw (initially "none")
     * Necessary to prevent the display from requiring its own
     * color sensor and static to prevent passing in the odometry correction to the 
     * display*/
    private static Option<Integer> lastColor;
    
    // Whether or not we've disabled the correction (disabled when turning)
    private static boolean isDisabled;
    
    // Initialization of static variables
    static{
    	lastColor = Option.none();
    	counter = 0;
    	isDisabled = false;;}
    
  
	/*******
	 * Instantiate a new OdometryCorrection with the odometer
	 * it is to correct
	 * 
	 * The assumption is made that the 
	 * @param odometer Odometer to correct
	 */
	public OdometryCorrection(Odometer odometer) {
		cs = new ColorSensor(SensorPort.S1);
		this.odometer = odometer;
		counter = 0;
		x = new Correction();
		y = new Correction();}

	// run method (required for Thread)
	public void run() {
		long correctionStart;
		int newColor;
		boolean sawLine;
		
		// The ColorSensor best saw lines with this color
		cs.setFloodlight(Color.GREEN);		
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			newColor = cs.getNormalizedLightValue();
			sawLine = false;
			
			if (lastColor.isNone()) lastColor.toSome(newColor); // Necessary for first one
			
			// Check if we just hit a line (rising edge)
			else if (lastColor.getElse(newColor) - newColor > THRESHOLD && 
						!OdometryCorrection.isDisabled 
						){
				Sound.beep();
				counter++;
				sawLine = true;
				
				// Prevent Odometer and OdometerCorrection from both trying to correct
            	synchronized(this){
            		// Convert from radians to degrees and make sure 0 <= theta < 360            	
            		double theta = Math.toDegrees(odometer.getTheta()) % 360.0;
            		
            		/* Determine if we crossed a line that helps us correct X or helps us correct Y
            		 * and in which way we crossed it (plus or minus).
            		 * 
            		 * In general, we trust the odometer on the first line we see, and from then
            		 * on correct the odometer based on when we hit lines. This algorithm also
            		 * takes into account the fact that the position of the robot will be different
            		 * based on from which direction we hit the line, hence the use of +- CHANGE.
            		 * 
            		 * During testing we achieved values within +- .25 cm of the actual value.
            		 * */
            		if (theta < 45 || theta >= 315){
            			// Correct Y going "up" ("plus")
            			if (y.getLast().isNone()) {
            				y.setLast(odometer.getY());
            				y.setChange(Correction.Change.PLUS);}
            			else{ try{
            				if (y.getChange().get().equals(Correction.Change.PLUS)){
	            				double y = this.y.getLast().get();
	            				y += Odometer.TILE_SIZE;
	            				odometer.setY(y);
	            				this.y.setLast(y);}
            				else{
            					y.setLast(y.getLast().get()-CHANGE);
            					odometer.setY(y.getLast().get());
            					y.setChange(Correction.Change.PLUS);}}
            			 catch(Option.InvalidOption e){}} // Shouldn't ever happen
            		}else if (theta >= 135 && theta < 225){
            			// Correct Y going "down" ("minus")
            			if (y.getLast().isNone()) {
            				y.setLast(odometer.getY());
            				y.setChange(Correction.Change.MINUS);}
            			else{ try{
            				if (y.getChange().get().equals(Correction.Change.MINUS)){
	            				double y = this.y.getLast().get();
	            				y -= Odometer.TILE_SIZE;
	            				odometer.setY(y);
	            				this.y.setLast(y);}
            				else{
            					y.setLast(y.getLast().get()+CHANGE);
            					odometer.setY(y.getLast().get());
            					y.setChange(Correction.Change.MINUS);}} 
            			catch(Option.InvalidOption e){}} // Shouldn't ever happen
            		}else if (theta >=45 && theta < 135){
            			// Correct X going "right" ("plus")
            			if (x.getLast().isNone()) {
            				x.setLast(odometer.getX());
            				x.setChange(Correction.Change.PLUS);}
            			else{ try{
            				if (x.getChange().get().equals(Correction.Change.PLUS)){
	            				double x = this.x.getLast().get();
	            				x += Odometer.TILE_SIZE;
	            				odometer.setX(x);
	            				this.x.setLast(x);}
            				else{
            					x.setLast(x.getLast().get()-CHANGE);
            					odometer.setX(x.getLast().get());
            					x.setChange(Correction.Change.PLUS);}}
            			catch(Option.InvalidOption e){}} // Shouldn't ever happen
            		}else{
            			// Correct X going "left" ("minus")
            			if (x.getLast().isNone()) {
            				x.setLast(odometer.getX());
            				x.setChange(Correction.Change.MINUS);}
            			else{ try{
            				if (x.getChange().get().equals(Correction.Change.MINUS)){
	            				double x = this.x.getLast().get();
	            				x -= Odometer.TILE_SIZE;
	            				odometer.setX(x);
	            				this.x.setLast(x);}
            				else{

            					x.setLast(x.getLast().get()+CHANGE);
            					odometer.setX(x.getLast().get());
            					x.setChange(Correction.Change.MINUS);}}
            			catch(Option.InvalidOption e){}}}}} // Shouldn't ever happen
			lastColor.toSome(newColor);
			
			long diff =  System.currentTimeMillis() - correctionStart;
			if (diff < CORRECTION_PERIOD) {
				try {
					if (sawLine)
						Thread.sleep(WAIT-diff);
					else
						Thread.sleep(CORRECTION_PERIOD - diff);
				} catch (InterruptedException e) {}}}}
	
	/**
	 * Get the current number of lines the robot has crossed
	 * 
	 * @return The number of lines the robot has crossed
	 */
	public int getCounter(){return counter;}
	
	/***
	 * Enable OdometryCorrection */
	public static void enable(){isDisabled = false;}
	
	/****
	 * Disable OdometryCorrection */
	public static void disable(){isDisabled = true;}
	
    /****
     * Get the last color we saw, or -1 if we haven't picked up a color yet
     * @return The integer value of ColorSensor.getNormalizedLightValue()
     * 			or -1 if we haven't seen one yet.
     */
    public static int getLastColor(){
    	return lastColor.getElse(-1);}
}