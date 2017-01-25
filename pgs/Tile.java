package pgs;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public enum Tile {
	
	WATER("Water", 0, 0.0f, PokemonDistribution.GRASS),
	GRASS("Grass", 4.0f, 0.0f, PokemonDistribution.GRASS),
	FOREST("Forest", 2.0f, 0.1f, PokemonDistribution.GRASS),
	MOUNTAIN("Mountains", 1.0f, 0.2f, PokemonDistribution.GRASS),
	WALL("Red wall", 0, 1.0f, PokemonDistribution.GRASS),
	ROAD("Road", 8.0f, 0.0f, PokemonDistribution.GRASS),
	HILL("Hills", 2.0f, 0.05f, PokemonDistribution.GRASS),
	LAVA("Lava", 0, 0.0f, PokemonDistribution.GRASS),
	SAND("Sand", 3.0f, 0.0f, PokemonDistribution.GRASS),
	SNOW("Snow", 3.0f, 0.0f, PokemonDistribution.GRASS),
	STONE_WALL("Stone wall", 0, 1.0f, PokemonDistribution.GRASS),
	WOODEN_FLOOR("Wooden floor", 5.0f, 0.0f, PokemonDistribution.GRASS);

	public static int tileSize = 16;

	private final String name;
	private final float velocity;
	private final float opacity;
	private Color color;
	private BufferedImage image;
	private final PokemonDistribution distribution;
	
	private Tile(String name, float velocity, float opacity, PokemonDistribution distribution) {
		this.name = name;
		try {
			this.image = ImageIO.read(new File("images/terrain/" + name + ".png"));
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
