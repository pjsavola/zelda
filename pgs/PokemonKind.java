package pgs;

import java.awt.Graphics;
import java.awt.Image;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum PokemonKind {

	DRAGONITE("Dragonite", 0.5f, 0.04, 0.05, 182, 263, 201, null, PokemonType.DRAGON, PokemonType.FLYING),
	DRAGONAIR("Dragonair", 0.4f, 0.08, 0.06, 122, 163, 138, DRAGONITE, PokemonType.DRAGON),
	DRATINI("Dratini", 0.3f, 0.32, 0.09, 82, 119, 94, DRAGONAIR, PokemonType.DRAGON),
	PIDGEOT("Pidgeot", 0.3f, 0.1, 0.06, 166, 166, 157, null, PokemonType.NORMAL, PokemonType.FLYING),
	PIDGEOTTO("Pidgeotto", 0.2f, 0.2, 0.09, 126, 117, 108, PIDGEOT, PokemonType.NORMAL, PokemonType.FLYING),
	PIDGEY("Pidgey", 0.1f, 0.4, 0.2, 80, 85, 76, PIDGEOTTO, PokemonType.NORMAL, PokemonType.FLYING),
	VENUSAUR("Venusaur", 0.5f, 0.04, 0.05, 160, 198, 198, null, PokemonType.GRASS, PokemonType.POISON),
	IVYSAUR("Ivysaur", 0.35f, 0.08, 0.07, 120, 151, 151, VENUSAUR, PokemonType.GRASS, PokemonType.POISON),
	BULBASAUR("Bulbasaur", 0.15f, 0.16, 0.1, 90, 118, 118, IVYSAUR, PokemonType.GRASS, PokemonType.POISON);
	
	private final float visibilityThreshold;
	private final double basicCaptureRate;
	private final double fleeRate;
	private final int baseStamina;
	private final int baseAttack;
	private final int baseDefence;
	private final PokemonKind evolvesTo;
	private final List<PokemonType> types;
	private final Image image;
	private final Icon icon;
	private final String name;
	private final Map<CaptureResult, Integer> captureResults = new HashMap<>();
	
	private PokemonKind(String name, float visibilityThreshold, double bcr, double fr, int bs, int ba, int bd, PokemonKind evolvesTo, PokemonType ... types) {
		this.name = name;
		this.visibilityThreshold = visibilityThreshold;
		this.basicCaptureRate = bcr;
		this.fleeRate = fr;
		this.baseStamina = bs;
		this.baseAttack = ba;
		this.baseDefence = bd;
		this.evolvesTo = evolvesTo;
		this.types = Arrays.asList(types);
		this.image = ImageCache.getImage("images/pokemon/" + name + ".png");
		this.icon = new ImageIcon("images/pokemon/" + name + "_large.png", name);
		captureResults.put(CaptureResult.CAPTURED, 0);
		captureResults.put(CaptureResult.ESCAPED, 0);
		captureResults.put(CaptureResult.FREE, 0);
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