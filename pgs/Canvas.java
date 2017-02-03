package pgs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public class Canvas extends JComponent {
	private int width = 50;
	private int height = 50;
	private final int gridOffsetX = 8;
	private final int gridOffsetY = 30;
	private final int gridSize = 29;
	private final int tileSize = Terrain.tileSize;
	private final int playerSize = 11;
	private final int middleCornerX = gridOffsetX + gridSize / 2 * tileSize;
	private final int middleCornerY = gridOffsetY + gridSize / 2 * tileSize;
	private final int middleX = middleCornerX + tileSize / 2;
	private final int middleY = middleCornerY + tileSize / 2;
	private final int middleOvalCornerX = middleX - playerSize / 2 - 1;
	private final int middleOvalCornerY = middleY - playerSize / 2 - 1;
	private final int refreshRate = 20;
	
	private double positionX = 5.0;
	private double positionY = 5.0;

	private float vision = 15.0f;
	
	private Double targetX;
	private Double targetY;


	private Trainer trainer = new Trainer();
	Journal journal = new Journal();
	
	private Vision visionCache;
	
	private Terrain[][] grid;
	private Renderable[][] thingGrid;
	private BufferedImage[][] imageGrid;
	
	private boolean collides(int x, int y) {
		return x < 0 || x >= width || y < 0 || y >= height || grid[x][y].getVelocity() == 0;
	}
	
	private boolean cornerCollides(double newPositionX, double newPositionY, double radius) {
		double x = newPositionX + radius;
		double y = newPositionY + radius;
		if (collides(map(x), map(y))) {
			return true;
		}
		y = newPositionY - radius;
		if (collides(map(x), map(y))) {
			return true;
		}
		x = newPositionX - radius;
		if (collides(map(x), map(y))) {
			return true;
		}
		y = newPositionY + radius;
		if (collides(map(x), map(y))) {
			return true;
		}
		return false;
	}
	
	// Returns the spent time.
	public double move() {
		// Target set at all?
		if (targetX == null || targetY == null) {
			return 0;
		}
		
		double dx = targetX - positionX;
		double dy = targetY - positionY;
		double hyp = Math.hypot(dx, dy);
		double directionX = dx / hyp;
		double directionY = dy / hyp;
		float velocity = grid[map(positionX)][map(positionY)].getVelocity();
		
		double newPositionX = positionX + velocity * directionX * refreshRate / 1000.0;
		double newPositionY = positionY + velocity * directionY * refreshRate / 1000.0;

		boolean moveX = true;
		boolean moveY = true;
		double radius = 0.5 * playerSize / tileSize;
		boolean collision = cornerCollides(newPositionX, newPositionY, radius);
		if (collision) {
			moveY = !cornerCollides(positionX, newPositionY, radius);
			moveX = !cornerCollides(newPositionX, positionY, radius);
			if (moveX && moveY) {
				// Both directions separately are ok, but combined
				// not ok, let's stop instead of guessing which
				// direction would be better.
				return 0;
			}
			if (!moveY) {
				newPositionY = positionY;
			}
			if (!moveX) {
				newPositionX = positionX;
			}
		}

		double distance = Math.hypot(targetX - positionX, targetY - positionY);
		double newDistance = Math.hypot(targetX - newPositionX, targetY - newPositionY);

		// Not getting any closer, stop moving
		if (newDistance > distance) {
			targetX = null;
			targetY = null;
			return 0;
		}

		double spentTime = 0;
		if (moveX) {
			spentTime += Math.abs(directionX);
			if (map(positionX) != map(newPositionX)) {
				visionCache = null;
			}
			positionX = newPositionX;
		}
		if (moveY) {
			spentTime += Math.abs(directionY);
			if (map(positionY) != map(newPositionY)) {
				visionCache = null;
			}
			positionY = newPositionY;
		}
		return spentTime;

	}
	
	private final GameTimer timer = new GameTimer(this);
	
	private final Rectangle xArea = new Rectangle(8, 0, 15, 15);
	private final Rectangle yArea = new Rectangle(58, 0, 15, 15);

	private static int map(double x) {
		return (int) Math.floor(x + 0.5);
	}
	
	public Canvas() {
		
		
		try {
			BufferedImage image = ImageIO.read(new File("images/world.png"));
			width = image.getWidth();
			height = image.getHeight();
			grid = new Terrain[width][height];
			thingGrid = new Renderable[width][height];
			imageGrid = new BufferedImage[width][height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int pixel = image.getRGB(i, j);
					//int alpha = (pixel >> 24) & 0xff;
				    //int red = (pixel >> 16) & 0xff;
				    //int green = (pixel >> 8) & 0xff;
				    //int blue = (pixel) & 0xff;
					grid[i][j] = Terrain.WATER;
					if (i == 32 && j == 46) {
						System.err.println(Integer.toHexString(pixel));
					}
					for (Terrain t : Terrain.values()) {
						if ((pixel & 0x00ffffff) == t.getMask()) {
							grid[i][j] = t;
							break;
						}
					}
					int alpha = (pixel >> 24) & 0xff;
					if (alpha == 0x7f) {
						thingGrid[i][j] = new PokeStop();
					}
				}
			}
		    positionX = 30;
		    positionY = 50;
		} catch (IOException e) {
			throw new RuntimeException("Map missing");
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				imageGrid[i][j] = Layers.createImage(grid, i, j);
			}
		}
		timer.addTimedEvent(new PokemonSpawn(), 0.5);
	}
	
	public void spawnPokemon() {
		int rad = Randomizer.r.nextInt(10) + 15;
		double angle = Randomizer.r.nextDouble() * 2 * Math.PI;
		double candX = positionX + rad * Math.cos(angle);
		double candY = positionY + rad * Math.sin(angle);
		int cx = map(candX);
		int cy = map(candY);
		if (cx >= 0 && cy >= 0 && cx < width && cy < height) {
			createRandomPokemon(cx, cy);
			System.err.println("Spawned at " + cx + ", " + cy);
		}
	}
	
	private void createRandomPokemon(int x, int y) {
		Terrain tile = grid[x][y];
		switch (Randomizer.r.nextInt(10)) {
		case 1: 
			if (x - 1 >= 0 && y + 1 < height) {
				tile = grid[x - 1][y + 1];
			}
			break;
		case 2:
			if (y + 1 < height) {
				tile = grid[x][y + 1];
			}
			break;
		case 3:
			if (x + 1 < width && y + 1 < height) {
				tile = grid[x + 1][y + 1];
			}
			break;
		case 4:
			if (x - 1 >= 0) {
				tile = grid[x - 1][y];
			}
			break;
		case 6:
			if (x + 1 < width) {
				tile = grid[x + 1][y];
			}
			break;
		case 7:
			if (x - 1 >= 0 && y - 1 >= 0) {
				tile = grid[x - 1][y - 1];
			}
			break;
		case 8:
			if (y - 1 >= 0) {
				tile = grid[x][y - 1];
			}
			break;
		case 9:
			if (x + 1 < width && y - 1 >= 0) {
				tile = grid[x + 1][y - 1];
			}
			break;
		}
		if (thingGrid[x][y] == null) {
			thingGrid[x][y] = new Pokemon(tile, trainer.getLevel(), x, y);
			timer.addTimedEvent(thingGrid[x][y], 5);
		}
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		//g.fillRect(0, 0, 480, 640);

		final int x = map(positionX);
		final int y = map(positionY);
		
		float vision = this.vision;
		double t = timer.getHoursFromMidnight();
		if (t < 6) {
			vision += t - 6;
		}
		if (visionCache != null && visionCache.getRadius() != vision) {
			visionCache = null;
		}

		final int minX = Math.max(0, x - (int) vision);
		final int maxX = Math.min(width, x + (int) vision);
		final int minY = Math.max(0, y - (int) vision);
		final int maxY = Math.min(height, y + (int) vision);
		
		// Recalculate vision if needed
		if (visionCache == null) {
			visionCache = new Vision(minX, x, maxX, minY, y, maxY, grid, vision);
			visionCache.calculateFOV();
		}
		
		// Paint terrain
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				double dx = positionX - i;
				double dy = positionY - j;
				if (Math.hypot(dx,dy) < vision) {
					float light = visionCache.getLightness(i, j);
					//light -= darkness;
					if (light > 0) {
						int px = middleCornerX - (int) (dx * tileSize);
						int py = middleCornerY - (int) (dy * tileSize);
						g.drawImage(imageGrid[i][j], px, py, null);
						if (thingGrid[i][j] != null && thingGrid[i][j].isVisible(light)) {
							thingGrid[i][j].render(g, px, py);
						}
						Color overlay = new Color(0, 0, 0, 255 - (int) (255 * light));
						g.setColor(overlay);
						g.fillRect(px, py, tileSize, tileSize);
					}
				}
			}
		}
		
		// 220, 15 is ~middle
		g.drawImage(ImageCache.getImage("images/terrain/Player.png"), middleOvalCornerX, middleOvalCornerY, null);
		g.setColor(Color.RED);

		timer.paint(g);
		paintCoordinates(g);
		String expNeeded = "Exp needed: " + trainer.getMissingExperience();
		g.drawString(expNeeded, 250, 15);
		String level = "Level: " + trainer.getLevel();
		g.drawString(level, 420, 15);
		journal.paint(g, 15, 500);
	}
	
	private void paintCoordinates(Graphics g) {
		g.drawString("X: " + map(positionX), xArea.x, xArea.y + 15);
		g.drawString("Y: " + map(positionY), yArea.x, yArea.y + 15);
	}
	
	public void click(int x, int y) {
		double dx = x - middleX;
		double dy = y - middleY;
		double targetX = positionX + dx / tileSize;
		double targetY = positionY + dy / tileSize;
		int px = map(targetX);
		int py = map(targetY);
		if (px >= 0 && py >= 0 && px < width && py < height) {
			Renderable p = thingGrid[px][py];
			if (p != null && visionCache != null &&
				p.isVisible(visionCache.getLightness(px, py))) {
				// Stop moving
				this.targetX = null;
				this.targetY = null;
				p.click(this, trainer);
				return;
			}
		}
		this.targetX = targetX;
		this.targetY = targetY;
		System.err.println("Targeting: " + targetX + ", " + targetY);
	}
	
	public void clear(int x, int y) {
		thingGrid[x][y] = null;
	}
	
	public void press() {
		System.err.println("Current position: " + positionX + ", " + positionY);
	}
	
	public GameTimer getTimer() {
		return timer;
	}
	
	public class PokemonSpawn implements Targetable {
		@Override
		public void event(Canvas canvas) {
			canvas.spawnPokemon();
			timer.addTimedEvent(new PokemonSpawn(), 0.5);
		}
	}
}
