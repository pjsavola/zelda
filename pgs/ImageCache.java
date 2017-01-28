package pgs;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageCache {
	private static Map<String, BufferedImage> imageCache = new HashMap<>();
	private static Map<List<String>, BufferedImage> layeredImageCache = new HashMap<>();
	
	public static BufferedImage getImage(String path) {
		BufferedImage image = imageCache.get(path);
		if (image != null) {
			return image;
		}
		try {
			image = ImageIO.read(new File(path));
			imageCache.put(path, image);
		} catch (IOException e) {
			throw new RuntimeException("Image " + path + " is missing");
		}
		return image;
	}
	
	public static BufferedImage getTerrainImage(String name) {
		final String path = "images/terrain/" + name + ".png";
		return getImage(path);
	}
	
	public static BufferedImage getLayeredTerrainImage(List<String> names) {
		BufferedImage image = layeredImageCache.get(names);
		if (image != null) {
			return image;
		}
		BufferedImage combined = new BufferedImage(
				Terrain.tileSize, Terrain.tileSize, BufferedImage.TYPE_INT_ARGB);
		Graphics g = combined.getGraphics();
		for (String name : names) {
			image = getTerrainImage(name);
			g.drawImage(image, 0, 0, null);
		}
		layeredImageCache.put(names, combined);
		return combined;
	}
}