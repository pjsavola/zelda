package zelda;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

public class GameTimer implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int refreshRate = 10;
	private final List<TimedEvent> eventQueue = new ArrayList<>();
	private final Game game;
	private transient Timer timer;
	private long time = 0;
	private String timeString = timeToString(time);

	public GameTimer(Game game) {
		this.game = game;
	}

	public void initialize() {
		timer = new Timer(refreshRate, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				double spentTime = game.move(refreshRate);
				if (spentTime > 0) {
					advanceTime(spentTime);
				}
			}
		});
		timer.start();		
	}

	// After delay number of game hours, invoke event-method for target.
	// Keep the event queue ordered based on time.
	public void addTimedEvent(Targetable target, double delay) {
		final long eventTime = time + (long) (delay * 3600);
		final TimedEvent event = new TimedEvent(target, eventTime);
		for (int i = 0; i < eventQueue.size(); i++) {
			if (!eventQueue.get(i).happens(eventTime)) {
				eventQueue.add(i, event);
				return;
			}
		}
		eventQueue.add(event);
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

	public void click(Clickable clickable, Trainer trainer) {
		timer.stop();
		clickable.click(game, trainer);
		timer.start();
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
		while (!eventQueue.isEmpty() && eventQueue.get(0).happens(time)) {
			eventQueue.remove(0).trigger(game);
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

	private static class TimedEvent implements Serializable {
		private static final long serialVersionUID = 1L;

		final Targetable target;
		final long time;

		public TimedEvent(Targetable target, long time) {
			this.target = target;
			this.time = time;
		}

		public boolean happens(long time) {
			return time > this.time;
		}

		public void trigger(Game canvas) {
			target.event(canvas);
		}
	}
}
