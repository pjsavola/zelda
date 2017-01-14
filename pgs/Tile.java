package pgs;
import java.awt.Color;

class Tile {
	
	static Tile WATER = new Tile("Water", 0, Color.BLUE, 1.0, 0.0f);
	static Tile GRASS = new Tile("Grass", 1, Color.GREEN, 0.1, 0.0f);
	static Tile FOREST = new Tile("Forest", 2, Color.GREEN.darker().darker(), 0.3, 0.2f);
	static Tile MOUNTAIN = new Tile("Mountain", 3, Color.GRAY, 0.8, 0.3f);
	static Tile WALL = new Tile("Wall", 4, Color.DARK_GRAY, 1.0, 1.0f);
	static Tile ROAD = new Tile("Road", 5, Color.BLACK, 0.0, 0.0f);
	
	private String name;
	private int id;
	private Color color;
	private double solidity;
	private float opacity;
	
	Tile(String name, int id, Color color, double solidity, float opacity) {
		this.name = name;
		this.id = id;
		this.color = color;
		this.solidity = solidity;
		this.opacity = opacity;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public double getSolidity() {
		return solidity;
	}
	
	public float getOpacity() {
		return opacity;
	}
}
