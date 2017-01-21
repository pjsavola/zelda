package pgs;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Tile {
	
	WATER("Water", "water.png", 0, 0.0f, PokemonDistribution.GRASS),
	GRASS("Grass", "grass.png", 4.0f, 0.0f, PokemonDistribution.GRASS),
	FOREST("Forest", "forest.png", 2.0f, 0.1f, PokemonDistribution.GRASS),
	MOUNTAIN("Mountain", "mountain3.png", 1.0f, 0.2f, PokemonDistribution.GRASS),
	WALL("Wall", "red_brick_wall.png", 0, 1.0f, PokemonDistribution.GRASS),
	ROAD("Road", "road.png", 8.0f, 0.0f, PokemonDistribution.GRASS),
	HILL("Hill", "hills.png", 2.0f, 0.05f, PokemonDistribution.GRASS);

	public static int tileSize = 16;

	private final String name;
	private final float velocity;
	private final float opacity;
	private Color color;
	private BufferedImage image;
	private final PokemonDistribution distribution;
	
	private Tile(String name, Color color, float velocity, float opacity, PokemonDistribution distribution) {
		this.name = name;
		this.color = color;
		this.image = null;
		this.velocity = velocity;
		this.opacity = opacity;
		this.distribution = distribution;
	}
	
	private Tile(String name, String file, float velocity, float opacity, PokemonDistribution distribution) {
		this.name = name;
		try {
			this.image = ImageIO.read(new File(file));
		} catch (IOException e) {
			this.color = Color.GRAY;
		}
		this.velocity = velocity;
		this.opacity = opacity;
		this.distribution = distribution;
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
	
	public void render(Graphics g, int x, int y) {
		if (image != null) {
			g.drawImage(image, x, y, null);
		} else {
			g.setColor(color);
			g.fillRect(x, y, tileSize, tileSize);
		}
	}
	
	public PokemonKind getRandomPokemonKind() {
		return distribution.getRandomPokemonKind();
	}
}
