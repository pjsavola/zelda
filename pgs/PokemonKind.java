package pgs;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum PokemonKind {

	PIDGEY("Pidgey", 0.5f, 0.4, 0.2, 80, 85, 76, PokemonType.NORMAL, PokemonType.FLYING);
	
	private final float visibilityThreshold;
	private final double basicCaptureRate;
	private final double fleeRate;
	private final int baseStamina;
	private final int baseAttack;
	private final int baseDefence;
	private final List<PokemonType> types;
	private final Image image;
	private final Icon icon;
	private final String name;
	
	private PokemonKind(String name, float visibilityThreshold, double bcr, double fr, int bs, int ba, int bd, PokemonType ... types) {
		this.name = name;
		this.visibilityThreshold = visibilityThreshold;
		this.basicCaptureRate = bcr;
		this.fleeRate = fr;
		this.baseStamina = bs;
		this.baseAttack = ba;
		this.baseDefence = bd;
		this.types = Arrays.asList(types);
		try {
			this.image = ImageIO.read(new File("images/pokemon/" + name + ".png"));
			this.icon = new ImageIcon("images/pokemon/" + name + "_large.png", name);
		} catch (IOException e) {
			throw new RuntimeException("Image for " + name + " is missing");
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
	
	public String getName() {
		return name;
	}
	
	public Icon getIcon() {
		return icon;
	}
	
	public double getBasicCaptureRate() {
		return basicCaptureRate;
	}
	
	public double getFleeRate() {
		return fleeRate;
	}
	
	public int getBaseStamina() {
		return baseStamina;
	}
	
	public int getBaseAttack() {
		return baseAttack;
	}
	
	public int getBaseDefence() {
		return baseDefence;
	}
}