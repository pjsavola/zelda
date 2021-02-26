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
import java.util.*;

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
	public static final String resourcePath = Zelda.class.getResource("/").getPath();
	
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
		TREASURE_CHEST("treasure chest");
		
		private Type(String file) {
			image = ImageCache.getImage("images/objects/" + file + ".png");
		}
		
		public BufferedImage getImage() {
			return image;
		}
		
		private final BufferedImage image;			
	}

	public enum Creature {
		LINK_NO_ITEMS("link_no_items"),
        BOKOBLIN("bokoblin");

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
                            //if ((pixel | 0xff000000) == t.getMask()) {
                        }
                    }
				} catch (IOException e) {
					// ok
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
	
	public class GameObject {
		
		protected Type type;
		protected int x;
		protected int y;
		
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

		public void paint(Graphics g, int x, int y) {
			g.drawImage(type.getImage(), x, y, null);
		}
	}
	
	public class Boulder extends GameObject {
		@Override
		public boolean isPushable() {
			return true;
		}
	}

	public class Character extends GameObject {
		int hp;
		int atk;
		int def;
		int maxHp;
		int speed = 100;
		long priority;
		Creature cre;

		float animOpacity = 0.f;
		float animOpacityDropPerSec = 0.f;

		@Override
		public void move(int dx, int dy) {
			final GameObject o = getObject(x + dx, y + dy);
			if (o instanceof Character) {
				Character c = (Character) o;
				if (atk > c.def) {
					c.hp -= atk - c.def;
					if (c.hp <= 0) {
					    c.death();
					} else {
						animator.addGlow(c, 1.0f, 2.f);
					}
				}
			} else {
				super.move(dx, dy);
			}
		}

		@Override
		public void paint(Graphics g, int x, int y) {
			if (animOpacity > 0.f) {
				g.drawImage(cre.getImage((int) (animOpacity * 10)), x, y, null);
			} else {
				g.drawImage(cre.getImage(), x, y, null);
			}
			if (hp < maxHp) {
				g.setColor(Color.BLACK);
				g.drawLine(x + 2, y, x + tileSize - 3, y);
				g.drawLine(x + 2, y + 1, x + tileSize - 3, y + 1);
				if (hp * 4 <= maxHp) g.setColor(Color.RED);
				else if (hp * 2 <= maxHp) g.setColor(Color.ORANGE);
				else if (hp * 4 / 3 <= maxHp) g.setColor(Color.YELLOW);
				else g.setColor(Color.GREEN);
				int width = (hp * (tileSize - 5) + maxHp / 2) / maxHp;
				g.drawLine(x + 2, y, x + width, y);
				g.drawLine(x + 2, y + 1, x + width, y + 1);
			}
		}

		public void death() {
		    queue.remove(this);
            objectGrid[x][y] = null;
        }
	}

    private int width;
	private int height;
	private Terrain[][] grid;
	private GameObject[][] objectGrid;
	private final List<GameObject> objects = new ArrayList<>();
	private Character link = new Character();
    private final Animator animator = new Animator(this);
    private final PriorityQueue<Character> queue = new PriorityQueue<>(Comparator.comparingLong(c -> c.priority));

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
		link.x = 40;
		link.y = 40;
		link.hp = 20;
		link.maxHp = 20;
		link.atk = 5;
		link.def = 1;
		objects.add(link);
		objectGrid[link.x][link.y] = link;
		
		GameObject rock = new Boulder();
		rock.type = Type.ROCK;
		rock.x = 41;
		rock.y = 40;
		objects.add(rock);
		objectGrid[41][35] = rock;

		Character boko = new Character();
		boko.cre = Creature.BOKOBLIN;
		boko.x = 45;
		boko.y = 40;
		boko.hp = 30;
		boko.maxHp = 30;
		boko.atk = 2;
		boko.def = 1;
		boko.speed = 150;
		objectGrid[boko.x][boko.y] = boko;
		enemies.add(boko);



		queue.add(boko);
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
					objectGrid[px][py].paint(g, x, y);
				}
			}
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

	public Random r = new Random();

	public void moveEnemies() {
	    for (Character c : enemies) {
            int dx = link.x - c.x;
            int dy = link.y - c.y;
            if (dx * dx + dy * dy < 100) {
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
        }
    }

    public int getDist(Character c1, Character c2) {
        int dx = c1.x - c2.x;
        int dy = c1.y - c2.y;
        return Math.max(dx, dy);
    }
	
	public void press(char input) {
	    switch (input) {
            case 'q': link.move(-1, -1); break;
            case 'w': link.move(0, -1); break;
            case 'e': link.move(1, -1); break;
            case 'a': link.move(-1, 0); break;
            case 'd': link.move(1, 0); break;
            case 'z': link.move(-1, 1); break;
            case 'x': link.move(0, 1); break;
            case 'c': link.move(1, 1); break;
        }
        link.priority += link.speed;
	    System.err.println("Add link with priority " + link.priority);
        queue.add(link);
        Character c;
        while ((c = queue.remove()) != link) {
            int dx = link.x - c.x;
            int dy = link.y - c.y;
            if (dx * dx + dy * dy < 100) {
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
            c.priority += c.speed;
            System.err.println("Add boko with priority " + c.priority);
            queue.add(c);
        }
		repaint();
	}
	
	public void click(int x, int y) {
		
	}
}
