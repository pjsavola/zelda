package pgs;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;


public class Editor extends Game {
	private static final long serialVersionUID = 1L;

	public Editor(String mapPath) {
		super(mapPath);
	}

	@Override
	protected float getVelocity(Terrain tile) {
		return 16.0f;
	}
	
	@Override
	protected void calculateVision(int minX, int x, int maxX, int minY, int y, int maxY, float vision) {
	}
	
	@Override
	protected float getLight(double dx, double dy, int i, int j) {
		return 1.0f;
	}

	@Override
	protected float getVision() {
		return 30.0f;
	}

	@Override
	protected Renderable checkAndGetRenderable(int x, int y, Renderable fallback) {
		return null;
	}

	@Override
	public void press() {
		System.err.println("yes");
	}
/*
	@Override
	protected void paintHeader(Graphics g) {
	}
*/
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
		Simulator.setGridConstants(8, 30, 11);
		JFrame frame = new JFrame("PGS Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(0, 0, 800, 800);
		final Editor editor = new Editor("images/world.png");
		frame.setContentPane(editor);
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				editor.press();
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyTyped(KeyEvent e) {
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
