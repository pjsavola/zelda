package pgs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

@SuppressWarnings("serial")
class Canvas extends JComponent {
	private final int width = 50;
	private final int height = 50;
	private final int gridOffsetX = 10;
	private final int gridOffsetY = 10;
	private final int gridSize = 23;
	private final int tileSize = 16;
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

	private double velocity = 4.0;
	private float vision = 11.0f;
	
	private Double targetX;
	private Double targetY;
	
	private Vision visionCache;
	
	private Tile[][] grid = new Tile[width][height];
	
	private boolean cornerCollides(double newPositionX, double newPositionY, double radius) {
		double x = newPositionX + radius;
		double y = newPositionY + radius;
		if (grid[map(x)][map(y)].getVelocity() == 0) {
			return true;
		}
		y = newPositionY - radius;
		if (grid[map(x)][map(y)].getVelocity() == 0) {
			return true;
		}
		x = newPositionX - radius;
		if (grid[map(x)][map(y)].getVelocity() == 0) {
			return true;
		}
		y = newPositionY + radius;
		if (grid[map(x)][map(y)].getVelocity() == 0) {
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
				if (map(positionX) != map(newPositionX)) {
					visionCache = null;
				}
				positionX = newPositionX;
			}
			if (moveY) {
				if (map(positionY) != map(newPositionY)) {
					visionCache = null;
				}
				positionY = newPositionY;
			}
			repaint();
		}
	});
	
	private static int map(double x) {
		return (int) (x + 0.5);
	}
	
	public Canvas() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (i == 0 || i == width - 1 || j == 0 || j == height - 1 || (i == 1 && j == 1)) {
					grid[i][j] = Tile.WATER;
				} else if (Math.hypot(i - 20, j - 20) < 8) {
					grid[i][j] = Tile.MOUNTAIN;
				} else if (i > 5 && i < 45 && j == 5) {
					grid[i][j] = Tile.WALL;
				} else if (i > 5 && i < 45 && j == 8) {
					grid[i][j] = Tile.ROAD;
				} else {
					grid[i][j] = Tile.GRASS;
				}
			}
		}
		timer.start();
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 500, 500);

		final int x = map(positionX);
		final int y = map(positionY);

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
					if (light > 0) {
						int px = middleCornerX - (int) (dx * tileSize);
						int py = middleCornerY - (int) (dy * tileSize);
						grid[i][j].render(g, px, py, light);
					}
				}
			}
		}
		
		// Paint adventurer
		g.setColor(Color.RED);
		g.fillOval(middleOvalCornerX, middleOvalCornerY, playerSize, playerSize);
	}
	

	
	public void click(int x, int y) {
		double dx = x - middleX;
		double dy = y - middleY;
		/*
		double hyp = Math.hypot(dx, dy);
		directionX = dx / hyp;
		directionY = dy / hyp;
		*/
		targetX = positionX + dx / tileSize;
		targetY = positionY + dy / tileSize;
		System.err.println("Targeting " + targetX + ", " + targetY);
	}
	
	public void press() {
		System.err.println(positionX + ", " + positionY);
	}
}
