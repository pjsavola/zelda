package pgs;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public enum PokemonKind {

	PIDGEY("pidgey.png", 0.1f, PokemonType.NORMAL, PokemonType.FLYING);
	
	private final float visibilityThreshold;
	private final List<PokemonType> types;
	private final Image image;
	
	private PokemonKind(String file, float visibilityThreshold, PokemonType ... types) {
		this.visibilityThreshold = visibilityThreshold;
		this.types = Arrays.asList(types);
		try {
			this.image = ImageIO.read(new File(file));
		} catch (IOException e) {
			throw new RuntimeException("Image '" + file + "' missing");
		}
	}
	
	public float getVisibilityThreshold() {
		return visibilityThreshold;
	}
	
	public List<PokemonType> getTypes() {
		return types;
	}
	
	public void render(Graphics g, int x, int y) {
		g.drawImage(image, x, y, null);
	}
}