package pgs;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Trainer {
	private Map<CatchItem, Integer> catchItemCounts = new HashMap<>();
	private Map<HealItem, Integer> healItemCounts = new HashMap<>();
	private List<Pokemon> pokemonStorage = new ArrayList<>();
	private Map<Pokemon, CaptureData> captureData = new HashMap<>();
	private int level = 1;
	
	public Trainer() {
		for (CatchItem item : CatchItem.values()) {
			catchItemCounts.put(item, 0);
		}
		for (HealItem item : HealItem.values()) {
			healItemCounts.put(item, 0);
		}
		catchItemCounts.put(CatchItem.POKE_BALL, 25);
	}
	
	public void capture(Canvas parent, final Pokemon p, final boolean razzberry) {
		
		// Create options for the dialog window
		CatchItem[] options = CatchItem.values();
		int buttonCount = options.length;
		for (int i = 0; i < options.length; i++) {
			final CatchItem item = options[i];
			int itemCount = catchItemCounts.get(item);
			if (itemCount == 0) {
				buttonCount--;
				options[i] = null;
			} else if (razzberry && item == CatchItem.RAZZBERRY) {
				options[i] = null;
			}
		}

		JOptionPane optionPane = new JOptionPane();
	    optionPane.setIcon(p.getIcon());
	    optionPane.setMessage("CP: " + p.getCombatPower());
	    optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
	    optionPane.setOptions(new Object[] {"Flee"});
	    
	    // Make the parent dialog accessible in button action listeners
	    final JDialog[] dialogArray = new JDialog[1];

	    // Create panel with the buttons
	    JPanel panel = new JPanel();
	    panel.setLayout(new GridLayout(buttonCount, 1));
	    for (int i = 0; i < options.length; i++)
	    {
	    	if (options[i] == null) {
	    		continue;
	    	}
	    	final CatchItem item = options[i];
	    	String buttonText = item.getName() + " (" + catchItemCounts.get(item) + ")";
	    	JButton button = new JButton(buttonText);
	        button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					final JDialog dialog = dialogArray[0];
					catchItemCounts.put(item, catchItemCounts.get(item) - 1);
					if (item == CatchItem.RAZZBERRY) {
						dialog.dispose();
						capture(parent, p, true);
						return;
					}
					CaptureResult result = p.capture(item, razzberry);
					switch (result) {
					case CAPTURED:
						JOptionPane.showMessageDialog(dialog, p.getName() + " caught!");
						dialog.dispose();
						pokemonStorage.add(p);
						break;
					case FREE:
						JOptionPane.showMessageDialog(dialog, "Broke free!");
						dialog.dispose();
						capture(parent, p, false);
						break;
					case ESCAPED:
						JOptionPane.showMessageDialog(dialog, "Broke free and escaped!");
						dialog.dispose();
						break;
					}
				}
	        });
	        panel.add(button);
	    }
	    optionPane.setOptionType(JOptionPane.OK_OPTION);
	    optionPane.add(panel, 1);
	    JDialog dialog = optionPane.createDialog(parent, "Capturing " + p.getName());
	    dialogArray[0] = dialog;
	    dialog.setVisible(true);
	}
	
	public void addCaptureData(Pokemon p, CaptureData data) {
		captureData.put(p, data);
	}
	
	public int getLevel() {
		return level;
	}
}
