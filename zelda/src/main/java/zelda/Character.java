package zelda;

import java.awt.*;

public class Character extends GameObject {
    int hp;
    int atk;
    int def;
    int maxHp;
    int speed = 100;
    long priority;
    int chaseTurns;
    Zelda.Creature cre;

    float animOpacity = 0.f;
    float animOpacityDropPerSec = 0.f;

    public Character(Zelda zelda) {
        super(zelda);
    }

    @Override
    public void move(int dx, int dy) {
        final GameObject o = zelda.getObject(x + dx, y + dy);
        if (o instanceof Character) {
            Character c = (Character) o;
            if (atk > c.def) {
                c.hp -= atk - c.def;
                if (c.hp <= 0) {
                    zelda.destroyObject(c);
                } else {
                    zelda.animator.addGlow(c, 1.0f, 2.f);
                }
            }
        } else {
            super.move(dx, dy);
        }
    }

    @Override
    public void paint(Graphics g, int x, int y) {
        if (animOpacity > 0.f) {
            g.drawImage(cre.getImage((int) (animOpacity * 10)), x, y, null);
        } else {
            g.drawImage(cre.getImage(), x, y, null);
        }
        if (hp < maxHp) {
            g.setColor(Color.BLACK);
            g.drawLine(x + 2, y, x + Zelda.tileSize - 3, y);
            g.drawLine(x + 2, y + 1, x + Zelda.tileSize - 3, y + 1);
            if (hp * 4 <= maxHp) g.setColor(Color.RED);
            else if (hp * 2 <= maxHp) g.setColor(Color.ORANGE);
            else if (hp * 4 / 3 <= maxHp) g.setColor(Color.YELLOW);
            else g.setColor(Color.GREEN);
            int width = (hp * (Zelda.tileSize - 5) + maxHp / 2) / maxHp;
            g.drawLine(x + 2, y, x + width, y);
            g.drawLine(x + 2, y + 1, x + width, y + 1);
        }
    }
}
