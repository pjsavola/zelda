package pgs;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Simulator {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("PGS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, windowWidth, windowHeight);
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
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public static final int windowWidth = 480;
	public static final int windowHeight = 640;

	// main areas
	public static final Rectangle headerArea = new Rectangle(0, 0, 480, 25);
	public static final Rectangle mainArea = new Rectangle(0, 25, 480, 480);
	public static final Rectangle footerArea = new Rectangle(0, 505, 480, 135);

	// header sub areas
	public static final Rectangle xArea = new Rectangle(10, 0, 50, 15);
	public static final Rectangle yArea = new Rectangle(60, 0, 50, 15);
	public static final Rectangle timeArea = new Rectangle(130, 0, 100, 15);
	public static final Rectangle expArea = new Rectangle(250, 0, 140, 15);
	public static final Rectangle levelArea = new Rectangle(410, 0, 60, 15);

	// footer sub areas
	public static final Rectangle journalArea = new Rectangle(10, 505, 460, 125);
}
