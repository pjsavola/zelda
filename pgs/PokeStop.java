package pgs;

import java.awt.Graphics;
import java.awt.Image;

public class PokeStop implements Clickable {

	boolean available = true;
	
	@Override
	public void click(Canvas parent, Trainer trainer, long time) {
		if (available) {
			parent.addEvent(new TimedEvent(this, time + 3600 * 10)); // 10 in game hours
			available = false;
			parent.repaint();
		}
	}

	@Override
	public void event(Canvas parent) {
		available = true;
		parent.repaint();
	}

	@Override
	public boolean isVisible(float light) {
		return light > 0;
	}

	@Override
	public void render(Graphics g, int x, int y) {
		final Image image;
		if (available) {
			image = ImageCache.getImage("images/Pokestop.png");
		} else {
			image = ImageCache.getImage("images/Pokestop taken.png");
		}
		g.drawImage(image, x, y, null);
	}

}
