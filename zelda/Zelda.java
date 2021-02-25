package zelda;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class Zelda extends JComponent {
	private static final int tileSize = 24;
	private static final int screenWidth = 21;
	private static final int screenHeight = 21;
	public static int windowWidth = tileSize * screenWidth;
	public static int windowHeight = tileSize * screenHeight;
	
	public static void main(String[] args) {
		JDialog frame = new JDialog();
		frame.setTitle("Zelda");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, windowWidth, windowHeight);
		final Zelda zelda = new Zelda();
		frame.setContentPane(zelda);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
				zelda.press(e.getKeyChar());
			}
		});
		zelda.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				zelda.click(e.getX(), e.getY());
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
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
			}
			@Override
			public void windowClosing(WindowEvent arg0) {
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}
			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}
			@Override
			public void windowIconified(WindowEvent arg0) {
			}
			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});
		frame.requestFocus();
		frame.setResizable(false);
		frame.setModal(true);
		frame.setVisible(true);
	}

	private static BufferedImage createMapImage(final String mapPath) {
		try {
			return ImageIO.read(new File(mapPath));
		} catch (IOException e) {
			throw new RuntimeException("Map missing: " + mapPath);
		}
	}
	
	public enum Terrain {
		GRASS("grass", 0xff4cff00, true),
		TALL_GRASS("tall grass", 0xff36b500, true),
		SAND("sand", 0xffffe97f, true),
		WALL("wall", 0xff60482b, false),
		WATER("water", 0xff0026ff, false),
		SNOW("snow", 0xffffffff, true),
		SOLID_ROCK("solid rock", 0xff808080, true),
		LAVA("lava", 0xffff0000, false);
		
		private Terrain(String file, int mask, boolean passable) {
			image = ImageCache.getTerrainImage(file);
			this.mask = mask;
			this.passable = passable;
		}
		
		public BufferedImage getImage() {
			return image;
		}
		
		public int getMask() {
			return mask;
		}
		
		public boolean isPassable() {
			return passable;
		}
		
		private final BufferedImage image;
		private final int mask;
		private final boolean passable;
	}

	public enum Type {
		ROCK("rock"),
		HOLE("hole"),
		TREASURE_CHEST("treasure chest"),
		LINK_NO_ITEMS("link_no_items");
		
		private Type(String file) {
			image = ImageCache.getImage("images/objects/" + file + ".png");
		}
		
		public BufferedImage getImage() {
			return image;
		}
		
		private final BufferedImage image;			
	}
	
	public class GameObject {
		
		private Type type;
		private int x;
		private int y;
		
		public void move(int dx, int dy) {
			final int tx = x + dx;
			final int ty = y + dy;
			if (canMoveTo(tx, ty)) {
				objectGrid[x][y] = null;
				objectGrid[tx][ty] = this;
				x += dx;
				y += dy;
			} else {
				GameObject o = getObject(tx, ty);
				if (o != null && o.isPushable()) {
					if (canMoveTo(x + 2 * dx, y + 2 * dy)) {
						objectGrid[x + 2 * dx][y + 2 * dy] = o;
						objectGrid[tx][ty] = this;
						objectGrid[x][y] = null;
						x += dx;
						y += dy;
					}
				}
			}
		}
		
		public boolean isPushable() {
			return false;
		}
		
		public boolean isPassable() {
			return false;
		}
	}
	
	public class Boulder extends GameObject {
		@Override
		public boolean isPushable() {
			return true;
		}
	}
	
	private int width;
	private int height;
	private Terrain[][] grid;
	private GameObject[][] objectGrid;
	private final List<GameObject> objects = new ArrayList<>();
	private GameObject link;
	
	public Zelda() {
		BufferedImage image = createMapImage("images/map4.png");
		width = image.getWidth();
		height = image.getHeight();
		grid = new Terrain[width][height];
		objectGrid = new GameObject[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pixel = image.getRGB(i, j);
				grid[i][j] = Terrain.WATER; // default to water
				for (Terrain t : Terrain.values()) {
					if ((pixel | 0xff000000) == t.getMask()) {
						grid[i][j] = t;
						break;
					}
				}
			}
		}
		link = new GameObject();
		link.type = Type.LINK_NO_ITEMS;
		link.x = 40;
		link.y = 40;
		objects.add(link);
		objectGrid[link.x][link.y] = link;
		
		GameObject rock = new Boulder();
		rock.type = Type.ROCK;
		rock.x = 41;
		rock.y = 40;
		objects.add(rock);
		objectGrid[41][35] = rock;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		for (int i = 0; i < screenWidth; ++i) {
			int px = link.x - screenWidth / 2 + i;
			if (px < 0 || px >= width) continue;
			for (int j = 0; j < screenHeight; ++j) {
				int py = link.y - screenHeight / 2 + j;
				if (py < 0 || py >= height) continue;
				final int x = i * tileSize;
				final int y = j * tileSize;
				g.drawImage(grid[px][py].getImage(), x, y, null);
				if (objectGrid[px][py] != null) {
					g.drawImage(objectGrid[px][py].type.getImage(), x, y, null);	
				}
			}
		}
		
		
		//g.drawImage(ImageCache.getImage("images/objects/link_no_items.png"), tileSize * (screenWidth / 2), tileSize * (screenHeight / 2), null);
		//g.setColor(Color.BLACK);
		//g.fillRect(tileSize * (screenWidth / 2), tileSize * (screenHeight / 2), tileSize, tileSize);
	}
	
	public boolean canMoveTo(int x, int y) {
		final Terrain terrain = getTerrain(x, y);
		boolean free = terrain == null ? false : terrain.isPassable();
		if (free) {
			final GameObject o = getObject(x, y);
			free = o == null ? true : o.isPassable();
		}
		return free;
	}
	
	public Terrain getTerrain(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height ? grid[x][y] : null;
	}
	
	public GameObject getObject(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height ? objectGrid[x][y] : null;
	}
	
	public void press(char c) {
		if (c == 'a') link.move(-1, 0);
		if (c == 'd') link.move(1, 0);
		if (c == 'w') link.move(0, -1);
		if (c == 's') link.move(0, 1);
		repaint();
	}
	
	public void click(int x, int y) {
		
	}
}
