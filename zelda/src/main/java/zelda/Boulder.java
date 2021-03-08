package zelda;

import java.awt.*;

public class Boulder extends GameObject {
    Feature feature;

    public Boulder(Zelda zelda, int x, int y) {
        super(zelda, x, y);
    }
    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void paint(Graphics g, int x, int y) {
g.drawImage(feature.getImage(), x, y, null);
}
}
