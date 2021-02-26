package zelda.tmp;

public enum CatchItem {
	POKE_BALL("Poke ball", 1.0),
	GREAT_BALL("Great ball", 1.5),
	ULTRA_BALL("Ultra ball", 2.0),
	RAZZBERRY("Razzberry", 1.5);
	
	private final String name;
	private final double multiplier;
	
	private CatchItem(String name, double multiplier) {
		this.name = name;
		this.multiplier = multiplier;
	}
	
	public String getName() {
		return name;
	}
	
	public double getMultiplier() {
		return multiplier;
	}
}
