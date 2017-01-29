package pgs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Canvas extends JComponent {
	private int width = 50;
	private int height = 50;
	private final int gridOffsetX = 8;
	private final int gridOffsetY = 10;
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
	
	private long time = 0;
	private long previousSpawn = 0;

	private Trainer trainer = new Trainer();
	
	private Vision visionCache;
	
	private Terrain[][] grid;
	private Pokemon[][] pokemonGrid;
	private BufferedImage[][] imageGrid;
	private List<DespawnData> despawnData = new ArrayList<>();
	private static Random r = new Random();
	
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
	
	private final Timer timer = new Timer(refreshRate, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			// Target set at all?
			if (targetX == null || targetY == null) {
				return;
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
					return;
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
				return;
			}

			if (moveX) {
				time += refreshRate * Math.abs(directionX);
				if (map(positionX) != map(newPositionX)) {
					visionCache = null;
				}
				positionX = newPositionX;
			}
			if (moveY) {
				time += refreshRate * Math.abs(directionY);
				if (map(positionY) != map(newPositionY)) {
					visionCache = null;
				}
				positionY = newPositionY;
			}
			
			if (!despawnData.isEmpty()) {
				while (despawnData.get(0).despawn(pokemonGrid, time)) {
					despawnData.remove(0);
					if (despawnData.isEmpty()) {
						break;
					}
				}
			}
			
			// Spawn new...
			if (time - previousSpawn > 1800) {
				previousSpawn = time;
				int rad = r.nextInt(10) + 15;
				double angle = r.nextDouble() * 2 * Math.PI;
				double candX = positionX + rad * Math.cos(angle);
				double candY = positionY + rad * Math.sin(angle);
				int cx = map(candX);
				int cy = map(candY);
				if (cx >= 0 && cy >= 0 && cx < width && cy < height) {
					createRandomPokemon(cx, cy);
					System.err.println("Spawned at " + cx + ", " + cy);
				}
			}
			repaint();
		}
	});
	
	private static int map(double x) {
		return (int) Math.floor(x + 0.5);
	}
	
	public Canvas() {
		
		
		try {
			BufferedImage image = ImageIO.read(new File("images/world.png"));
			width = image.getWidth();
			height = image.getHeight();
			grid = new Terrain[width][height];
			pokemonGrid = new Pokemon[width][height];
			imageGrid = new BufferedImage[width][height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int pixel = image.getRGB(i, j) & 0x00ffffff;
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
		
		timer.start();
	}
	
	private void createRandomPokemon(int x, int y) {
		Terrain tile = grid[x][y];
		switch (r.nextInt(10)) {
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
		if (pokemonGrid[x][y] == null) {
			pokemonGrid[x][y] = new Pokemon(tile, trainer.getLevel());
			despawnData.add(new DespawnData(x, y, time));
		}
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 480, 640);

		final int x = map(positionX);
		final int y = map(positionY);
		
		float vision = this.vision;
		double t = TimeUtil.getHoursFromMidnight(time);
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
						if (pokemonGrid[i][j] != null && pokemonGrid[i][j].isVisible(light)) {
							pokemonGrid[i][j].render(g, px, py);
						}
						Color overlay = new Color(0, 0, 0, 255 - (int) (255 * light));
						g.setColor(overlay);
						g.fillRect(px, py, tileSize, tileSize);
					}
				}
			}
		}
		
		// Paint adventurer
		//g.setColor(Color.RED);
		g.drawImage(ImageCache.getImage("images/terrain/Player.png"), middleOvalCornerX, middleOvalCornerY, null);
		//g.fillOval(middleOvalCornerX, middleOvalCornerY, playerSize, playerSize);
		
		g.drawString(TimeUtil.timeToString(time), 220, 15);
	}
	
	public void click(int x, int y) {
		double dx = x - middleX;
		double dy = y - middleY;
		double targetX = positionX + dx / tileSize;
		double targetY = positionY + dy / tileSize;
		int px = map(targetX);
		int py = map(targetY);
		if (px >= 0 && py >= 0 && px < width && py < height) {
			Pokemon p = pokemonGrid[px][py];
			if (p != null && visionCache != null &&
				p.isVisible(visionCache.getLightness(px, py))) {
				// Stop moving
				this.targetX = null;
				this.targetY = null;
				trainer.capture(this, p, false);
				trainer.addCaptureData(p, new CaptureData(p.getStatus(), px, py, time));
				if (p.getStatus() != CaptureResult.FREE) {
					pokemonGrid[px][py] = null;
					repaint();
				}
				return;
			}
		}
		this.targetX = targetX;
		this.targetY = targetY;
		System.err.println("Targeting: " + targetX + ", " + targetY);
	}
	

	
	public void press() {
		System.err.println("Current position: " + positionX + ", " + positionY);
	}
}
