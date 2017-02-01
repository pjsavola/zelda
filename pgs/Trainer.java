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

	private static final int[] expRequired = {
		1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 10000, 10000, 10000,
		15000, 20000, 20000, 20000, 25000, 25000, 50000, 75000, 100000, 125000, 150000,
		190000, 200000, 250000, 300000, 350000, 500000, 500000, 750000, 1000000,
		1250000, 1500000, 2000000, 2500000, 3000000, 5000000};
	
	private Map<CatchItem, Integer> catchItemCounts = new HashMap<>();
	private Map<HealItem, Integer> healItemCounts = new HashMap<>();
	private List<Pokemon> pokemonStorage = new ArrayList<>();
	private Map<Pokemon, CaptureData> captureData = new HashMap<>();
	private Map<PokeStop, Integer> stopData = new HashMap<>();
	private int level = 1;
	private int exp = 0;
	private int cumulativeExp = 0;
	
	public Trainer() {
		for (CatchItem item : CatchItem.values()) {
			catchItemCounts.put(item, 0);
		}
		for (HealItem item : HealItem.values()) {
			healItemCounts.put(item, 0);
		}
		gainItems(CatchItem.POKE_BALL, 25);
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
						gainXp(parent, 100); // tweak?
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
	
	public int getMissingExperience() {
		if (level > expRequired.length) {
			return 0;
		}
		return expRequired[level - 1] - exp;
	}
	
	private void gainXp(Canvas parent, int xp) {
		cumulativeExp += xp;
		exp += xp;
		while (level <= expRequired.length && exp >= expRequired[level - 1]) {
			exp -= expRequired[level - 1];
			levelUp();
			JOptionPane.showMessageDialog(parent, "Level up: " + level);
		}
	}
	
	private void levelUp() {
		switch (++level) {
		case 2:
		case 3:
		case 4:
			gainItems(CatchItem.POKE_BALL, 15);
			break;
		case 5:
			gainItems(HealItem.POTION, 10);
			gainItems(HealItem.REVIVE, 10);
			// 1 incense
			break;
		case 6:
			gainItems(CatchItem.POKE_BALL, 15);
			gainItems(HealItem.POTION, 10);
			gainItems(HealItem.REVIVE, 10);
			// 1 incubator
			break;
		case 7:
			gainItems(CatchItem.POKE_BALL, 15);
			gainItems(HealItem.POTION, 10);
			gainItems(HealItem.REVIVE, 10);
			// 1 incense
			break;
		case 8:
			gainItems(CatchItem.POKE_BALL, 15);
			gainItems(HealItem.POTION, 10);
			gainItems(HealItem.REVIVE, 5);
			gainItems(CatchItem.RAZZBERRY, 10);
			// 1 lure
			break;
		case 9:
			gainItems(CatchItem.POKE_BALL, 15);
			gainItems(HealItem.POTION, 10);
			gainItems(HealItem.REVIVE, 5);
			gainItems(CatchItem.RAZZBERRY, 3);
			// 1 lucky egg
			break;
		case 10:
			gainItems(CatchItem.POKE_BALL, 15);
			gainItems(HealItem.SUPER_POTION, 10);
			gainItems(HealItem.REVIVE, 10);
			gainItems(CatchItem.RAZZBERRY, 10);
			// 1 incense, 1 lucky egg, 1 incubator, 1 lure
			break;
		case 11:
			gainItems(CatchItem.POKE_BALL, 15);
			gainItems(HealItem.SUPER_POTION, 10);
			gainItems(HealItem.REVIVE, 3);
			gainItems(CatchItem.RAZZBERRY, 3);
			break;
		case 12:
			gainItems(CatchItem.GREAT_BALL, 20);
			gainItems(HealItem.SUPER_POTION, 10);
			gainItems(HealItem.REVIVE, 3);
			gainItems(CatchItem.RAZZBERRY, 3);
			break;
		case 13:
		case 14:
			gainItems(CatchItem.GREAT_BALL, 15);
			gainItems(HealItem.SUPER_POTION, 10);
			gainItems(HealItem.REVIVE, 3);
			gainItems(CatchItem.RAZZBERRY, 3);
			break;
		case 15:
			gainItems(CatchItem.GREAT_BALL, 15);
			gainItems(HealItem.HYPER_POTION, 20);
			gainItems(HealItem.REVIVE, 10);
			gainItems(CatchItem.RAZZBERRY, 10);
			// 1 incense, 1 lucky egg, 1 incubator, 1 lure
			break;
		case 16:
		case 17:
		case 18:
		case 19:
			gainItems(CatchItem.GREAT_BALL, 10);
			gainItems(HealItem.HYPER_POTION, 10);
			gainItems(HealItem.REVIVE, 5);
			gainItems(CatchItem.RAZZBERRY, 5);
			break;
		case 20:
			gainItems(CatchItem.ULTRA_BALL, 20);
			gainItems(HealItem.HYPER_POTION, 20);
			gainItems(HealItem.REVIVE, 20);
			gainItems(CatchItem.RAZZBERRY, 20);
			// 2 incense, 2 lucky egg, 2 incubator, 2 lure
			break;
		case 21:
		case 22:
		case 23:
		case 24:
			gainItems(CatchItem.ULTRA_BALL, 10);
			gainItems(HealItem.HYPER_POTION, 10);
			gainItems(HealItem.REVIVE, 10);
			gainItems(CatchItem.RAZZBERRY, 10);
			break;
		case 25:
			gainItems(CatchItem.ULTRA_BALL, 25);
			gainItems(HealItem.MAX_POTION, 20);
			gainItems(HealItem.REVIVE, 15);
			gainItems(CatchItem.RAZZBERRY, 15);
			// 1 incense, 1 lucky egg, 1 incubator, 1 lure
			break;
		case 26:
		case 27:
		case 28:
		case 29:
			gainItems(CatchItem.ULTRA_BALL, 10);
			gainItems(HealItem.MAX_POTION, 15);
			gainItems(HealItem.REVIVE, 10);
			gainItems(CatchItem.RAZZBERRY, 15);
			break;
		case 30:
			gainItems(CatchItem.ULTRA_BALL, 30);
			gainItems(HealItem.MAX_POTION, 20);
			gainItems(HealItem.MAX_REVIVE, 20);
			gainItems(CatchItem.RAZZBERRY, 20);
			// 3 incense, 3 lucky egg, 3 incubator, 3 lure
			break;
		case 31:
		case 32:
		case 33:
		case 34:
			gainItems(CatchItem.ULTRA_BALL, 10);
			gainItems(HealItem.MAX_POTION, 15);
			gainItems(HealItem.MAX_REVIVE, 10);
			gainItems(CatchItem.RAZZBERRY, 15);
			break;
		case 35:
			gainItems(CatchItem.ULTRA_BALL, 30);
			gainItems(HealItem.MAX_POTION, 20);
			gainItems(HealItem.MAX_REVIVE, 20);
			gainItems(CatchItem.RAZZBERRY, 20);
			// 2 incense, 1 lucky egg, 1 incubator, 1 lure
			break;
		case 36:
		case 37:
		case 38:
		case 39:
			gainItems(CatchItem.ULTRA_BALL, 20);
			gainItems(HealItem.MAX_POTION, 20);
			gainItems(HealItem.MAX_REVIVE, 10);
			gainItems(CatchItem.RAZZBERRY, 20);
			break;
		case 40:
			gainItems(CatchItem.ULTRA_BALL, 40);
			gainItems(HealItem.MAX_POTION, 40);
			gainItems(HealItem.MAX_REVIVE, 40);
			gainItems(CatchItem.RAZZBERRY, 40);
			// 4 incense, 4 lucky egg, 4 incubator, 4 lure
			break;
		}
	}
	
	private void gainItems(CatchItem item, int count) {
		Integer oldCount = catchItemCounts.get(item);
		catchItemCounts.put(item, oldCount + count);
	}
	
	private void gainItems(HealItem item, int count) {
		Integer oldCount = healItemCounts.get(item);
		healItemCounts.put(item, oldCount + count);
	}
	
	private void gainRandomItems(int count) {
		while (count-- > 0) {
			if (level >= 5 && Randomizer.r.nextInt(5) == 0) {
				int bound = 2;
				if (level >= 10) bound++; // super potion
				if (level >= 15) bound++; // hyper potion
				if (level >= 25) bound++; // max potion
				if (level >= 30) bound++; // max revive
				switch (Randomizer.r.nextInt(bound)) {
				case 0:
					gainItems(HealItem.POTION, 1);
					break;
				case 1:
					gainItems(HealItem.REVIVE, 1);
					break;
				case 2:
					gainItems(HealItem.SUPER_POTION, 1);
					break;
				case 3:
					gainItems(HealItem.HYPER_POTION, 1);
					break;
				case 4:
					gainItems(HealItem.MAX_POTION, 1);
					break;
				case 5:
					gainItems(HealItem.MAX_REVIVE, 1);
					break;
				}
			} else {
				int bound = 3;
				if (level >= 8) bound++; // razzberry
				if (level >= 12) bound += 2; // great ball
				if (level >= 20) bound++; // ultra ball
				switch (Randomizer.r.nextInt(bound)) {
				case 0:
				case 1:
				case 2:
					gainItems(CatchItem.POKE_BALL, 1);
					break;
				case 3:
					gainItems(CatchItem.RAZZBERRY, 1);
					break;
				case 4:
				case 5:
					gainItems(CatchItem.GREAT_BALL, 1);
					break;
				case 6:
					gainItems(CatchItem.ULTRA_BALL, 1);
					break;
				}
			}
		}
	}
	
	public void collect(Canvas parent, PokeStop stop) {
		Integer visitCount = stopData.get(stop);
		if (visitCount == null) {
			visitCount = 0;
		}
		gainRandomItems(Math.max(3, Randomizer.r.nextInt(7)));
		gainXp(parent, Math.max(0, 50 - visitCount));
		stopData.put(stop, ++visitCount);
	}
}
