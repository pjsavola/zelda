package zelda.tmp;

import java.awt.Graphics;

public interface Renderable extends Clickable, Targetable {
	boolean isVisible(float light);
	void render(Graphics g, int x, int y);
	int getAlphaMask();
}
