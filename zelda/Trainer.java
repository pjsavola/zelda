package zelda;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Trainer implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int[] expRequired = {
		1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 10000, 10000, 10000,
		15000, 20000, 20000, 20000, 25000, 25000, 50000, 75000, 100000, 125000, 150000,
		190000, 200000, 250000, 300000, 350000, 500000, 500000, 750000, 1000000,
		1250000, 1500000, 2000000, 2500000, 3000000, 5000000};
	
	private Map<CatchItem, Integer> catchItemCounts;
	private Map<HealItem, Integer> healItemCounts;
	private List<Pokemon> pokemonStorage = new ArrayList<>();
	private Map<PokeStop, Integer> stopData = new HashMap<>();
	private Map<PokemonKind, Map<CaptureResult, Integer>> captureStats = new HashMap<>();
	private int level = 1;
	private int exp = 0;
	private final Journal journal = new Journal();

	private static abstract class Generator <T> {
		abstract protected T generate();
	}
	private static class IntegerGenerator extends Generator<Integer> {
		@Override protected Integer generate() { return 0; }
	}
	private static class MapGenerator extends Generator<Map<CaptureResult, Integer>> {
		@Override protected Map<CaptureResult, Integer> generate() {
			return initEnumMap(CaptureResult.values(), new IntegerGenerator());
		}
	}

	private static <T, U> Map<T, U> initEnumMap(T[] values, Generator<U> generator) {
		Map<T, U> m = new HashMap<>();
		for (T item : values) {
			m.put(item, generator.generate());
		}
		return m;
	}

	public Trainer() {
		catchItemCounts = initEnumMap(CatchItem.values(), new IntegerGenerator());
		healItemCounts = initEnumMap(HealItem.values(), new IntegerGenerator());
		captureStats = initEnumMap(PokemonKind.values(), new MapGenerator());
		gainItems(CatchItem.POKE_BALL, 25);
	}

	private String printCaptureStats(PokemonKind kind) {
		final Map<CaptureResult, Integer> stats = captureStats.get(kind);
		Integer captured = stats.get(CaptureResult.CAPTURED);
		Integer escaped = stats.get(CaptureResult.ESCAPED);
		Integer free = stats.get(CaptureResult.FREE);
		return captured + "/" + escaped + "/" + free;
	}

	
	public void modifyCaptureStats(PokemonKind kind, CaptureResult result, int modifier) {
		final Map<CaptureResult, Integer> stats = captureStats.get(kind);
		stats.put(result, stats.get(result) + modifier);
	}

	public void capture(final Game game, final Pokemon p, final boolean razzberry) {
		
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
	    optionPane.setMessage("CP: " + p.getCombatPower() + "\n" + printCaptureStats(p.getKind()));
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
						capture(game, p, true);
						return;
					}
					CaptureResult result = p.capture(item, razzberry);
					switch (result) {
					case CAPTURED:
						JOptionPane.showMessageDialog(dialog, p.getName() + " caught!");
						dialog.dispose();
						pokemonStorage.add(p);
						journal.add(new Journal.Entry(
							p.getName() + " (" + p.getCombatPower() + ") captured!"));
						gainXp(game, 100); // tweak?
						break;
					case FREE:
						JOptionPane.showMessageDialog(dialog, "Broke free!");
						dialog.dispose();
						capture(game, p, false);
						break;
					case ESCAPED:
						JOptionPane.showMessageDialog(dialog, "Broke free and escaped!");
						dialog.dispose();
						journal.add(new Journal.Entry(
								p.getName() + " (" + p.getCombatPower() + ") ran away!"));
						gainXp(game, 25);
						break;
					}
				}
	        });
	        panel.add(button);
	    }
	    optionPane.setOptionType(JOptionPane.OK_OPTION);
	    optionPane.add(panel, 1);
	    JDialog dialog = optionPane.createDialog(game, "Capturing " + p.getName());
	    dialogArray[0] = dialog;
	    dialog.setVisible(true);
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

	public int getTotalExperience() {
		int level = 1;
		int totalExp = exp;
		while (this.level < level++) {
			totalExp += expRequired[level - 1];
		}
		return totalExp;
	}

	private void gainXp(Game game, int xp) {
		journal.amend(" + " + xp + " exp");
		exp += xp;
		while (level <= expRequired.length && exp >= expRequired[level - 1]) {
			exp -= expRequired[level - 1];
			levelUp();
			journal.add(new Journal.Entry("Level up: " + level));
			JOptionPane.showMessageDialog(game, "Level up: " + level);
		}
		game.repaint(Simulator.journalArea);
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

	private static void createRandomItems(int count, int level, Map<CatchItem, Integer> m1, Map<HealItem, Integer> m2) {
		while (count-- > 0) {
			if (level >= 5 && Randomizer.r.nextInt(5) == 0) {
				int bound = 2;
				if (level >= 10) bound++; // super potion
				if (level >= 15) bound++; // hyper potion
				if (level >= 25) bound++; // max potion
				if (level >= 30) bound++; // max revive
				HealItem item = null;
				switch (Randomizer.r.nextInt(bound)) {
				case 0: item = HealItem.POTION; break;
				case 1: item = HealItem.REVIVE; break;
				case 2: item = HealItem.SUPER_POTION; break;
				case 3: item = HealItem.HYPER_POTION; break;
				case 4: item = HealItem.MAX_POTION; break;
				case 5: item = HealItem.MAX_REVIVE; break;
				}
				m2.put(item, m2.get(item) + 1);
			} else {
				int bound = 3;
				if (level >= 8) bound++; // razzberry
				if (level >= 12) bound += 2; // great ball
				if (level >= 20) bound++; // ultra ball
				CatchItem item = null;
				switch (Randomizer.r.nextInt(bound)) {
				case 0:
				case 1:
				case 2: item = CatchItem.POKE_BALL; break;
				case 3: item = CatchItem.RAZZBERRY; break;
				case 4:
				case 5: item = CatchItem.GREAT_BALL; break;
				case 6: item = CatchItem.ULTRA_BALL; break;
				}
				m1.put(item, m1.get(item) + 1);
			}
		}
	}

	public void collect(Game game, PokeStop stop) {
		Integer visitCount = stopData.get(stop);
		if (visitCount == null) {
			visitCount = 0;
		}
		Map<CatchItem, Integer> m1 = initEnumMap(CatchItem.values(), new IntegerGenerator());
		Map<HealItem, Integer> m2 = initEnumMap(HealItem.values(), new IntegerGenerator());
		createRandomItems(Math.max(3, Randomizer.r.nextInt(7)), level, m1, m2);
		String text = "Received: ";
		for (Map.Entry<CatchItem, Integer> e : m1.entrySet()) {
			if (e.getValue() > 0) {
				gainItems(e.getKey(), e.getValue());
				text += e.getValue() + "x " + e.getKey().getName() + ", ";
			}
		}
		for (Map.Entry<HealItem, Integer> e : m2.entrySet()) {
			if (e.getValue() > 0) {
				gainItems(e.getKey(), e.getValue());
				text += e.getValue() + "x " + e.getKey().getName() + ", ";
			}
		}
		journal.add(new Journal.Entry(text.substring(0, text.length() - 2)));
		gainXp(game, Math.max(0, 50 - visitCount));
		stopData.put(stop, ++visitCount);
	}

	public void paintJournal(Graphics g) {
		journal.paint(g);
	}
}
