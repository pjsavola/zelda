package pgs;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;

public class Simulator {
	
	private static final String mapPath = "images/world.png";
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("PGS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, windowWidth, windowHeight);
		final Game oldGame = load();
		final Game game = oldGame == null ? new Game(mapPath) : oldGame;
		frame.setContentPane(game);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
				game.press(e.getKeyChar());
			}
		});
		game.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				game.click(e.getX(), e.getY());
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
				// Save the game
				save(game);
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

	private static Game load() {
		Game game = null;
		try {
			FileInputStream fileIn = new FileInputStream("game.svg");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			game = (Game) in.readObject();
			in.close();
			fileIn.close();
			game.initialize(false);
		} catch (IOException e) {
			System.err.println("Failed to load the game");
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found");
		}
		return game;
	}

	private static void save(Game game) {
		try {
			FileOutputStream fileOut = new FileOutputStream("game.svg");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(game);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			System.err.println("Failed to save the game");
			e.printStackTrace();
		}
	}

	public static int windowWidth = 480;
	public static int windowHeight = 640;

	// main areas
	public static final Rectangle headerArea = new Rectangle(0, 0, 480, 25);
	public static Rectangle mainArea = new Rectangle(0, 25, 480, 480);
	public static final Rectangle footerArea = new Rectangle(0, 505, 480, 135);

	// header sub areas
	public static final Rectangle xArea = new Rectangle(10, 0, 50, 15);
	public static final Rectangle yArea = new Rectangle(60, 0, 50, 15);
	public static final Rectangle timeArea = new Rectangle(130, 0, 100, 15);
	public static final Rectangle expArea = new Rectangle(250, 0, 140, 15);
	public static final Rectangle levelArea = new Rectangle(410, 0, 60, 15);

	// footer sub areas
	public static final Rectangle journalArea = new Rectangle(10, 505, 460, 125);

	// Grid position related constants
	public static int playerSize;
	public static int middleCornerX;
	public static int middleCornerY;
	public static int middleX;
	public static int middleY;
	public static int playerCornerX;
	public static int playerCornerY;	
	public static void setGridConstants(int x, int y, int size) {
		int gridOffsetX = x;
		int gridOffsetY = y;
		int gridSize = (windowWidth - 1) / Terrain.tileSize; // 29
		playerSize = size;
		middleCornerX = gridOffsetX + gridSize / 2 * Terrain.tileSize;
		middleCornerY = gridOffsetY + gridSize / 2 * Terrain.tileSize;
		middleX = middleCornerX + Terrain.tileSize / 2;
		middleY = middleCornerY + Terrain.tileSize / 2;
		playerCornerX = middleX - playerSize / 2 - 1;
		playerCornerY = middleY - playerSize / 2 - 1;	
	}

	static {
		setGridConstants(8, 30, 11);
	}
}
