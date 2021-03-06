package zelda;

import java.awt.image.BufferedImage;

public enum Terrain implements TileFeature {
    GRASS("grass", 0xff4cff00, true, false, false, 0.f),
    TALL_GRASS("tall grass", 0xff36b500, true, false, false, 0.1f),
    SAND("sand", 0xffffe97f, true, false, false, 0.f),
    WALL("wall", 0xff60482b, false, true, true, 1.f),
    WATER("water", 0xff0026ff, false, false, false, 0.f),
    SNOW("snow", 0xffffffff, true, false, false, 0.f),
    SOLID_ROCK("solid rock", 0xff808080, true, false, false, 0.f),
    LAVA("lava", 0xffff0000, false, false, false, 0.f);

    Terrain(String file, int mask, boolean passable, boolean blocksFlying, boolean blocksProjectiles, float opacity) {
        image = ImageCache.getTerrainImage(file);
        this.mask = mask;
        this.passable = passable;
        this.blocksFlying = blocksFlying;
        this.blocksProjectiles = blocksProjectiles;
        this.opacity = opacity;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getMask() {
        return mask;
    }

    @Override
    public boolean isPassable() {
        return passable;
    }

    public boolean allowsSwimming() {
        return this == WATER;
    }

    @Override
    public boolean blocksFlying() {
        return blocksFlying;
    }

    @Override
    public boolean blocksProjectiles() {
        return blocksProjectiles;
    }

    public float getOpacity() {
        return opacity;
    }

    private final BufferedImage image;
    private final int mask;
    private final boolean passable;
    private final boolean blocksFlying;
    private final boolean blocksProjectiles;
    private final float opacity;
}
