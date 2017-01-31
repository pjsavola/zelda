package pgs;

import java.awt.Graphics;

public interface Clickable {
	void click(Canvas canvas, Trainer trainer, long time);
	void event(Canvas canvas);
	boolean isVisible(float light);
	void render(Graphics g, int x, int y);
}
