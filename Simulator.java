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
	private final int middleX = gridOffsetX + gridSize / 2 * tileSize;
	private final int middleY = gridOffsetY + gridSize / 2 * tileSize;
	private final int refreshRate = 20;
	private final double acceptableDistance = 0.1;
	
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

			boolean moveX = false;
			boolean moveY = false;
			int newX = map(newPositionX);
			int newY = map(newPositionY);
			if (directionX > 0) {
				newX++;
			}
			if (directionY > 0) {
				newY++;
			}
			if (grid[newX][map(positionY)].getSolidity() < 1.0) {
				moveX = true;
			}
			if (grid[map(positionX)][newY].getSolidity() < 1.0) {
				moveY = true;
			}
			
			if (!moveX && !moveY) {
				directionX = 0;
				directionY = 0;
				return;
			}

			if (moveX) {
				newDistance = Math.hypot(targetX - newPositionX, targetY - positionY);
				if (newDistance > distance) {
					directionX = 0;
					directionY = 0;
					return;
				}
				positionX = newPositionX;
			}
			if (moveY) {
				newDistance = Math.hypot(targetX - positionX, targetY - newPositionY);
				if (newDistance > distance) {
					directionX = 0;
					directionY = 0;
					return;
				}
				positionY = newPositionY;
			}
			repaint();
		}
	});
	
	private static int map(double x) {
		return (int) x;
	}
	
	Canvas() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (i == 0 || i == width - 1 || j == 0 || j == height - 1) {
					grid[i][j] = Tile.WATER;
				} else if (Math.hypot(i - 20, j - 20) < 8) {
					grid[i][j] = Tile.MOUNTAIN;
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
		
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				double dx = positionX - i;
				double dy = positionY - j;
				double distance = Math.hypot(dx, dy);
				if (distance < vision) {
					Color color = grid[i][j].getColor();
					if (distance > vision * 0.75) {
						while (distance > vision * 0.75) {
							color = color.darker();
							distance -= vision * 0.05;
						}
					}
					g.setColor(color);
					g.fillRect(middleX - (int) (dx * tileSize), middleY - (int) (dy * tileSize), tileSize, tileSize);
				}
			}
		}
		
		g.setColor(Color.WHITE);
		g.fillOval(middleX, middleY, tileSize, tileSize);
	}
	
	void click(int x, int y) {
		double dx = x - middleX - tileSize / 2;
		double dy = y - middleY - tileSize / 2;
		double hyp = Math.hypot(dx, dy);
		directionX = dx / hyp;
		directionY = dy / hyp;
		targetX = positionX + dx / tileSize;
		targetY = positionY + dy / tileSize;
		System.err.println("Targeting " + targetX + ", " + targetY);
	}
	
	void press() {
		System.err.println(positionX + ", " + positionY);
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