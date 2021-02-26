package zelda.tmp;

import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;

public class Pokemon implements Renderable, Serializable {
	private static final long serialVersionUID = 1L;

	private static double[] CPM = {
			0.094,
			0.135137432,
			0.16639787,
			0.192650919,
			0.21573247,
			0.236572661,
			0.25572005,
			0.273530381,
			0.29024988,
			0.306057377,
			0.3210876,
			0.335445036,
			0.34921268,
			0.362457751,
			0.37523559,
			0.387592406,
			0.39956728,
			0.411193551,
			0.42250001,
			0.432926419,
			0.44310755,
			0.4530599578,
			0.46279839,
			0.472336083,
			0.48168495,
			0.4908558,
			0.49985844,
			0.508701765,
			0.51739395,
			0.525942511,
			0.53435433,
			0.542635767,
			0.55079269,
			0.558830576,
			0.56675452,
			0.574569153,
			0.58227891,
			0.589887917,
			0.59740001,
			0.604818814,
			0.61215729,
			0.619399365,
			0.62656713,
			0.633644533,
			0.64065295,
			0.647576426,
			0.65443563,
			0.661214806,
			0.667934,
			0.674577537,
			0.68116492,
			0.687680648,
			0.69414365,
			0.700538673,
			0.70688421,
			0.713164996,
			0.71939909,
			0.725571552,
			0.7317,
			0.734741009,
			0.73776948,
			0.740785574,
			0.74378943,
			0.746781211,
			0.74976104,
			0.752729087,
			0.75568551,
			0.758630378,
			0.76156384,
			0.764486065,
			0.76739717,
			0.770297266,
			0.7731865,
			0.776064962,
			0.77893275,
			0.781790055,
			0.78463697,
			0.787473578,
			0.79030001
			};
	
	private static double getCPM(int level) {
		return CPM[2 * (level - 1)];
	}

	private final PokemonKind kind;
	private final int attack;
	private final int defence;
	private final int stamina;
	private final int level;
	private final int x;
	private final int y;
	private CaptureResult status = CaptureResult.FREE;
	private Long clickTime = null;

	Pokemon(Terrain tile, int level, int x, int y) {
		kind = tile.getRandomPokemonKind();
		attack = Randomizer.r.nextInt(16);
		defence = Randomizer.r.nextInt(16);
		stamina = Randomizer.r.nextInt(16);
		this.level = Randomizer.r.nextInt(level) + 1;
		this.x = x;
		this.y = y;
	}

	@Override
	public void render(Graphics g, int x, int y) {
		kind.render(g, x, y);
	}
	
	public boolean isVisible(float light) {
		return light > kind.getVisibilityThreshold();
	}
	
	public String getName() {
		return kind.getName();
	}
	
	public Icon getIcon() {
		return kind.getIcon();
	}
	
	public double getAttack() {
		return (kind.getBaseAttack() + attack) * getCPM(level);
	}
	
	public double getDefence() {
		return (kind.getBaseDefence() + defence) * getCPM(level);
	}
	
	public double getStamina() {
		return (kind.getBaseStamina() + stamina) * getCPM(level);
	}
	
	public int getCombatPower() {
		//System.err.println(getAttack());
		//System.err.println(getDefence());
		//System.err.println(getStamina());
		return Math.max(10, (int) (getAttack() * Math.pow(getDefence(), 0.5) * Math.pow(getStamina(), 0.5) / 10));
	}
	
	public CaptureResult capture(CatchItem item, boolean razzberry) {
		double multiplier = item.getMultiplier();
		if (razzberry) {
			multiplier *= CatchItem.RAZZBERRY.getMultiplier();
		}
		double bcr = kind.getBasicCaptureRate();
		double fail = Math.pow(1 - 0.5 * bcr / getCPM(level), multiplier);
		multiplier *= Randomizer.r.nextDouble() * 3.4;
		System.err.println("Failure chance: " + fail);
		if (fail < Randomizer.r.nextDouble()) {
			status = CaptureResult.CAPTURED;
		} else if (Randomizer.r.nextDouble() < kind.getFleeRate()) {
			status = CaptureResult.ESCAPED;
		}
		return status;
	}
	
	public CaptureResult getStatus() {
		return status;
	}

	@Override
	public void click(Game game, Trainer trainer) {
		trainer.capture(game, this, false);
		final CaptureResult result = getStatus();
		if (clickTime == null) {
			trainer.modifyCaptureStats(kind, result, 1);
		}
		if (result != CaptureResult.FREE) {
			event(game);
			if (clickTime != null) {
				trainer.modifyCaptureStats(kind, CaptureResult.FREE, -1);
				trainer.modifyCaptureStats(kind, result, 1);
			}
			game.repaint(Simulator.mainArea);
		}
		clickTime = game.getTimer().getTime();
	}

	@Override
	public void event(Game game) {
		game.removeRenderable(x, y);
	}
	
	public PokemonKind getKind() {
		return kind;
	}

	@Override
	public int getAlphaMask() {
		return 0xffffffff;
	}
}