import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;

public class OdometerDisplay{
	
	public static void print(double x, double y, double theta){
		new Thread(new Runnable(){ public void run(){
			// clear the lines for displaying odometry information
			LCD.drawString("X:                  ", 0, 0);
			LCD.drawString("Y:                  ", 0, 1);
			LCD.drawString("T:                  ", 0, 2);
	
			LCD.drawString(formattedDoubleToString(x, 2), 3, 0);
			LCD.drawString(formattedDoubleToString(y, 2), 3, 1);
			LCD.drawString(formattedDoubleToString(theta, 2), 3, 2);
	
			ColorSensor cs = new ColorSensor(SensorPort.S1);
	
			LCD.drawString("LightVal = " + cs.getNormalizedLightValue(), 0, 3);
			LCD.drawString("#lines:" + Integer.toString(OdometryCorrection.counter),0, 4);
			}}).start();}

	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;

		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";

		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long) x;
			if (t < 0)
				t = -t;

			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;}
			result += stack;
		}

		// put the decimal, if needed
		if (places > 0) {
			result += ".";

			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = 10.0 * (x - Math.floor(x));
				result += Long.toString((long) x);}}
		return result;
	}

	public static void printMainMenu() {
		// clear the display
		LCD.clear();

		// ask the user whether the motors should drive in a square or float
		LCD.drawString("< Left | Right >", 0, 0);
		LCD.drawString("       |        ", 0, 1);
		LCD.drawString(" Float | Drive  ", 0, 2);
		LCD.drawString("motors | in a   ", 0, 3);
		LCD.drawString("       | square ", 0, 4);
	}

}
