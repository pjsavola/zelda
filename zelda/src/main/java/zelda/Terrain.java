package zelda;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

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
        try {
            darkImage = ImageCache.toCompatibleImage(ImageIO.read(new File(Zelda.resourcePath + "images/terrain/" + file + ".png")));
            for (int x = 0; x < Zelda.tileSize; ++x) {
                for (int y = 0; y < Zelda.tileSize; ++y) {
                    int rgb = darkImage.getRGB(x, y);
                    if (rgb == 0) continue;
                    int r = (rgb & 0x00ff0000) >> 16;
                    int g = (rgb & 0x0000ff00) >> 8;
                    int b = rgb & 0x000000ff;
                    int w = 50;
                    int r2 = r * 255 / (w + 255);
                    int g2 = g * 255 / (w + 255);
                    int b2 = b * 255 / (w + 255);
                    int rgb2 = (255 << 24) + (r2 << 16) + (g2 << 8) + b2;
                    darkImage.setRGB(x, y, rgb2);
                }
            }
        } catch (IOException e) {
            darkImage = null;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getDarkImage() {
        return darkImage;
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
    private BufferedImage darkImage;
    private final int mask;
    private final boolean passable;
    private final boolean blocksFlying;
    private final boolean blocksProjectiles;
    private final float opacity;
}
