package pgs;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class Tile {
	
	static Tile WATER = new Tile("Water", 0, "water.png", 0, 0.0f);
	static Tile GRASS = new Tile("Grass", 1, "grass.png", 4.0f, 0.0f);
	static Tile FOREST = new Tile("Forest", 2, "forest.png", 2.0f, 0.1f);
	static Tile MOUNTAIN = new Tile("Mountain", 3, "mountain3.png", 1.0f, 0.2f);
	static Tile WALL = new Tile("Wall", 4, "red_brick_wall.png", 0, 1.0f);
	static Tile ROAD = new Tile("Road", 5, "road.png", 8.0f, 0.0f);
	static Tile HILL = new Tile("Hill", 6, "hills.png", 2.0f, 0.05f);
	
	private String name;
	private int id;
	private Color color;
	private float velocity;
	private float opacity;
	public static int tileSize = 16;
	private BufferedImage image;
	
	Tile(String name, int id, Color color, float velocity, float opacity) {
		this.name = name;
		this.id = id;
		this.color = color;
		this.velocity = velocity;
		this.opacity = opacity;
	}
	
	Tile(String name, int id, String file, float velocity, float opacity) {
		this.name = name;
		this.id = id;
		try {
			this.image = ImageIO.read(new File(file));
		} catch (IOException e) {
			this.color = Color.GRAY;
		}
		this.velocity = velocity;
		this.opacity = opacity;
	}
	
	public String getName() {
		return name;
	}
	
	public float getVelocity() {
		return velocity;
	}
	
	public float getOpacity() {
		return opacity;
	}
	
	public void render(Graphics g, int x, int y, float light) {
		if (image != null) {
			g.drawImage(image, x, y, null);
		} else {
			g.setColor(color);
			g.fillRect(x, y, tileSize, tileSize);
		}
		Color overlay = new Color(0, 0, 0, 255 - (int) (255 * light));
		g.setColor(overlay);
		g.fillRect(x, y, tileSize, tileSize);

	}
}
