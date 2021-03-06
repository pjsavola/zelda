package zelda;

import java.awt.*;

public class Character extends GameObject {
    public enum Team { FRIENDLY, NEUTRAL, ENEMY }
    int hp;
    int atk;
    int def;
    int maxHp;
    int range = 1;
    int speed = 100;
    long priority;
    int chaseTurns;
    Zelda.Creature cre;
    Team team = Team.ENEMY;
    boolean flying;
    boolean jumping;
    boolean swimming;

    float animOpacity = 0.f;
    float animOpacityDropPerSec = 0.f;

    public Character(Zelda zelda) {
        super(zelda);
    }

    public boolean canMoveTo(int dx, int dy) {
        final boolean moveAllowed = canGoTo(x + dx, y + dy);
        if (!moveAllowed) {
            if (canPushTo(dx, dy) != null) {
                return true;
            }
            if (canJumpOver(dx, dy)) {
                return true;
            }
        }
        return moveAllowed;
    }

    private GameObject canPushTo(int dx, int dy) {
        final Terrain terrain = zelda.getTerrain(x + dx, y + dy);
        if (terrain != null && terrain.isPassable()) {
            final GameObject o = zelda.getObject(x + dx, y + dy);
            if (o != null && o.isPushable() && o.canGoTo(x + 2 * dx, y + 2 * dy)) {
                return o;
            }
        }
        return null;
    }

    private boolean canJumpOver(int dx, int dy) {
        final Terrain terrain = zelda.getTerrain(x + dx, y + dy);
        if (terrain != null && terrain.isJumpable()) {
            return canGoTo(x + 2 * dx, y + 2 * dy);
        }
        final GameObject o = zelda.getObject(x + dx, y + dy);
        if (o != null && o.isJumpable()) {
            return canGoTo(x + 2 * dx, y + 2 * dy);
        }
        return false;
    }

    @Override
    public boolean canGoTo(int x, int y) {
        final Terrain terrain = zelda.getTerrain(x, y);
        boolean free = terrain != null && (terrain.isPassable() || (flying && !terrain.blocksFlying()) || (swimming && terrain.allowsSwimming()));
        if (free) {
            final GameObject o = zelda.getObject(x, y);
            free = o == null || (o.isPassable() || (flying && !o.blocksFlying()));
        }
        return free;
    }

    public void move(int dx, int dy) {
        final GameObject o = zelda.getObject(x + dx, y + dy);
        if (o instanceof Character) {
            final Character c = (Character) o;
            if (c.team == team) {
                if (c.canGoTo(x, y) && canGoTo(x + dx, y + dy)) {
                    zelda.swapObjects(this, c);
                }
            } else {
                hit(c);
            }
        } else if (canGoTo(x + dx, y + dy)) {
            zelda.moveObject(this, x + dx, y + dy);
        } else {
            final GameObject pushTarget = canPushTo(dx, dy);
            if (pushTarget != null) {
                zelda.moveObject(pushTarget, x + 2 * dx, y + 2 * dy);
                zelda.moveObject(this, x + dx, y + dy);
            } else if (canJumpOver(dx, dy)) {
                zelda.moveObject(this, x + 2 * dx, y + 2 * dy);
            }
        }
    }

    public void hit(Character c) {
        if (atk > c.def) {
            System.err.println(c.cre + " lost " + (atk - c.def) + " HP");
            c.hp -= atk - c.def;
            if (c.hp <= 0) {
                zelda.destroyObject(c);
                if (c == zelda.link) {
                    System.exit(0);
                }
            } else {
                zelda.animator.addGlow(c, 1.0f, 2.f);
            }
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
