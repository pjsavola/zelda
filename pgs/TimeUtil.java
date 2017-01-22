package pgs;

public abstract class TimeUtil {
	public static double getHoursFromMidnight(long time) {
		long gameTime = time * 1000;
		long secs = gameTime / 1000;
		long mins = secs / 60;
		long hours = mins / 60;
		long h = (hours + 12) % 24;
		final double result;
		if (h >= 12) {
			result = 24 - h - mins % 60 / 60.0;
		} else {
			result = h + mins % 60 / 60.0;
		}
		return result;
	}
	
	public static String timeToString(long time) {
		long gameTime = time * 1000;
		long secs = gameTime / 1000;
		long mins = secs / 60;
		long hours = mins / 60;
		mins = mins % 60;
		long h = (hours + 12) % 24;
		String extra0 = mins < 10 ? "0" : "";
		return h + ":" + extra0 + mins;
	}
}
