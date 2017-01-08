import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class Simulator {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("PGS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 500, 500);
		final Canvas canvas = new Canvas();
		frame.setContentPane(canvas);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				canvas.press();
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		canvas.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				canvas.click(e.getX(), e.getY());
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});
		frame.requestFocus();
		frame.setVisible(true);
	}
}

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
	private double vision = 11.0;
	
	private double directionX;
	private double directionY;
	private Double targetX;
	private Double targetY;
	
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
				positionX = newPositionX;
			}
			if (moveY) {
				positionY = newPositionY;
			}
			repaint();
		}
	});
	
	private static int map(double x) {
		return (int) (x + 0.5);
	}
	
	Canvas() {
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

		double l = positionX - vision;
		double u = positionY - vision;
		
		int minX = Math.max(0, (int) l);
		int maxX = Math.min(width, (int) (positionX + vision + 1));
		int minY = Math.max(0, (int) u);
		int maxY = Math.min(height, (int) (positionY + vision + 1));
		
		int x = map(positionX);
		int y = map(positionY);
		int startx = -1;
		int starty = -1;
		float[][] resistanceMap = new float[maxX - minX][maxY - minY];
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				int xx = i - minX;
				int yy = j - minY;
				resistanceMap[xx][yy] = (float) (1.0 - grid[i][j].getTransparency());
				if (x == i && y == j) {
					startx = xx;
					starty = yy;
				}
			}
		}
		Vision v = new Vision();
		float[][] result = v.calculateFOV(resistanceMap, startx, starty, (float) vision, new RadiusStrategy());
		
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				double dx = positionX - i;
				double dy = positionY - j;
				double distance = Math.hypot(dx, dy);
				if (distance < vision) {
					Color color = grid[i][j].getColor();
					int blue = color.getBlue();
					int green = color.getGreen();
					int red = color.getRed();
					float light = result[i - minX][j - minY];
					int newBlue = (int) (blue * light);
					int newGreen = (int) (green * light);
					int newRed = (int) (red * light);
					color = new Color(newRed, newGreen, newBlue);
					g.setColor(color);
					g.fillRect(middleCornerX - (int) (dx * tileSize), middleCornerY - (int) (dy * tileSize), tileSize, tileSize);
				}
			}
		}
		
		g.setColor(Color.RED);
		g.fillOval(middleOvalCornerX, middleOvalCornerY, playerSize, playerSize);
	}
	
	void click(int x, int y) {
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
	
	void press() {
		System.err.println(positionX + ", " + positionY);
		double l = positionX - vision;
		double u = positionY - vision;
		
		int minX = Math.max(0, (int) l);
		int maxX = Math.min(width, (int) (positionX + vision + 1));
		int minY = Math.max(0, (int) u);
		int maxY = Math.min(height, (int) (positionY + vision + 1));

		int x = map(positionX);
		int y = map(positionY);
		int startx = -1;
		int starty = -1;
		float[][] resistanceMap = new float[maxX - minX][maxY - minY];
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				int xx = i - minX;
				int yy = j - minY;
				resistanceMap[xx][yy] = (float) (1.0 - grid[i][j].getTransparency());
				if (x == i && y == j) {
					startx = xx;
					starty = yy;
				}
			}
		}
		Vision v = new Vision();
		float[][] result = v.calculateFOV(resistanceMap, startx, starty, (float) vision, new RadiusStrategy());
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				System.err.print(result[i][j] + " ");
			}
			System.err.println();
		}
		

	}
}

class Direction {
	
	private Direction(int x, int y) {
		deltaX = x;
		deltaY = y;
	}
	int deltaX;
	int deltaY;
	
	static Direction[] DIAGONALS = createDiagonals(); 
	
	private static Direction[] createDiagonals() {
		Direction[] result = new Direction[4];
		result[0] = new Direction(1, 1);
		result[1] = new Direction(1, -1);
		result[2] = new Direction(-1, 1);
		result[3] = new Direction(-1, -1);
		return result;
	}
}

class RadiusStrategy {
	float radius(int dx, int dy) {
		return (float) Math.hypot(dx, dy);
	}
}

class Vision {
	
	int startx;
	int starty;
	float radius;
	int width;
	int height;
	RadiusStrategy rStrat;
	float[][] resistanceMap;
	float[][] lightMap;
	
	public float[][] calculateFOV(float[][] resistanceMap, int startx, int starty, float radius, RadiusStrategy rStrat) {
	    this.startx = startx;
	    this.starty = starty;
	    this.radius = radius;
	    this.rStrat = rStrat;
	    this.resistanceMap = resistanceMap;
	 
	    width = resistanceMap.length;
	    height = resistanceMap[0].length;
	    lightMap = new float[width][height];
	 
	    lightMap[startx][starty] = 1.0f; //light the starting cell
	    for (Direction d : Direction.DIAGONALS) {
	        castLight(1, 1.0f, 0.0f, 0, d.deltaX, d.deltaY, 0);
	        castLight(1, 1.0f, 0.0f, d.deltaX, 0, 0, d.deltaY);
	    }
	 
	    return lightMap;
	}
	
	private void castLight(int row, float start, float end, int xx, int xy, int yx, int yy) {
	    float newStart = 0.0f;
	    if (start < end) {
	        return;
	    }
	    boolean blocked = false;
	    for (int distance = row; distance <= radius && !blocked; distance++) {
	        int deltaY = -distance;
	        for (int deltaX = -distance; deltaX <= 0; deltaX++) {
	            int currentX = startx + deltaX * xx + deltaY * xy;
	            int currentY = starty + deltaX * yx + deltaY * yy;
	            float leftSlope = (deltaX - 0.5f) / (deltaY + 0.5f);
	            float rightSlope = (deltaX + 0.5f) / (deltaY - 0.5f);
	 
	            if (!(currentX >= 0 && currentY >= 0 && currentX < this.width && currentY < this.height) || start < rightSlope) {
	                continue;
	            } else if (end > leftSlope) {
	                break;
	            }
	 
	            //check if it's within the lightable area and light if needed
	            if (rStrat.radius(deltaX, deltaY) <= radius) {
	                float bright = (float) (1 - (rStrat.radius(deltaX, deltaY) / radius));
	                lightMap[currentX][currentY] = bright;
	            }
	 
	            if (blocked) { //previous cell was a blocking one
	                if (resistanceMap[currentX][currentY] >= 1) {//hit a wall
	                    newStart = rightSlope;
	                    continue;
	                } else {
	                    blocked = false;
	                    start = newStart;
	                }
	            } else {
	                if (resistanceMap[currentX][currentY] >= 1 && distance < radius) {//hit a wall within sight line
	                    blocked = true;
	                    castLight(distance + 1, start, leftSlope, xx, xy, yx, yy);
	                    newStart = rightSlope;
	                }
	            }
	        }
	    }
	}
}

class Tile {
	
	static Tile WATER = new Tile("Water", 0, Color.BLUE, 1.0, 1.0);
	static Tile GRASS = new Tile("Grass", 1, Color.GREEN, 0.1, 1.0);
	static Tile FOREST = new Tile("Forest", 2, Color.GREEN.darker().darker(), 0.3, 0.5);
	static Tile MOUNTAIN = new Tile("Mountain", 3, Color.GRAY, 0.8, 0.2);
	static Tile WALL = new Tile("Wall", 4, Color.DARK_GRAY, 1.0, 0.0);
	static Tile ROAD = new Tile("Road", 5, Color.BLACK, 0.0, 1.0);
	
	private String name;
	private int id;
	private Color color;
	private double solidity;
	private double transparency;
	
	Tile(String name, int id, Color color, double solidity, double transparency) {
		this.name = name;
		this.id = id;
		this.color = color;
		this.solidity = solidity;
		this.transparency = transparency;
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
	
	public double getTransparency() {
		return transparency;
	}
}