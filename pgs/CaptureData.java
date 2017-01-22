package pgs;

public class CaptureData {
	private final CaptureResult captureResult;
	private final int captureX;
	private final int captureY;
	private final long captureTime;
	
	public CaptureData(CaptureResult cr, int x, int y, long time) {
		this.captureResult = cr;
		this.captureX = x;
		this.captureY = y;
		this.captureTime = time;
	}
}
