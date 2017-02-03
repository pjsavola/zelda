package pgs;

import java.awt.Graphics;
import java.awt.Image;

public class PokeStop implements Renderable {

	boolean available = true;
	
	@Override
	public void click(Canvas parent, Trainer trainer) {
		if (available) {
			trainer.collect(parent, this);
			parent.getTimer().addTimedEvent(this, 10);
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
