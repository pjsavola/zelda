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
	static final int screenWidth = 21;
	static final int screenHeight = 21;
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
		BOKOBLIN_BOW("bokoblin boko club"),
		BOAR("boar"),
		FOX("fox2"),
		MOBLIN("moblin2");

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

	int width;
	int height;
	Terrain[][] grid;
	private GameObject[][] objectGrid;
	private boolean[][] visited;
	Character link;
    final Animator animator = new Animator(this);
    private final PriorityQueue<Character> queue = new PriorityQueue<>(Comparator.comparingLong(c -> c.priority));

    private final List<Character> enemies = new ArrayList<>();
	
	public Zelda() {
		BufferedImage image = createMapImage(resourcePath + "images/map4.png");
		width = image.getWidth();
		height = image.getHeight();
		grid = new Terrain[width][height];
		objectGrid = new GameObject[width][height];
		visited = new boolean[width][height];
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
		link = new Link(this, 37, 45);
		link.cre = Creature.LINK_NO_ITEMS;
		link.hp = 20;
		link.maxHp = 20;
		link.atk = 5;
		link.def = 1;
		link.team = Character.Team.FRIENDLY;
		link.pushing = true;
		//link.swimming = true;

		Boulder rock = new Boulder(this, 41, 35);
		rock.feature = Feature.ROCK;

		Character boko2 = new Character(this, 36, 39);
		boko2.cre = Creature.BOKOBLIN_BOW;
		boko2.hp = 30;
		boko2.maxHp = 30;
		boko2.atk = 2;
		boko2.def = 1;
		boko2.speed = 150;
		boko2.range = 10;
		//boko2.team = Character.Team.NEUTRAL;

		Character boko = new Character(this, 37, 42);
		boko.cre = Creature.BOKOBLIN;
		boko.hp = 30;
		boko.maxHp = 30;
		boko.atk = 2;
		boko.def = 1;
		boko.speed = 150;

		Group.from(boko, boko2);
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
				if (link.canSee(px, py)) {
					g.drawImage(grid[px][py].getImage(), x, y, null);
					if (objectGrid[px][py] != null) {
						objectGrid[px][py].paint(g, x, y);
					}
					if (arrowTarget != null) {
						if (arrowTarget.x == px && arrowTarget.y == py) {
							g.setColor(Color.ORANGE);
							BasicStroke bs = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3, null, 0);
							((Graphics2D) g).setStroke(bs);
							g.drawOval(x + 4, y + 4, 16, 16);
						}
					}
				} else {
					if (visited[px][py]) {
						g.drawImage(grid[px][py].getDarkImage(), x, y, null);
					} else {
						g.setColor(Color.BLACK);
						g.fillRect(x, y, tileSize, tileSize);
					}
                }
			}
		}
		if (arrowIndex >= 0) {
			final int offsetX = Util.center(link.x) - screenWidth * tileSize / 2;
			final int offsetY = Util.center(link.y) - screenHeight * tileSize / 2;
			int x0 = arrowPath.get(Math.max(0, arrowIndex - 9)).x - offsetX;
			int y0 = arrowPath.get(Math.max(0, arrowIndex - 9)).y - offsetY;
			int x1 = arrowPath.get(arrowIndex).x - offsetX;
			int y1 = arrowPath.get(arrowIndex).y - offsetY;
		    g.setColor(Color.ORANGE);
		    BasicStroke bs = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3, null, 0);
            ((Graphics2D) g).setStroke(bs);
			g.drawLine(x0, y0, x1, y1);
        } else if (aimX != -1 && aimY != -1 && !arrowPath.isEmpty()) {
			final int offsetX = Util.center(link.x) - screenWidth * tileSize / 2;
			final int offsetY = Util.center(link.y) - screenHeight * tileSize / 2;
			int x0 = arrowPath.get(0).x - offsetX;
			int y0 = arrowPath.get(0).y - offsetY;
			int x1 = arrowPath.get(arrowPath.size() - 1).x - offsetX;
			int y1 = arrowPath.get(arrowPath.size() - 1).y - offsetY;
			g.setColor(Color.ORANGE);
			BasicStroke bs = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3, new float[] {9}, 5);
			((Graphics2D) g).setStroke(bs);
			g.drawLine(x0, y0, x1, y1);
		}
	}

	public void placeObject(GameObject o, int x, int y) {
		if (objectGrid[x][y] != null) throw new RuntimeException("Target position " + x + "," + y + " is not empty");
		objectGrid[x][y] = o;
		o.x = x;
		o.y = y;
		if (o instanceof Character && !(o instanceof Link)) {
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
		if (o instanceof Character) {
			((Character) o).calculateVision();

		}
	}

	public void swapObjects(GameObject o1, GameObject o2) {
		final int x = o1.x;
		final int y = o1.y;
		objectGrid[x][y] = o2;
		objectGrid[o2.x][o2.y] = o1;
		o1.x = o2.x;
		o1.y = o2.y;
		o2.x = x;
		o2.y = y;
		if (o1 instanceof Character) {
			((Character) o1).calculateVision();
		}
		if (o2 instanceof Character) {
			((Character) o2).calculateVision();
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
	
	public boolean blocksProjectiles(int x, int y) {
		final Terrain terrain = getTerrain(x, y);
		if (terrain == null || terrain.blocksProjectiles()) return true;

		final GameObject o = getObject(x, y);
		return o != null && o.blocksProjectiles();
	}
	
	public Terrain getTerrain(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height ? grid[x][y] : null;
	}
	
	public GameObject getObject(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height ? objectGrid[x][y] : null;
	}

    public void updateExploredArea(Link l) {
		for (int i = 0; i < screenWidth; ++i) {
			int px = l.x - screenWidth / 2 + i;
			if (px < 0 || px >= width) continue;

			for (int j = 0; j < screenHeight; ++j) {
				int py = l.y - screenHeight / 2 + j;
				if (py < 0 || py >= height) continue;

				final int x = i * tileSize;
				final int y = j * tileSize;
				if (l.canSee(px, py)) {
					visited[px][py] = true;
				}
			}
		}
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
		final Character target = c.pickTarget();
		final int cost = c.pickMove();
		if (cost > 0) {
			c.priority += cost;
			queue.add(c);
		} else {
			enemies.add(c);
		}
		if (arrowTarget == null) {
			nextTurn();
		}
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
			case '1': System.err.println(link.pickTarget()); break;
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
			if (c.pickTarget() != null) {
				c.priority = link.priority;
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
				if (px >= 0 && px < width && py >= 0 && py < height && link.canSee(px, py)) {
					link.refreshArrowTarget(px, py);
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
			src.hit(arrowTarget);
			arrowTarget = null;
		}
		if (src == link) {
			wakeUpEnemies();
		}
		nextTurn();
	}

	private int aimX = -1;
	private int aimY = -1;
	final List<Point> arrowPath = new ArrayList<>();
	Character arrowTarget;
	int arrowIndex = -1;

	public int getTileX(int x) {
		int tx = x / tileSize;
		return link.x - screenWidth / 2 + tx;
	}

	public int getTileY(int y) {
		int ty = y / tileSize;
		return link.y - screenHeight / 2 + ty;
	}

	public int centralize(int x) {
		return x * tileSize + tileSize / 2;
	}
}
