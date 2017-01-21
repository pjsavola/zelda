package pgs;

import java.awt.Graphics;

public class Pokemon {
	private final PokemonKind kind;
	private final int attack;
	private final int defence;
	private final int hitpoints;
	private final int level;
	
	Pokemon(Tile tile) {
		kind = tile.getRandomPokemonKind();
		attack = 0;
		defence = 0;
		hitpoints = 0;
		level = 0;
	}
	
	public void render(Graphics g, int x, int y) {
		kind.render(g, x, y);
	}
}