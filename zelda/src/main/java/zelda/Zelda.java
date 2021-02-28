package zelda;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class Zelda extends JComponent {
	static final int tileSize = 24;
	private static final int screenWidth = 21;
	private static final int screenHeight = 21;
	private static final Dimension dim = new Dimension(tileSize * screenWidth, tileSize * screenHeight);
	public static final String resourcePath = Zelda.class.getResource("/").getPath();

	@Override
	public Dimension getPreferredSize() {
		return dim;
	}
	
	public static void main(String[] args) {
		JDialog frame = new JDialog();
		frame.setTitle("Zelda");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final Zelda zelda = new Zelda();
		frame.setContentPane(zelda);
		frame.pack();
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
		zelda.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				zelda.mouseOver(e.getX(), e.getY());
			}
		});
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
				System.exit(0);
			}
			@Override
			public void windowClosing(WindowEvent arg0) {
			    zelda.animator.terminate();
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
		frame.setVisible(true);
	}

	private static BufferedImage createMapImage(final String mapPath) {
		try {
			return ImageIO.read(new File(mapPath));
		} catch (IOException e) {
			throw new RuntimeException("Map missing: " + mapPath);
		}
	}

    public enum Creature {
		LINK_NO_ITEMS("link_no_items"),
        BOKOBLIN("bokoblin"),
		BOKOBLIN_BOW("bokoblin boko club");

		private Creature(String file) {
			String path = "images/objects/" + file + ".png";
			image = ImageCache.getImage(path);

			for (int i = 0; i <= 10; ++i) {
				try {
					variants[i] = ImageCache.toCompatibleImage(ImageIO.read(new File(resourcePath + path)));
					for (int x = 0; x < tileSize; ++x) {
					    for (int y = 0; y < tileSize; ++y) {
                            int rgb = variants[i].getRGB(x, y);
                            if (rgb == 0) continue;
                            int r = (rgb & 0x00ff0000) >> 16;
                            int g = (rgb & 0x0000ff00) >> 8;
                            int b = rgb & 0x000000ff;
                            int w = 25 * i;
                            int r2 = (r * 255 + 255 * w) / (w + 255);
                            int g2 = g * 255 / (w + 255);
                            int b2 = b * 255 / (w + 255);
                            int rgb2 = (255 << 24) + (r2 << 16) + (g2 << 8) + b2;
                            variants[i].setRGB(x, y, rgb2);
                        }
                    }
				} catch (IOException e) {
					// too bad...
				}
			}
		}

		public BufferedImage getImage() {
			return image;
		}

		public BufferedImage getImage(int variant) {
			return variants[variant];
		}

		private final BufferedImage image;
		private final BufferedImage[] variants = new BufferedImage[11];
	}

	private int width;
	private int height;
	private Terrain[][] grid;
	private GameObject[][] objectGrid;
	Character link = new Character(this);
    final Animator animator = new Animator(this);
    private final PriorityQueue<Character> queue = new PriorityQueue<>(Comparator.comparingLong(c -> c.priority));
    private float vision = 11.f;
    private Vision los;

    private final List<Character> enemies = new ArrayList<>();
	
	public Zelda() {
		BufferedImage image = createMapImage(resourcePath + "images/map4.png");
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
		link.cre = Creature.LINK_NO_ITEMS;
		link.hp = 20;
		link.maxHp = 20;
		link.atk = 5;
		link.def = 1;
		placeObject(link, 36, 39);

		Boulder rock = new Boulder(this);
		rock.feature = Feature.ROCK;
		placeObject(rock, 41, 35);

		Character boko = new Character(this);
		boko.cre = Creature.BOKOBLIN;
		boko.hp = 30;
		boko.maxHp = 30;
		boko.atk = 2;
		boko.def = 1;
		boko.speed = 1500;
		placeObject(boko, 37, 42);

		Character boko2 = new Character(this);
		boko2.cre = Creature.BOKOBLIN_BOW;
		boko2.hp = 30;
		boko2.maxHp = 30;
		boko2.atk = 2;
		boko2.def = 1;
		boko2.speed = 150;
		boko2.range = 10;
		placeObject(boko2, 37, 45);

		calculateVision();
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
				if (los.getLightness(px, py) <= 0.f) {
				    g.setColor(Color.BLACK);
				    g.fillRect(x, y, tileSize, tileSize);
				    continue;
                }
				g.drawImage(grid[px][py].getImage(), x, y, null);
				if (objectGrid[px][py] != null) {
					objectGrid[px][py].paint(g, x, y);
				}
			}
		}
		if (arrowIndex >= 0) {
			int x0 = arrowPath.get(Math.max(0, arrowIndex - 9)).x;
			int y0 = arrowPath.get(Math.max(0, arrowIndex - 9)).y;
			int x1 = arrowPath.get(arrowIndex).x;
			int y1 = arrowPath.get(arrowIndex).y;
		    g.setColor(Color.ORANGE);
		    BasicStroke bs = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3, null, 0);
            ((Graphics2D) g).setStroke(bs);
			g.drawLine(x0, y0, x1, y1);
        } else if (aimX != -1 && aimY != -1 && !arrowPath.isEmpty()) {
			int x0 = arrowPath.get(0).x;
			int y0 = arrowPath.get(0).y;
			int x1 = arrowPath.get(arrowPath.size() - 1).x;
			int y1 = arrowPath.get(arrowPath.size() - 1).y;
			g.setColor(Color.ORANGE);
			BasicStroke bs = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3, new float[] {9}, 5);
			((Graphics2D) g).setStroke(bs);
			g.drawLine(x0, y0, x1, y1);
			if (arrowTarget != null) {
				bs = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3, null, 0);
				((Graphics2D) g).setStroke(bs);
				g.drawOval(arrowTarget.x - 8, arrowTarget.y - 8, 16, 16);
			}
		}
	}

	public void placeObject(GameObject o, int x, int y) {
		if (objectGrid[x][y] != null) throw new RuntimeException("Target position " + x + "," + y + " is not empty");
		objectGrid[x][y] = o;
		o.x = x;
		o.y = y;
		if (o instanceof Character && o != link) {
			enemies.add((Character) o);
		}
	}

	public void moveObject(GameObject o, int x, int y) {
		if (objectGrid[o.x][o.y] != o) throw new RuntimeException("Object pmismatch: " + o.x + "," + o.y);
		if (objectGrid[x][y] != null) throw new RuntimeException("Target position " + x + "," + y + " is not empty");
		objectGrid[o.x][o.y] = null;
		objectGrid[x][y] = o;
		o.x = x;
		o.y = y;
		if (o == link) {
			calculateVision();
		}
	}

	public void destroyObject(GameObject o) {
		if (objectGrid[o.x][o.y] != o) throw new RuntimeException("Object pmismatch: " + o.x + "," + o.y);
		objectGrid[o.x][o.y] = null;
		o.x = -1;
		o.y = -1;
		if (o instanceof Character) {
			queue.remove(o);
			enemies.remove(o);
		}
	}
	
	public boolean canMoveTo(int x, int y) {
		final Terrain terrain = getTerrain(x, y);
		boolean free = terrain != null && terrain.isPassable();
		if (free) {
			final GameObject o = getObject(x, y);
			free = o == null || o.isPassable();
		}
		return free;
	}
	
	public Terrain getTerrain(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height ? grid[x][y] : null;
	}
	
	public GameObject getObject(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height ? objectGrid[x][y] : null;
	}

    public void calculateVision() {
        final int minX = Math.max(0, link.x - screenWidth / 2);
        final int maxX = Math.min(width, link.x + screenWidth / 2 + 1);
        final int minY = Math.max(0, link.y - screenHeight / 2);
        final int maxY = Math.min(height, link.y + screenHeight / 2 + 1);
        los = new Vision(minX, link.x, maxX, minY, link.y, maxY, grid, vision);
        los.calculateFOV();
    }

    public Random r = new Random();

	public void nextTurn() {
		final Set<Character> test = new HashSet<>();
		test.addAll(queue);
		test.addAll(enemies);
		if (test.size() != queue.size() + enemies.size()) throw new RuntimeException("Duplicates in queue");
		Character c = queue.remove();
		if (c == link) {
			repaint();
			return;
		}
		if (c.chaseTurns > 0) {
			int dx = link.x - c.x;
			int dy = link.y - c.y;
			if (c.range > 1 && c.range * c.range > dx * dx + dy * dy) {
				int x1 = screenWidth * tileSize / 2;
				int y1 = screenHeight * tileSize / 2;
				int x0 = x1 - dx * tileSize;
				int y0 = y1 - dy * tileSize;
				refreshArrowPath(x0, y0, x1, y1, c);
				if (!arrowPath.isEmpty() && arrowTarget != null) {
					arrowIndex = 0;
					c.priority += c.speed;
					queue.add(c);
					animator.addArrow(arrowPath.size() - 1, c);
					return;
				}
			}
			int dist = Math.max(Math.abs(dx), Math.abs(dy));
			if (dist == 1) {
				c.move(dx, dy);
			} else {
				final List<Integer> dirs = new ArrayList<>();
				for (int i = 0; i < 9; ++i) {
					int x = (i % 3) - 1;
					int y = (i / 3) - 1;
					if (canMoveTo(c.x + x, c.y + y)) {
						int dx2 = c.x + x - link.x;
						int dy2 = c.y + y - link.y;
						int dist2 = Math.max(Math.abs(dx2), Math.abs(dy2));
						if (dist2 < dist) {
							dirs.clear();
							dist = dist2;
							dirs.add(i);
						} else if (dist2 == dist) {
							dirs.add(i);
						}
					}
				}
				if (!dirs.isEmpty()) {
					int dir = dirs.get(r.nextInt(dirs.size()));
					c.move((dir % 3) - 1, (dir / 3) - 1);
				}
			}
		}
		--c.chaseTurns;
		if (los.getLightness(c.x, c.y) > 0.f) {
			c.chaseTurns = 3;
		}
		if (c.chaseTurns > 0) {
			c.priority += c.speed;
			queue.add(c);
		} else {
			enemies.add(c);
		}
		nextTurn();
	}

	public void press(char input) {
		if (arrowIndex >= 0) return;

		aimX = -1;
		aimY = -1;

	    switch (input) {
            case 'q': link.move(-1, -1); break;
            case 'w': link.move(0, -1); break;
            case 'e': link.move(1, -1); break;
            case 'a': link.move(-1, 0); break;
            case 'd': link.move(1, 0); break;
            case 'z': link.move(-1, 1); break;
            case 'x': link.move(0, 1); break;
            case 'c': link.move(1, 1); break;
			case 's': break;
			default: return;
        }
		wakeUpEnemies();
        nextTurn();
	}

	public void wakeUpEnemies() {
		link.priority += link.speed;
		Iterator<Character> it = enemies.iterator();
		while (it.hasNext()) {
			Character c = it.next();
			if (los.getLightness(c.x, c.y) > 0.f) {
				c.priority = link.priority;
				c.chaseTurns = 3;
				queue.add(c);
				it.remove();
			}
		}
		queue.add(link);
	}
	
	public void click(int x, int y) {
		if (arrowIndex >= 0) return;

		mouseOver(x, y);
		if (!arrowPath.isEmpty()) {
			arrowIndex = 0;
			animator.addArrow(arrowPath.size() - 1, link);
			aimX = -1;
			aimY = -1;
		}
	}

	public void mouseOver(int x, int y) {
		if (arrowIndex >= 0) return;

		int tx = x / tileSize;
		int ty = y / tileSize;
		if (tx != aimX || ty != aimY) {
			aimX = -1;
			aimY = -1;
			arrowPath.clear();
			if (tx >= 0 && tx < screenWidth && ty >= 0 && ty < screenHeight) {
				int px = link.x - screenWidth / 2 + tx;
				int py = link.y - screenHeight / 2 + ty;
				if (px >= 0 && px < width && py >= 0 && py < height && los.getLightness(px, py) > 0.f) {
					int x0 = screenWidth * tileSize / 2;
					int y0 = screenHeight * tileSize / 2;
					refreshArrowPath(x0, y0, tx * tileSize + tileSize / 2, ty * tileSize + tileSize / 2, link);
					aimX = tx;
					aimY = ty;
				}
			}
			repaint();
		}
	}

	void hitArrow(Character src) {
		arrowIndex = -1;
		if (arrowTarget != null) {
			int tx = arrowTarget.x / tileSize;
			int ty = arrowTarget.y / tileSize;
			int px = link.x - screenWidth / 2 + tx;
			int py = link.y - screenHeight / 2 + ty;
			src.hit((Character) objectGrid[px][py]);
			arrowTarget = null;
		}
		if (src == link) {
			wakeUpEnemies();
		}
		nextTurn();
	}

	private int aimX = -1;
	private int aimY = -1;
	private final List<Point> arrowPath = new ArrayList<>();
	private Point arrowTarget;
	int arrowIndex = -1;

	private void refreshArrowPath(int x0, int y0, int x1, int y1, Character src) {
		arrowTarget = null;
		arrowPath.clear();
		int dx = Math.abs(x1 - x0);
		int sx = x0 < x1 ? 1 : -1;
		int dy = -Math.abs(y1 - y0);
		int sy = y0 < y1 ? 1 : -1;
		int err = dx + dy;
		while (x0 != x1 || y0 != y1) {
			int tx = x0 / tileSize;
			int ty = y0 / tileSize;
			int px = link.x - screenWidth / 2 + tx;
			int py = link.y - screenHeight / 2 + ty;
			if (!canMoveTo(px, py)) {
				if (objectGrid[px][py] instanceof Character) {
					Character c = (Character) objectGrid[px][py];
					if (c != src) {
						arrowTarget = new Point(tx * tileSize + tileSize / 2, ty * tileSize + tileSize / 2);
						break;
					}
				} else {
					break;
				}
			}
		    arrowPath.add(new Point(x0, y0));
			int e2 = 2 * err;
			if (e2 >= dy) {
				err += dy;
				x0 += sx;
			}
			if (e2 <= dx) {
				err += dx;
				y0 += sy;
			}
		}
	}
}
