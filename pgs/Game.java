package pgs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Game extends JComponent {
	private static final long serialVersionUID = 1L;

	private transient int width;
	private transient int height;
	private double positionX;
	private double positionY;

	private static final int gridOffsetX = 8;
	private static final int gridOffsetY = 30;
	private static final int gridSize = 29;
	private static final int tileSize = Terrain.tileSize;
	private static final int playerSize = 11;
	private static final int middleCornerX = gridOffsetX + gridSize / 2 * tileSize;
	private static final int middleCornerY = gridOffsetY + gridSize / 2 * tileSize;
	private static final int middleX = middleCornerX + tileSize / 2;
	private static final int middleY = middleCornerY + tileSize / 2;
	private static final int playerCornerX = middleX - playerSize / 2 - 1;
	private static final int playerCornerY = middleY - playerSize / 2 - 1;

	// Must be 15.0f or less or the window is too small to show everything.
	private float vision = 15.0f;
	
	private transient Double targetX;
	private transient Double targetY;

	private Trainer trainer = new Trainer();
	
	private transient Vision visionCache;

	private String mapPath;
	private transient Terrain[][] grid;
	private transient BufferedImage[][] imageGrid;
	private Renderable[][] renderableGrid;

	private final GameTimer timer = new GameTimer(this);

	public Game(String mapPath) {
		this.mapPath = mapPath;
		timer.addTimedEvent(new PokemonSpawnEvent(), 0.5);
	    positionX = 30;
	    positionY = 50;
	}

	public void initialize() {
		loadMap();
		timer.initialize();
	}

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
	public double move(int refreshRate) {
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
				repaint(Simulator.xArea);
			}
			positionX = newPositionX;
		}
		if (moveY) {
			spentTime += Math.abs(directionY);
			if (map(positionY) != map(newPositionY)) {
				visionCache = null;
				repaint(Simulator.yArea);
			}
			positionY = newPositionY;
		}
		return spentTime;

	}
	
	private static int map(double x) {
		return (int) Math.floor(x + 0.5);
	}

	private void loadMap() {
		boolean loadRenderables = renderableGrid == null;
		try {
			BufferedImage image = ImageIO.read(new File(mapPath));
			width = image.getWidth();
			height = image.getHeight();
			grid = new Terrain[width][height];
			if (loadRenderables) {
				renderableGrid = new Renderable[width][height];
			}
			imageGrid = new BufferedImage[width][height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int pixel = image.getRGB(i, j);
					//int alpha = (pixel >> 24) & 0xff;
				    //int red = (pixel >> 16) & 0xff;
				    //int green = (pixel >> 8) & 0xff;
				    //int blue = (pixel) & 0xff;
					grid[i][j] = Terrain.WATER;
					for (Terrain t : Terrain.values()) {
						if ((pixel & 0x00ffffff) == t.getMask()) {
							grid[i][j] = t;
							break;
						}
					}
					if (loadRenderables) {
						// 50% transparency is interpreted as a poke stop
						int alpha = (pixel >> 24) & 0xff;
						if (alpha == 0x7f) {
							renderableGrid[i][j] = new PokeStop();
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Map missing: " + mapPath);
		}

		// Create images for the whole map. 
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				imageGrid[i][j] = ImageBuilder.createImage(grid, i, j);
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		// Clear everything.
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Simulator.windowWidth, Simulator.windowHeight);

		// Calculate vision based on time and base vision, and possibly clear vision cache.
		float vision = getVision();

		// Calculate bounds for vision calculation and terrain rendering.
		final int x = map(positionX);
		final int y = map(positionY);
		final int minX = Math.max(0, x - (int) vision);
		final int maxX = Math.min(width, x + (int) vision);
		final int minY = Math.max(0, y - (int) vision);
		final int maxY = Math.min(height, y + (int) vision);
		
		// Recalculate vision if needed.
		if (visionCache == null) {
			visionCache = new Vision(minX, x, maxX, minY, y, maxY, grid, vision);
			visionCache.calculateFOV();
		}

		// Paint terrain.
		paintTerrain(g, minX, maxX, minY, maxY);

		// Paint player.
		g.drawImage(ImageCache.getImage("images/terrain/Player.png"), playerCornerX, playerCornerY, null);

		// Paint header and footer.
		paintHeader(g);
		paintFooter(g);
	}
	
	private void paintTerrain(Graphics g, int minX, int maxX, int minY, int maxY) {
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				double dx = positionX - i;
				double dy = positionY - j;
				if (Math.hypot(dx,dy) < vision) {
					float light = visionCache.getLightness(i, j);
					if (light > 0) {
						int px = middleCornerX - (int) (dx * tileSize);
						int py = middleCornerY - (int) (dy * tileSize);
						g.drawImage(imageGrid[i][j], px, py, null);
						if (renderableGrid[i][j] != null && renderableGrid[i][j].isVisible(light)) {
							renderableGrid[i][j].render(g, px, py);
						}
						g.drawImage(ImageCache.getDarkOverlay(light), px, py, null);
					}
				}
			}
		}		
	}

	private void paintHeader(Graphics g) {
		g.setColor(Color.RED);
		final String expNeeded = "Exp needed: " + trainer.getMissingExperience();
		final String level = "Level: " + trainer.getLevel();
		
		g.drawString("X: " + map(positionX), Simulator.xArea.x, Simulator.xArea.y + 15);
		g.drawString("Y: " + map(positionY), Simulator.yArea.x, Simulator.yArea.y + 15);
		timer.paint(g);
		g.drawString(expNeeded, Simulator.expArea.x, Simulator.expArea.y + 15);
		g.drawString(level, Simulator.levelArea.x, Simulator.levelArea.y + 15);
		
	}

	private void paintFooter(Graphics g) {
		trainer.paintJournal(g);
	}

	private float getVision() {
		float vision = this.vision;
		double t = timer.getHoursFromMidnight();
		if (t < 6) {
			vision += t - 6;
		}
		if (visionCache != null && visionCache.getRadius() != vision) {
			visionCache = null;
		}
		return vision;
	}

	public void click(int x, int y) {
		double dx = x - middleX;
		double dy = y - middleY;
		double targetX = positionX + dx / tileSize;
		double targetY = positionY + dy / tileSize;
		int px = map(targetX);
		int py = map(targetY);
		Renderable r = checkAndGetRenderable(px, py, null);
		if (r != null && visionCache != null && r.isVisible(visionCache.getLightness(px, py))) {
			// Click is done through timer in order to stop time for possible dialogs.
			timer.click(r, trainer);
		} else {
			// New move target
			this.targetX = targetX;
			this.targetY = targetY;
			System.err.println("Targeting: " + targetX + ", " + targetY);
		}
	}

	public void removeRenderable(int x, int y) {
		renderableGrid[x][y] = null;
	}

	public void press() {
		System.err.println("Current position: " + positionX + ", " + positionY);
	}

	public GameTimer getTimer() {
		return timer;
	}

	private Terrain checkAndGetTerrain(int x, int y, Terrain fallback) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			return grid[x][y];
		}
		return fallback;
	}

	private Renderable checkAndGetRenderable(int x, int y, Renderable fallback) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			return renderableGrid[x][y];
		}
		return fallback;
	}

	public class PokemonSpawnEvent implements Targetable, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void event(Game game) {
			spawnPokemon();
			timer.addTimedEvent(new PokemonSpawnEvent(), 0.5);
		}

		private void spawnPokemon() {
			int radius = Randomizer.r.nextInt(10) + 15;
			double angle = Randomizer.r.nextDouble() * 2 * Math.PI;
			double candidateX = positionX + radius * Math.cos(angle);
			double candidateY = positionY + radius * Math.sin(angle);
			int x = map(candidateX);
			int y = map(candidateY);
			createRandomPokemon(x, y);
		}

		private void createRandomPokemon(int x, int y) {
			Terrain tile = checkAndGetTerrain(x, y, null);
			switch (Randomizer.r.nextInt(10)) {
			case 1: tile = checkAndGetTerrain(x - 1, y + 1, tile); break;
			case 2: tile = checkAndGetTerrain(x,     y + 1, tile); break;
			case 3: tile = checkAndGetTerrain(x + 1, y + 1, tile); break;
			case 4: tile = checkAndGetTerrain(x - 1, y,     tile); break;
			case 6: tile = checkAndGetTerrain(x + 1, y,     tile); break;
			case 7: tile = checkAndGetTerrain(x - 1, y - 1, tile); break;
			case 8: tile = checkAndGetTerrain(x,     y - 1, tile); break;
			case 9: tile = checkAndGetTerrain(x + 1, y - 1, tile); break;
			}
			if (tile != null && renderableGrid[x][y] == null) {
				renderableGrid[x][y] = new Pokemon(tile, trainer.getLevel(), x, y);
				timer.addTimedEvent(renderableGrid[x][y], 5);
				System.err.println("Spawned at " + x + ", " + y);
			}
		}
	}
}
