package pgs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Editor extends Game {
	private static final long serialVersionUID = 1L;
	private Deque<Pair<Terrain, List<Pair<Integer, Integer>>>> undoStack = new ArrayDeque<>();
	Float vision;
	Terrain tile;
	boolean editMode;
	boolean fillMode;

	public Editor(String mapPath) {
		super(mapPath);
	}

	@Override
	protected float getVelocity(Terrain tile) {
		return 16.0f;
	}
	
	@Override
	protected void calculateVision(int minX, int x, int maxX, int minY, int y, int maxY, float vision) {
		if (this.vision != null) {
			super.calculateVision(minX, x, maxX, minY, y, maxY, vision);
		}
	}
	
	@Override
	protected float getLight(double dx, double dy, int i, int j, float vision) {
		if (this.vision != null) {
			return super.getLight(dx, dy, i, j, vision);
		}
		return 1.0f;
	}

	@Override
	protected float getVision() {
		if (this.vision != null) {
			return this.vision;
		}
		return 30.0f;
	}

	@Override
	protected Renderable checkAndGetRenderable(int x, int y, Renderable fallback) {
		return null;
	}

	@Override
	public void click(int x, int y) {
		if (editMode && tile != null) {
			Pair<Terrain, List<Pair<Integer, Integer>>> modifications = super.modify(x, y, tile, fillMode);
			if (!modifications.second.isEmpty()) {
				undoStack.push(modifications);
				repaint(Simulator.mainArea);
			}
		} else {
			super.click(x, y);
		}
	}

	private List<Integer> showIntegerFormDialog(String title, String message, String[] labels) {
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(message);
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridLayout(labels.length, 2));
	    JTextField[] fields = new JTextField[labels.length];
	    for (int i = 0; i < labels.length; i++) {
	    	fields[i] = new JTextField();
	    	panel.add(new JLabel(labels[i]));
	    	panel.add(fields[i]);
	    }
	    optionPane.add(panel, 1);
		JDialog dialog = optionPane.createDialog(this, title);
		dialog.setVisible(true);
		List<Integer> result = new ArrayList<>(labels.length);
		if (optionPane.getValue() != null && !optionPane.getValue().equals(JOptionPane.CLOSED_OPTION)) {
			try {
				for (JTextField field : fields) {
					result.add(Integer.parseInt(field.getText()));
				}
			} catch (NumberFormatException e) {
				System.err.println("Invalid input: " + e.getMessage());
			}
		}
		return result;
	}

	private void showTileSelectionDialog(Terrain theme) {
		final String message = theme == null ? "Miscallenous" : theme.getName();
		JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(message + " terrain");
		optionPane.setOptions(new String[] {"Cancel"});
	    JPanel panel = new JPanel();
	    List<Terrain> themedTerrains = new ArrayList<>();
	    if (theme != null) {
	    	themedTerrains.add(theme);
	    	for (Terrain terrain : Terrain.values()) {
	    		if (terrain.getTheme() == theme) {
	    			themedTerrains.add(terrain);
	    		}
	    	}
	    } else {
	    	for (Terrain terrain : Terrain.values()) {
	    		if (terrain.getTheme() == null && terrain != Terrain.GRASS &&
    				terrain != Terrain.SAND && terrain != Terrain.SNOW) {
	    			themedTerrains.add(terrain);
	    		}
	    	}
	    }
	    int size = (int) Math.ceil(Math.sqrt(themedTerrains.size()));
	    panel.setLayout(new GridLayout(size, size));
	    final JDialog[] dialogArray = new JDialog[1];
	    for (final Terrain terrain : themedTerrains) {
	    	JButton button = new JButton(new ImageIcon(getImage(terrain)));
	    	button.setBackground(Color.BLACK);
	    	button.setPreferredSize(new Dimension(16, 16));
	    	button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					tile = terrain;
					editMode = true;
					changeCursor();
					dialogArray[0].dispose();
				}
	    	});
	    	panel.add(button);
	    }
	    optionPane.add(panel, 1);
	    JDialog dialog = optionPane.createDialog(this, "Select terrain");
	    dialog.setBackground(Color.BLACK);
	    dialogArray[0] = dialog;
		dialog.setVisible(true);
	}

	private static BufferedImage getImage(Terrain terrain) {
		if (terrain.getTheme() != null) {
			return ImageCache.getLayeredTerrainImage(Arrays.asList(terrain.getTheme().getName(), terrain.getName()));
		} else {
			return ImageCache.getTerrainImage(terrain.getName());
		}
	}

	private void changeCursor() {
		if (editMode && tile != null) {
			Image image = getImage(tile);
			if (fillMode) {
				BufferedImage newImage = new BufferedImage(Terrain.tileSize, Terrain.tileSize, BufferedImage.TYPE_INT_ARGB);
				Graphics g = newImage.getGraphics();
				g.drawImage(image, 0, 0, null);
				g.setColor(new Color(255, 0, 0, 127));
				g.drawOval(0, 0, Terrain.tileSize, Terrain.tileSize);
				g.dispose();
				image = newImage;
			}
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Cursor c = toolkit.createCustomCursor(image, new Point(getX(), getY()), "img");
			setCursor(c);
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
		repaint();
	}

	@Override
	public void press(char c) {
		switch (c) {
		case '1':
			showTileSelectionDialog(Terrain.GRASS);
			break;
		case '2':
			showTileSelectionDialog(Terrain.SAND);
			break;
		case '3':
			showTileSelectionDialog(Terrain.SNOW);
			break;
		case '4':
			showTileSelectionDialog(null);
			break;
		case 'e':
			if (tile != null) {
				editMode = !editMode;
				fillMode = false;
				changeCursor();
			}
			break;
		case 'f':
			if (editMode && tile != null) {
				fillMode = !fillMode;
				changeCursor();
			}
			break;
		case 'j': // Jump to
			stopMoving();
			List<Integer> jumpResult = showIntegerFormDialog("Jump to ...", "Select destination", new String[] {"X:", "Y:"});
			if (jumpResult.size() != 2) {
				break;
			}
			setPosition(jumpResult.get(0), jumpResult.get(1));
			repaint(Simulator.mainArea);
			break;
		case 'l': // Load map
			stopMoving();
			JFileChooser loadFileChooser = new JFileChooser();
			loadFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int loadResult = loadFileChooser.showOpenDialog(this);
			if (loadResult == JFileChooser.APPROVE_OPTION) {
			    File selectedFile = loadFileChooser.getSelectedFile();
				try {
					BufferedImage mapImage = ImageIO.read(selectedFile);
				    loadMap(mapImage, true);
				    clearVisionCache();
				    repaint(Simulator.mainArea);
				} catch (IOException e) {
					System.err.println("Failed to load: " + selectedFile.getAbsolutePath());
				}
			}
			break;
		case 'n': // New map
			stopMoving();
			List<Integer> newResult = showIntegerFormDialog("New map ...", "Select dimensions for the new map", new String[] {"Width:", "Height:"});
			if (newResult.size() != 2) {
				break;
			}
			int x = newResult.get(0);
			int y = newResult.get(1);
			if (x < 20 || x > 1000 || y < 20 || y > 1000) {
				System.err.println("Map too large");
				break;
			}
			BufferedImage blankImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
			Graphics g = blankImage.getGraphics();
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, x, y);
			loadMap(blankImage, true);
			clearVisionCache();
			repaint(Simulator.mainArea);
			break;
		case 's': // Save map
			stopMoving();
			JFileChooser saveFileChooser = new JFileChooser();
			saveFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int saveResult = saveFileChooser.showSaveDialog(this);
			if (saveResult == JFileChooser.APPROVE_OPTION) {
				File selectedFile = saveFileChooser.getSelectedFile();
				BufferedImage mapImage = getMapAsImage();
				try {
					ImageIO.write(mapImage, "png", selectedFile);
				} catch (IOException e) {
					System.err.println("Failed to save: " + selectedFile.getAbsolutePath());
				}
			}
			break;
		case 'u': // undo
			if (!undoStack.isEmpty()) {
				Pair<Terrain, List<Pair<Integer, Integer>>> modifications = undoStack.pop();
				Set<Pair<Integer, Integer>> dirtyPairs = new HashSet<>(modifications.second);
				modify(modifications.second, modifications.first, dirtyPairs);
				repaint(Simulator.mainArea);
			}
			break;
		case 'v':
			vision = vision == null ? 15.0f : null;
			repaint(Simulator.mainArea);
			break;
	    }
	}

	@Override
	protected void paintHeader(Graphics g) {
		super.paintHeader(g);
	}

	@Override
	protected void paintFooter(Graphics g) {
	}

	@Override
	protected void spawn(PokemonSpawnEvent event) {
	}

	public static void main(String[] args) {
		Simulator.mainArea = new Rectangle(0, 0, 800, 800);
		Simulator.windowHeight = 800;
		Simulator.windowWidth = 800;
		Simulator.setGridConstants(0, 0, 11);
		JFrame frame = new JFrame("PGS Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(0, 0, 800, 800);
		final Editor editor = new Editor("images/map.png");
		frame.setContentPane(editor);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
				editor.press(e.getKeyChar());
			}
		});
		editor.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				editor.click(e.getX(), e.getY());
			}
		});
		editor.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				editor.click(e.getX(), e.getY());
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
		frame.setVisible(true);
	}
}
