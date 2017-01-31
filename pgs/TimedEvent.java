package pgs;

public class TimedEvent {
	Clickable target;
	long time;
	
	public TimedEvent(Clickable target, long time) {
		this.target = target;
		this.time = time;
	}
	
	public boolean happens(long time) {
		return time > this.time;
	}
	
	public void trigger(Canvas canvas) {
		target.event(canvas);
	}
}
