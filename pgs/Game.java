package pgs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Game extends JComponent {
	private static final long serialVersionUID = 1L;

	private transient int width;
	private transient int height;
	private double positionX;
	private double positionY;

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
	    initialize(true);
	}

	public void initialize(boolean loadRenderables) {
		loadMap(createMapImage(), loadRenderables);
		timer.initialize();
	}

	protected BufferedImage createMapImage() {
		try {
			return ImageIO.read(new File(mapPath));
		} catch (IOException e) {
			throw new RuntimeException("Map missing: " + mapPath);
		}
	}

	protected float getVelocity(Terrain tile) {
		return tile.getVelocity();
	}

	private boolean collides(int x, int y) {
		return x < 0 || x >= width || y < 0 || y >= height || getVelocity(grid[x][y]) == 0;
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
		if (hyp < 0.01) {
			// Target too close.
			return 0;
		}
		double directionX = dx / hyp;
		double directionY = dy / hyp;
		float velocity = getVelocity(grid[map(positionX)][map(positionY)]);
		
		double newPositionX = positionX + velocity * directionX * refreshRate / 1000.0;
		double newPositionY = positionY + velocity * directionY * refreshRate / 1000.0;

		boolean moveX = true;
		boolean moveY = true;
		double radius = 0.5 * Simulator.playerSize / Terrain.tileSize;
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
			stopMoving();
			return 0;
		}

		double spentTime = 0;
		if (moveX) {
			spentTime += Math.abs(directionX);
			if (map(positionX) != map(newPositionX)) {
				clearVisionCache();
				repaint(Simulator.xArea);
			}
			positionX = newPositionX;
		}
		if (moveY) {
			spentTime += Math.abs(directionY);
			if (map(positionY) != map(newPositionY)) {
				clearVisionCache();
				repaint(Simulator.yArea);
			}
			positionY = newPositionY;
		}
		return spentTime;

	}
	
	private static int map(double x) {
		return (int) Math.floor(x + 0.5);
	}

	protected void clearVisionCache() {
		visionCache = null;
	}

	protected void stopMoving() {
		targetX = null;
		targetY = null;
	}

	protected void setPosition(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < width) {
			positionX = x;
			positionY = y;
		}
	}

	protected void loadMap(BufferedImage image, boolean loadRenderables) {
		width = image.getWidth();
		height = image.getHeight();
		grid = new Terrain[width][height];
		if (loadRenderables) {
			renderableGrid = new Renderable[width][height];
			positionX = 0;
			positionY = 0;
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
					if (pixel == t.getMask()) {
						grid[i][j] = t;
						break;
					}
				}
				if (loadRenderables) {
					// 50% transparency is interpreted as a poke stop
					int alpha = (pixel >> 24) & 0xff;
					if (alpha == 0x7f) {
						renderableGrid[i][j] = new PokeStop();
					} else if (alpha == 0x64) {
						positionX = i;
						positionY = j;
					}
				}
			}
		}

		// Create images for the whole map. 
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				imageGrid[i][j] = ImageBuilder.createImage(grid, i, j);
			}
		}
	}

	protected BufferedImage getMapAsImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int mask = grid[i][j].getMask();
				g.setColor(new Color(mask, true));
				g.drawRect(i, j, 1, 1);
			}
		}
		return image;
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
		calculateVision(minX, x, maxX, minY, y, maxY, vision);

		// Paint terrain.
		paintTerrain(g, minX, maxX, minY, maxY, vision);

		// Paint player.
		g.drawImage(ImageCache.getImage("images/terrain/Player.png"), Simulator.playerCornerX, Simulator.playerCornerY, null);

		// Paint header and footer.
		paintHeader(g);
		paintFooter(g);
	}

	protected void calculateVision(int minX, int x, int maxX, int minY, int y, int maxY, float vision) {
		if (visionCache == null) {
			visionCache = new Vision(minX, x, maxX, minY, y, maxY, grid, vision);
			visionCache.calculateFOV();
		}
	}

	protected float getLight(double dx, double dy, int i, int j, float vision) {
		if (Math.hypot(dx, dy) < vision) {
			return visionCache.getLightness(i, j);
		}
		return 0;
	}

	private void paintTerrain(Graphics g, int minX, int maxX, int minY, int maxY, float vision) {
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				double dx = positionX - i;
				double dy = positionY - j;
				float light = getLight(dx, dy, i, j, vision);
				if (light > 0) {
					int px = Simulator.middleCornerX - (int) (dx * Terrain.tileSize);
					int py = Simulator.middleCornerY - (int) (dy * Terrain.tileSize);
					g.drawImage(imageGrid[i][j], px, py, null);
					if (renderableGrid[i][j] != null && renderableGrid[i][j].isVisible(light)) {
						renderableGrid[i][j].render(g, px, py);
					}
					g.drawImage(ImageCache.getDarkOverlay(light), px, py, null);
				}
			}
		}		
	}

	protected void paintHeader(Graphics g) {
		g.setColor(Color.RED);
		final String expNeeded = "Exp needed: " + trainer.getMissingExperience();
		final String level = "Level: " + trainer.getLevel();
		
		g.drawString("X: " + map(positionX), Simulator.xArea.x, Simulator.xArea.y + 15);
		g.drawString("Y: " + map(positionY), Simulator.yArea.x, Simulator.yArea.y + 15);
		timer.paint(g);
		g.drawString(expNeeded, Simulator.expArea.x, Simulator.expArea.y + 15);
		g.drawString(level, Simulator.levelArea.x, Simulator.levelArea.y + 15);
		
	}

	protected void paintFooter(Graphics g) {
		trainer.paintJournal(g);
	}

	protected float getVision() {
		float vision = this.vision;
		double t = timer.getHoursFromMidnight();
		if (t < 6) {
			vision += t - 6;
		}
		if (visionCache != null && visionCache.getRadius() != vision) {
			clearVisionCache();
		}
		return vision;
	}

	public void click(int x, int y) {
		double dx = x - Simulator.middleX;
		double dy = y - Simulator.middleY;
		double targetX = positionX + dx / Terrain.tileSize;
		double targetY = positionY + dy / Terrain.tileSize;
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

	protected void modify(int x, int y, Terrain tile, boolean fillMode) {
		double dx = x - Simulator.middleX;
		double dy = y - Simulator.middleY;
		double targetX = positionX + dx / Terrain.tileSize;
		double targetY = positionY + dy / Terrain.tileSize;
		int px = map(targetX + 0.5);
		int py = map(targetY + 0.5);
		final Terrain t = checkAndGetTerrain(px, py, null);
		if (t != null && t != tile) {
			if (fillMode) {
				Set<List<Integer>> visitedPairs = new HashSet<>();
				List<List<Integer>> workPairs = new ArrayList<>();
				workPairs.add(Arrays.asList(px, py));
				while (!workPairs.isEmpty()) {
					visit(workPairs.remove(0), visitedPairs, workPairs, t);
				}
				for (List<Integer> pair : visitedPairs) {
					modify(pair.get(0), pair.get(1), tile);
				}
			} else {
				modify(px, py, tile);
			}
		}
	}

	private void visit(List<Integer> pair, Set<List<Integer>> visitedPairs, List<List<Integer>> workPairs, Terrain t) {
		int px = pair.get(0);
		int py = pair.get(1);
		if (checkAndGetTerrain(px, py, null) == t) {
			if (visitedPairs.add(pair)) {
				workPairs.add(Arrays.asList(px + 1, py));
				workPairs.add(Arrays.asList(px - 1, py));
				workPairs.add(Arrays.asList(px, py + 1));
				workPairs.add(Arrays.asList(px, py - 1));
			}
		}
	}

	private void modify(int x, int y, Terrain tile) {
		grid[x][y] = tile;
		imageGrid[x][y] = ImageBuilder.createImage(grid, x, y);
		Terrain[] terrains = ImageBuilder.getAdjacentTerrains(grid, x, y);
		if (terrains[1] != null) imageGrid[x - 1][y + 1] = ImageBuilder.createImage(grid, x - 1, y + 1);
		if (terrains[2] != null) imageGrid[x    ][y + 1] = ImageBuilder.createImage(grid, x    , y + 1);
		if (terrains[3] != null) imageGrid[x + 1][y + 1] = ImageBuilder.createImage(grid, x + 1, y + 1);
		if (terrains[4] != null) imageGrid[x - 1][y    ] = ImageBuilder.createImage(grid, x - 1, y    );
		if (terrains[6] != null) imageGrid[x + 1][y    ] = ImageBuilder.createImage(grid, x + 1, y    );
		if (terrains[7] != null) imageGrid[x - 1][y - 1] = ImageBuilder.createImage(grid, x - 1, y - 1);
		if (terrains[8] != null) imageGrid[x    ][y - 1] = ImageBuilder.createImage(grid, x    , y - 1);
		if (terrains[9] != null) imageGrid[x + 1][y - 1] = ImageBuilder.createImage(grid, x + 1, y - 1);
	}

	public void removeRenderable(int x, int y) {
		renderableGrid[x][y] = null;
	}

	public void press(char c) {
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

	protected Renderable checkAndGetRenderable(int x, int y, Renderable fallback) {
		if (x >= 0 && y >= 0 && x < width && y < height) {
			return renderableGrid[x][y];
		}
		return fallback;
	}

	protected void spawn(PokemonSpawnEvent event) {
		event.spawnPokemon();
		timer.addTimedEvent(new PokemonSpawnEvent(), 0.5);
	}

	public class PokemonSpawnEvent implements Targetable, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void event(Game game) {
			spawn(this);
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
