package zelda;

import java.awt.image.BufferedImage;

public enum Terrain {
    GRASS("grass", 0xff4cff00, true, 0.f),
    TALL_GRASS("tall grass", 0xff36b500, true, 0.1f),
    SAND("sand", 0xffffe97f, true, 0.f),
    WALL("wall", 0xff60482b, false, 1.f),
    WATER("water", 0xff0026ff, false, 0.f),
    SNOW("snow", 0xffffffff, true, 0.f),
    SOLID_ROCK("solid rock", 0xff808080, true, 0.f),
    LAVA("lava", 0xffff0000, false, 0.f);

    Terrain(String file, int mask, boolean passable, float opacity) {
        image = ImageCache.getTerrainImage(file);
        this.mask = mask;
        this.passable = passable;
        this.opacity = opacity;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getMask() {
        return mask;
    }

    public boolean isPassable() {
        return passable;
    }

    public float getOpacity() {
        return opacity;
    }

    private final BufferedImage image;
    private final int mask;
    private final boolean passable;
    private final float opacity;
}
