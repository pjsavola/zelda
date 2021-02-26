package zelda;

import java.awt.image.BufferedImage;

public enum Feature {
    ROCK("rock"),
    HOLE("hole"),
    TREASURE_CHEST("treasure chest");

    private Feature(String file) {
        image = ImageCache.getImage("images/objects/" + file + ".png");
    }

    public BufferedImage getImage() {
        return image;
    }

    private final BufferedImage image;
}
