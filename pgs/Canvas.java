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
	
	private double directionX;
	private double directionY;
	private Double targetX;
	private Double targetY;
	
	private Vision visionCache;
	
	private Tile[][] grid = new Tile[width][height]; 
	
	private final Timer timer = new Timer(refreshRate, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			// Target set at all?
			if (targetX == null || targetY == null) {
				return;
			}
			
			// Stuck?
			if (directionX == 0 && directionY == 0) {
				return;
			}
			
			double newPositionX = positionX + velocity * directionX * refreshRate / 1000.0;
			double newPositionY = positionY + velocity * directionY * refreshRate / 1000.0;
			
			double distance = Math.hypot(targetX - positionX, targetY - positionY);
			double newDistance = Math.hypot(targetX - newPositionX, targetY - newPositionY);

			// Not getting any closer
			if (newDistance > distance) {
				directionX = 0;
				directionY = 0;
				return;
			}

			boolean collision = false;
			boolean moveX = true;
			boolean moveY = true;
			double radius = 0.5 * playerSize / tileSize;
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					double x = newPositionX + (i == 0 ? radius : -radius);
					double y = newPositionY + (j == 0 ? radius : -radius);
					if (grid[map(x)][map(y)].getSolidity() == 1.0) {
						collision = true;
						break;
					}
				}
			}
			if (collision) {
				moveX = true;
				moveY = true;
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 2; j++) {
						double x = positionX + (i == 0 ? radius : -radius);
						double y = newPositionY + (j == 0 ? radius : -radius);
						if (grid[map(x)][map(y)].getSolidity() == 1.0) {
							moveY = false;
							break;
						}
					}
				}
				for (int i = 0; i < 2; i++) {
					for (int j = 0; j < 2; j++) {
						double x = newPositionX + (i == 0 ? radius : -radius);
						double y = positionY + (j == 0 ? radius : -radius);
						if (grid[map(x)][map(y)].getSolidity() == 1.0) {
							moveX = false;
							break;
						}
					}
				}
				if (moveX && moveY) {
					return;
				}
			}

			if (moveX && !moveY) {
				newDistance = Math.hypot(targetX - newPositionX, targetY - positionY);
				// Not getting any closer
				if (newDistance > distance) {
					directionX = 0;
					directionY = 0;
					return;
				}
				directionY = 0;
			}
			if (!moveX && moveY) {
				newDistance = Math.hypot(targetX - positionX, targetY - newPositionY);
				// Not getting any closer
				if (newDistance > distance) {
					directionX = 0;
					directionY = 0;
					return;
				}
				directionX = 0;
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
						Color color = adjustColor(grid[i][j].getColor(), light);
						g.setColor(color);
						g.fillRect(middleCornerX - (int) (dx * tileSize), middleCornerY - (int) (dy * tileSize), tileSize, tileSize);
					}
				}
			}
		}
		
		// Paint adventurer
		g.setColor(Color.RED);
		g.fillOval(middleOvalCornerX, middleOvalCornerY, playerSize, playerSize);
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
	
	public void click(int x, int y) {
		double dx = x - middleX;
		double dy = y - middleY;
		double hyp = Math.hypot(dx, dy);
		directionX = dx / hyp;
		directionY = dy / hyp;
		targetX = positionX + dx / tileSize;
		targetY = positionY + dy / tileSize;
		System.err.println("Targeting " + targetX + ", " + targetY);
		System.err.println("Direction " + directionX + ", " + directionY);
	}
	
	public void press() {
		System.err.println(positionX + ", " + positionY);
	}
}
