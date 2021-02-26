package zelda.tmp;

public enum HealItem {
	POTION("Potion", 20, false),
	SUPER_POTION("Super potion", 50, false),
	HYPER_POTION("Hyper potion", 200, false),
	MAX_POTION("Max potion", 1000, false),
	REVIVE("Revive", 0, true),
	MAX_REVIVE("Max revive", 1000, true);
	
	private final String name;
	private final int heal;
	private final boolean revive;
	
	private HealItem(String name, int heal, boolean revive) {
		this.name = name;
		this.heal = heal;
		this.revive = revive;
	}
	
	public String getName() {
		return name;
	}
	
	public int getHeal() {
		return heal;
	}
	
	public boolean isRevive() {
		return revive;
	}
}
