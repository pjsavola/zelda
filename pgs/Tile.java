package pgs;
import java.awt.Color;
import java.awt.Graphics;

class Tile {
	
	static Tile WATER = new Tile("Water", 0, Color.BLUE, 0, 0.0f);
	static Tile GRASS = new Tile("Grass", 1, Color.GREEN, 4.0f, 0.0f);
	static Tile FOREST = new Tile("Forest", 2, Color.GREEN.darker().darker(), 2.0f, 0.2f);
	static Tile MOUNTAIN = new Tile("Mountain", 3, Color.GRAY, 1.0f, 0.1f);
	static Tile WALL = new Tile("Wall", 4, Color.DARK_GRAY, 0, 1.0f);
	static Tile ROAD = new Tile("Road", 5, new Color(222, 184, 135), 8.0f, 0.0f);
	
	private String name;
	private int id;
	private Color color;
	private float velocity;
	private float opacity;
	public static int tileSize = 16;
	
	Tile(String name, int id, Color color, float velocity, float opacity) {
		this.name = name;
		this.id = id;
		this.color = color;
		this.velocity = velocity;
		this.opacity = opacity;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public float getVelocity() {
		return velocity;
	}
	
	public float getOpacity() {
		return opacity;
	}
	
	public void render(Graphics g, int x, int y, float light) {
		Color color = adjustColor(this.color, light);
		g.setColor(color);
		g.fillRect(x, y, tileSize, tileSize);

	}
	
	private static Color adjustColor(Color color, float light) {
		int blue = color.getBlue();
		int green = color.getGreen();
		int red = color.getRed();
		int newBlue = (int) (blue * light);
		int newGreen = (int) (green * light);
		int newRed = (int) (red * light);
		return new Color(newRed, newGreen, newBlue);
	}
}
