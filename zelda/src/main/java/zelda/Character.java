package zelda;

import java.awt.*;
import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Character extends GameObject {
    public enum Team { FRIENDLY, NEUTRAL, ENEMY }
    Vision los;
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
    boolean pushing;
    float vision = 9.5f;
    Character target;
    Group group;

    float animOpacity = 0.f;
    float animOpacityDropPerSec = 0.f;

    public Character(Zelda zelda, int x, int y) {
        super(zelda, x, y);
        calculateVision();
    }

    @Override
    public boolean blocksProjectiles() {
        return false;
    }

    public boolean canMoveTo(int dx, int dy) {
        if (dx == 0 && dy == 0) return true;

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
        if (pushing) {
            final Terrain terrain = zelda.getTerrain(x + dx, y + dy);
            if (terrain != null && terrain.isPassable()) {
                final GameObject o = zelda.getObject(x + dx, y + dy);
                if (o != null && o.isPushable() && o.canGoTo(x + 2 * dx, y + 2 * dy)) {
                    return o;
                }
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
            free = o == null || o.isPassable();
        }
        return free;
    }

    public void move(int dx, int dy) {
        if (dx == 0 && dy == 0) return;

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
                if (c.group != null) {
                    c.group.remove(c);
                }
                zelda.destroyObject(c);
                if (c == zelda.link) {
                    System.exit(0);
                }
            } else {
                zelda.animator.addGlow(c, 1.0f, 2.f);
            }
        }
    }

    public void calculateVision() {
        final int minX = Math.max(0, x - 1 - (int) vision);
        final int maxX = Math.min(zelda.width, x + 2 + (int) vision);
        final int minY = Math.max(0, y - 1 - (int) vision);
        final int maxY = Math.min(zelda.height, y + 2 + (int) vision);
        los = new Vision(minX, x, maxX, minY, y, maxY, zelda.grid, vision);
        los.calculateFOV();
    }

    public boolean canSee(int x, int y) {
        return los.getLightness(x, y) > 0.f;
    }

    public Character pickTarget() {
        final int minX = Math.max(0, x - 1 - (int) vision);
        final int maxX = Math.min(zelda.width, x + 2 + (int) vision);
        final int minY = Math.max(0, y - 1 - (int) vision);
        final int maxY = Math.min(zelda.height, y + 2 + (int) vision);
        final List<Character> reachableTargets = new ArrayList<>();
        final List<Character> visibleTargets = new ArrayList<>();
        for (int i = minX; i < maxX; ++i) {
            for (int j = minY; j < maxY; ++j) {
                if (canSee(i, j)) {
                    final GameObject o = zelda.getObject(i, j);
                    if (o instanceof Character) {
                        final Character c = (Character) o;
                        if (c.team != team && c.team != Team.NEUTRAL) {
                            if (canHit(c)) {
                                reachableTargets.add(c);
                            } else {
                                visibleTargets.add(c);
                            }
                        }
                    }
                }
            }
        }
        if (chooseTarget(reachableTargets)) {
            chaseTurns = 3;
            return target;
        }
        if (chooseTarget(visibleTargets)) {
            chaseTurns = 3;
            return target;
        }
        if (group != null) {
            final List<Character> groupTargets = group.members().filter(c -> c != this).map(c -> c.target).filter(Objects::nonNull).collect(Collectors.toList());
            if (chooseTarget(groupTargets)) {
                chaseTurns = 3;
                return target;
            }
        }
        if (chaseTurns == 0) {
            target = null;
            return target;
        }
        --chaseTurns;
        return target;
    }

    private boolean chooseTarget(List<Character> targets) {
        if (target != null && targets.contains(target)) {
            return true;
        } else if (!targets.isEmpty()) {
            final List<Character> closestTargets = new ArrayList<>();
            Character c = targets.get(0);
            int dist = Math.max(Math.abs(x - c.x), Math.abs(y - c.y));
            closestTargets.add(c);
            for (int i = 1; i < targets.size(); ++i) {
                c = targets.get(i);
                int newDist = Math.max(Math.abs(x - c.x), Math.abs(y - c.y));
                if (newDist < dist) {
                    closestTargets.clear();
                    closestTargets.add(c);
                    dist = newDist;
                } else if (newDist == dist) {
                    closestTargets.add(c);
                }
            }
            target = closestTargets.get(zelda.r.nextInt(closestTargets.size()));
            return true;
        }
        return false;
    }

    public boolean canHit(Character c) {
        final int dx = x - c.x;
        final int dy = y - c.y;
        final int dist = Math.max(Math.abs(dx), Math.abs(dy));
        if (dist == 1) {
            return true;
        } else if (range * range >= dx * dx + dy * dy) {
            if (canSee(c.x, c.y) && canShoot(c.x, c.y)) {
                return true;
            }
        }
        return false;
    }

    boolean canShoot(int x, int y) {
        int x0 = Util.center(this.x);
        int y0 = Util.center(this.y);
        final int x1 = Util.center(x);
        final int y1 = Util.center(y);
        int dx = Math.abs(x1 - x0);
        int sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0);
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        while (x0 != x1 || y0 != y1) {
            int tx = Util.tile(x0);
            int ty = Util.tile(y0);
            if (zelda.blocksProjectiles(tx, ty)) {
                System.err.println(tx + " " + ty + " blocks projs");
                break;
            } else {
                final GameObject o = zelda.getObject(tx, ty);
                if (o instanceof Character) {
                    final Character c = (Character) o;
                    if (c != this) {
                        return c.x == x && c.y == y;
                    }
                }
            }
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x0 += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y0 += sy;
            }
        }
        return false;
    }

    void refreshArrowTarget(int x, int y) {
        zelda.arrowTarget = null;
        zelda.arrowPath.clear();
        int x0 = Util.center(this.x);
        int y0 = Util.center(this.y);
        final int x1 = Util.center(x);
        final int y1 = Util.center(y);
        int dx = Math.abs(x1 - x0);
        int sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0);
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;
        while (x0 != x1 || y0 != y1) {
            int tx = Util.tile(x0);
            int ty = Util.tile(y0);
            if (zelda.blocksProjectiles(tx, ty)) {
                System.err.println(tx + " " + ty + " blocks projs");
                break;
            } else {
                final GameObject o = zelda.getObject(tx, ty);
                if (o instanceof Character) {
                    final Character c = (Character) o;
                    if (c != this) {
                        zelda.arrowTarget = c;
                        break;
                    }
                }
            }
            zelda.arrowPath.add(new Point(x0, y0));
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x0 += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    private int getRandomRetreatDir(Character target) {
        int dist = Math.max(Math.abs(target.x - x), Math.abs(target.y - y));
        final List<Integer> dirs = new ArrayList<>();
        for (int i = 0; i < 9; ++i) {
            final int x = (i % 3) - 1;
            final int y = (i / 3) - 1;
            if (canMoveTo(x, y)) {
                final int dx2 = target.x - this.x - x;
                final int dy2 = target.y - this.y - y;
                final int dist2 = Math.max(Math.abs(dx2), Math.abs(dy2));
                if (dist2 > dist) {
                    dirs.clear();
                    dist = dist2;
                    dirs.add(i);
                } else if (dist2 == dist) {
                    dirs.add(i);
                }
            }
        }
        if (!dirs.isEmpty()) {
            return dirs.get(zelda.r.nextInt(dirs.size()));
        }
        return 4;
    }

    private int getRandomEngageDir(Character target) {
        int dist = Math.max(Math.abs(target.x - x), Math.abs(target.y - y));
        final List<Integer> dirs = new ArrayList<>();
        for (int i = 0; i < 9; ++i) {
            final int x = (i % 3) - 1;
            final int y = (i / 3) - 1;
            if (canMoveTo(x, y)) {
                final int dx2 = target.x - this.x - x;
                final int dy2 = target.y - this.y - y;
                final int dist2 = Math.max(Math.abs(dx2), Math.abs(dy2));
                if (dist2 < dist) {
                    dirs.clear();
                    dist = dist2;
                    dirs.add(i);
                } else if (dist2 == dist) {
                    dirs.add(i);
                }
            }
        }
        if (!dirs.isEmpty()) {
            return dirs.get(zelda.r.nextInt(dirs.size()));
        }
        return 4;
    }

    private int getRandomDir() {
        final List<Integer> dirs = new ArrayList<>();
        for (int i = 0; i < 9; ++i) {
            final int x = (i % 3) - 1;
            final int y = (i / 3) - 1;
            if (canMoveTo(x, y)) {
                dirs.add(i);
            }
        }
        if (!dirs.isEmpty()) {
            return dirs.get(zelda.r.nextInt(dirs.size()));
        }
        return 4;
    }

    public int pickMove() {
        int dx, dy, dir;
        switch (team) {
            case FRIENDLY:
                if (target == null) {
                    dx = zelda.link.x - x;
                    dy = zelda.link.y - y;
                    if (15 * 15 > dx * dx + dy * dy) {
                        dir = getRandomEngageDir(zelda.link);
                        move((dir % 3) - 1, (dir / 3) - 1);
                        return speed;
                    }
                }
                // Fall through
            case ENEMY:
                if (target == null) return 0;

                dx = target.x - x;
                dy = target.y - y;
                int dist = Math.max(Math.abs(dx), Math.abs(dy));
                if (dist == 1) {
                    move(dx, dy);
                    return speed;
                }
                if (range > 1 && range * range >= dx * dx + dy * dy && canHit(target)) {
                    refreshArrowTarget(target.x, target.y);
                    zelda.arrowIndex = 0;
                    zelda.animator.addArrow(zelda.arrowPath.size() - 1, this);
                    return speed;
                }
                dir = getRandomEngageDir(target);
                move((dir % 3) - 1, (dir / 3) - 1);
                return speed;
            case NEUTRAL:
                if (target == null) return 0;

                dx = target.x - x;
                dy = target.y - y;
                if (5 * 5 > dx * dx + dy * dy) {
                    // Flee
                    dir = getRandomRetreatDir(target);
                    move((dir % 3) - 1, (dir / 3) - 1);
                } else {
                    // Move randomly
                    dir = getRandomDir();
                    move((dir % 3) - 1, (dir / 3) - 1);
                }
                return speed;
        }
        return 0;
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
