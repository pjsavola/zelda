package zelda;

import java.awt.*;

public class GameObject {
    protected Zelda zelda;
    protected int x;
    protected int y;

    public GameObject(Zelda zelda) {
        this.zelda = zelda;
    }

    public void move(int dx, int dy) {
        final int tx = x + dx;
        final int ty = y + dy;
        if (zelda.canMoveTo(tx, ty)) {
            zelda.moveObject(this, tx, ty);
        } else {
            GameObject o = zelda.getObject(tx, ty);
            if (o != null && o.isPushable()) {
                if (zelda.canMoveTo(x + 2 * dx, y + 2 * dy)) {
                    zelda.moveObject(o, x + 2 * dx, y + 2 * dy);
                    zelda.moveObject(this, tx, ty);
                }
            }
        }
    }

    public boolean isPushable() {
        return false;
    }

    public boolean isPassable() {
        return false;
    }

    public void paint(Graphics g, int x, int y) {
    }
}
