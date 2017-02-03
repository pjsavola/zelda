package pgs;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.Timer;

public class GameTimer {
	private static final int refreshRate = 20;
	private final Deque<TimedEvent> eventQueue = new ArrayDeque<TimedEvent>();
	private final Canvas game;
	private final Timer timer;
	private long time = 0;
	private String timeString = timeToString(time);

	public GameTimer(Canvas game) {
		this.game = game;
		timer = new Timer(refreshRate, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				double spentTime = game.move();
				if (spentTime > 0) {
					advanceTime(spentTime);
				}
			}
		});
		timer.start();
	}

	// After delay number of game hours, invoke event-method for target.
	public void addTimedEvent(Targetable target, double delay) {
		eventQueue.addLast(new TimedEvent(target, time + (long) (delay * 3600)));
	}

	// Return the game time in milliseconds.
	public long getTime() {
		return time;
	}

	public double getHoursFromMidnight() {
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

	public void paint(Graphics g) {
		g.drawString("Time: " + timeString, Simulator.timeArea.x, Simulator.timeArea.y + 15);
	}

	// Advance game time, trigger any events and trigger repaint for required areas.
	private void advanceTime(double spentTime) {
		time += spentTime * refreshRate;

		// Update the time string and repaint it if needed
		final String newTimeString = timeToString(time);
		if (!timeString.equals(newTimeString)) {
			timeString = newTimeString;
			game.repaint(Simulator.headerArea);
		}

		// Check for any timed events and trigger them
		while (!eventQueue.isEmpty() && eventQueue.peekFirst().happens(time)) {
			eventQueue.removeFirst().trigger(game);
		}

		game.repaint(Simulator.mainArea);
	}

	private static String timeToString(long time) {
		long gameTime = time * 1000;
		long secs = gameTime / 1000;
		long mins = secs / 60;
		long hours = mins / 60;
		mins = mins % 60;
		long h = (hours + 12) % 24;
		String extra0 = h < 10 ? "0" : "";
		String extra1 = mins < 10 ? "0" : "";
		return extra0 + h + ":" + extra1 + mins;
	}

	private static class TimedEvent {
		final Targetable target;
		final long time;

		public TimedEvent(Targetable target, long time) {
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
}
