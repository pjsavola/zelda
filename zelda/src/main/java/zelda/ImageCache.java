package zelda;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public abstract class ImageCache {
	private static Map<String, Icon> iconCache = new HashMap<>();
	private static Map<String, BufferedImage> imageCache = new HashMap<>();
	private static Map<List<String>, BufferedImage> layeredImageCache = new HashMap<>();
	private static Map<Integer, BufferedImage> darkOverlayCache = new HashMap<>();

	public static Icon getTerrainIcon(String name) {
		final String path = "images/terrain/" + name + ".png";
		return getIcon(path, name);
	}

	public static Icon getPokemonIcon(String name) {
		final String path = "images/pokemon/" + name + "_large.png";
		return getIcon(path, name);
	}

	public static Icon getIcon(String path, String name) {
		Icon icon = iconCache.get(path);
		if (icon != null) {
			return icon;
		}
		icon = new ImageIcon(path, name);
		iconCache.put(path, icon);
		return icon;
	}

	public static BufferedImage getPokemonImage(String name) {
		final String path = "images/pokemon/" + name + ".png";
		return getImage(path);
	}

	public static BufferedImage getTerrainImage(String name) {
		final String path = "images/terrain/" + name + ".png";
		return getImage(path);
	}

	public static BufferedImage getImage(String path) {
		BufferedImage image = imageCache.get(Zelda.resourcePath + path);
		if (image != null) {
			return image;
		}
		try {
			image = ImageIO.read(new File(Zelda.resourcePath + path));
		} catch (IOException e) {
			throw new RuntimeException("Image " + Zelda.resourcePath + path + " is missing");
		}
		return updateCache(image, path, imageCache);
	}

	private static <T> BufferedImage updateCache(BufferedImage image, T key, Map<T, BufferedImage> cache) {
		image = toCompatibleImage(image);
		cache.put(key, image);
		return image;
	}

	public static BufferedImage toCompatibleImage(BufferedImage image) {
        GraphicsConfiguration gc = getConfiguration(); 
        if (image.getColorModel().equals(gc.getColorModel())) { 
            return image; 
        } 
        BufferedImage compatibleImage = gc.createCompatibleImage( 
                image.getWidth(), image.getHeight(), 
                image.getTransparency()); 
        Graphics g = compatibleImage.getGraphics(); 
        g.drawImage(image, 0, 0, null); 
        g.dispose(); 
        return compatibleImage; 
    }

    private static GraphicsConfiguration getConfiguration() { 
        return GraphicsEnvironment.getLocalGraphicsEnvironment(). 
                getDefaultScreenDevice().getDefaultConfiguration(); 
    }
}