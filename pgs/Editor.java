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

// It was possible to implement an editor for the game itself by overriding a few methods.
public class Editor extends Game {
	private static final long serialVersionUID = 1L;

	// Undo stack for terrain modifications
	private Deque<Pair<Terrain, List<Pair<Integer, Integer>>>> undoStack = new ArrayDeque<>();

	// Vision when the vision mode is enabled, or null if the mode is disabled.
	Float vision;

	// Currently selected Terrain or SpecialObject, or null if nothing is selected
	Object tile;

	// Mode for editing instead of moving when clicking.
	boolean editMode;

	// Mode for filling instead of editing when clicking. 
	boolean fillMode;

	// Keeping track of starting location.
	Pair<Integer, Integer> startingLocation = Pair.intPair(0, 0);

	// Dummy Renderable to render something for the starting location.
	final Renderable start = new Renderable() {
		@Override
		public void click(Game game, Trainer trainer) {
		}
		@Override
		public void event(Game game) {
		}
		@Override
		public boolean isVisible(float light) {
			return true;
		}
		@Override
		public void render(Graphics g, int x, int y) {
			g.drawImage(SpecialObject.START.getImage(), x, y, null);
		}
		@Override
		public int getAlphaMask() {
			return SpecialObject.START.getAlphaMask();
		}
	};

	public Editor(String mapPath) {
		super(createBlankMap(20, 20));
	}

	// Return velocity which is comfortable for editing.
	@Override
	protected float getVelocity(Terrain tile) {
		return 16.0f;
	}

	// Update VisionCache only when vision mode is enabled.
	@Override
	protected void calculateVision(int minX, int x, int maxX, int minY, int y, int maxY, float vision) {
		if (this.vision != null) {
			super.calculateVision(minX, x, maxX, minY, y, maxY, vision);
		}
	}

	// Calculate light only when vision mode is enabled.
	@Override
	protected float getLight(double dx, double dy, int i, int j, float vision) {
		if (this.vision != null) {
			return super.getLight(dx, dy, i, j, vision);
		}
		return 1.0f;
	}

	// Returns vision which is large enough to see whole screen unless vision mode is enabled.
	@Override
	protected float getVision() {
		if (this.vision != null) {
			return this.vision;
		}
		// Just enough for 800 x 800 window.
		return 27.0f;
	}

	// Disables click actions for all Renderables.
	@Override
	protected Renderable checkAndGetRenderable(int x, int y, Renderable fallback) {
		return null;
	}

	// Header contains some obsolete info, but coordinates are useful.
	@Override
	protected void paintHeader(Graphics g) {
		super.paintHeader(g);
	}

	// Show commands in footer.
	@Override
	protected void paintFooter(Graphics g) {
		g.drawString("1: Grass 2: Sand 3: Snow 4: Misc 5: Objects e: Edit f: Fill j: Jump l: Load n: New s: Save u: Undo v: Vision", 80, 795);
	}

	@Override
	public void click(int x, int y) {
		if (editMode && tile != null) {
			if (tile instanceof SpecialObject) {
				createSpecialObject((SpecialObject) tile, x, y);
			} else if (tile instanceof Terrain) {
				createTerrain((Terrain) tile, x, y);
			}
		} else {
			super.click(x, y);
		}
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
		case '5':
			showSpecialObjectSelectionDialog();
			break;
		case 'e':
			if (tile != null) {
				editMode = !editMode;
				fillMode = false;
				changeCursor();
			}
			break;
		case 'f':
			if (editMode && tile instanceof Terrain) {
				fillMode = !fillMode;
				changeCursor();
			}
			break;
		case 'j': // Jump to
			stopMoving();
			List<Integer> jumpResult = showIntegerFormDialog("Jump to ...", "Select destination", new String[] {"X:", "Y:"});
			if (jumpResult.size() == 2 &&
				check(jumpResult.get(0), jumpResult.get(1))) {
				setPosition(Pair.intPair(jumpResult.get(0), jumpResult.get(1)));
				repaint(Simulator.mainArea);
			}
			break;
		case 'l': // Load map
			loadMap();
			break;
		case 'n': // New map
			newMap();
			break;
		case 's': // Save map
			saveMap();
			break;
		case 'u': // undo
			if (!undoStack.isEmpty()) {
				Pair<Terrain, List<Pair<Integer, Integer>>> modifications = undoStack.pop();
				Set<Pair<Integer, Integer>> dirtyPairs = new HashSet<>(modifications.second);
				mutator.modify(modifications.second, modifications.first, dirtyPairs);
				repaint(Simulator.mainArea);
			}
			break;
		case 'v':
			vision = vision == null ? 15.0f : null;
			repaint(Simulator.mainArea);
			break;
	    }
	}

	private void createSpecialObject(SpecialObject o, int x, int y) {
		final Pair<Integer, Integer> indices = mutator.getCursorIndices(x, y);
		if (!check(indices.first, indices.second)) {
			return;
		}
		final boolean isStart = indices.equals(startingLocation);
		switch (o) {
		case EMPTY:
			if (!isStart) {
				mutator.modifyRenderable(indices, null);
			}
			break;
		case POKESTOP:
			if (!isStart) {
				mutator.modifyRenderable(indices, new PokeStop());
			}
			break;
		case START:
			if (!isStart) {
				mutator.modifyRenderable(startingLocation, null);
				mutator.modifyRenderable(indices, start);
			}
			startingLocation = indices;
			break;
		}
		repaint(Simulator.mainArea);
	}

	private void createTerrain(Terrain terrain, int x, int y) {
		final Pair<Terrain, List<Pair<Integer, Integer>>> modifications =
			mutator.modify(x, y, terrain, fillMode);
		if (!modifications.second.isEmpty()) {
			undoStack.push(modifications);
			clearVisionCache();
			repaint(Simulator.mainArea);
		}
	}

	private void resetSettings() {
	    clearVisionCache();
	    tile = null;
	    editMode = false;
	    fillMode = false;
	    repaint(Simulator.mainArea);
	}

	private void loadMap() {
		stopMoving();
		JFileChooser loadFileChooser = new JFileChooser();
		loadFileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int loadResult = loadFileChooser.showOpenDialog(this);
		if (loadResult == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = loadFileChooser.getSelectedFile();
			try {
				BufferedImage mapImage = ImageIO.read(selectedFile);
			    startingLocation = loadMap(mapImage, true);
			    setPosition(startingLocation);
			    mutator.modifyRenderable(startingLocation, start);
			    resetSettings();
			} catch (IOException e) {
				System.err.println("Failed to load: " + selectedFile.getAbsolutePath());
			}
		}
	}

	private void newMap() {
		stopMoving();
		List<Integer> newResult = showIntegerFormDialog("New map ...", "Select dimensions for the new map", new String[] {"Width:", "Height:"});
		if (newResult.size() != 2) {
			return;
		}
		int x = newResult.get(0);
		int y = newResult.get(1);
		if (x < 20 || x > 1000 || y < 20 || y > 1000) {
			System.err.println("Map too large");
			return;
		}
		loadMap(createBlankMap(x, y), true);
		resetSettings();
		repaint(Simulator.mainArea);
	}

	private static BufferedImage createBlankMap(int width, int height) {
		BufferedImage blankImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = blankImage.getGraphics();
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, width, height);
		g.dispose();
		return blankImage;
	}

	private void saveMap() {
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
		final String message = theme == null ? "Miscallenous" : theme.getName();
	    showDialog(themedTerrains, message, "Select terrain");
	}

	private void showSpecialObjectSelectionDialog() {
	    showDialog(Arrays.asList(SpecialObject.values()), "Select object", "Select object");
	}

	private void showDialog(List<?> objects, String title, String message) {
		final JOptionPane optionPane = new JOptionPane();
		optionPane.setMessage(message);
		optionPane.setOptions(new String[] {"Cancel"});
	    final JPanel panel = new JPanel();
	    final int size = (int) Math.ceil(Math.sqrt(objects.size()));
	    panel.setLayout(new GridLayout(size, size));
	    final JDialog[] dialogArray = new JDialog[1];
	    for (final Object object : objects) {
	    	final ImageIcon icon = new ImageIcon(getImage(object));
	    	final JButton button = new JButton(icon);
	    	button.setPreferredSize(new Dimension(Terrain.tileSize, Terrain.tileSize));
	    	button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					tile = object;
					editMode = true;
					changeCursor();
					dialogArray[0].dispose();
					icon.getImage().flush();
				}
	    	});
	    	panel.add(button);
	    }
	    optionPane.add(panel, 1);
	    final JDialog dialog = optionPane.createDialog(this, title);
	    dialogArray[0] = dialog;
		dialog.setVisible(true);
	}

	private static BufferedImage getImage(Object tile) {
		if (tile instanceof Terrain) {
			Terrain terrain = (Terrain) tile;
			if (terrain.getTheme() != null) {
				return ImageCache.getLayeredTerrainImage(Arrays.asList(terrain.getTheme().getName(), terrain.getName()));
			} else {
				return ImageCache.getTerrainImage(terrain.getName());
			}
		} else if (tile instanceof SpecialObject) {
			return ((SpecialObject) tile).getImage();
		}
		throw new RuntimeException("Invalid tile type");
	}

	private void changeCursor() {
		if (editMode && tile != null) {
			Image image = getImage(tile);
			if (fillMode && tile instanceof Terrain) {
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
