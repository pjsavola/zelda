package zelda;

import java.awt.*;

public class GameObject implements TileFeature {
    protected Zelda zelda;
    protected int x;
    protected int y;

    public GameObject(Zelda zelda) {
        this.zelda = zelda;
    }

    public boolean canGoTo(int x, int y) {
        final Terrain terrain = zelda.getTerrain(x, y);
        boolean free = terrain != null && terrain.isPassable();
        if (free) {
            final GameObject o = zelda.getObject(x, y);
            free = o == null || o.isPassable();
        }
        return free;
    }

    @Override
    public boolean blocksProjectiles() {
        return true;
    }

    public void paint(Graphics g, int x, int y) {
    }
}
