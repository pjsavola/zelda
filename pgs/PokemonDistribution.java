package pgs;

import java.util.Random;

public enum PokemonDistribution {

	GRASS(new PokemonKind[] {PokemonKind.PIDGEY}, new int[] {100});

	private static Random r = new Random();
	private PokemonKind[] pokemonArray = new PokemonKind[100];
	
	private PokemonDistribution(PokemonKind[] kinds, int[] likelihoods) {
		if (kinds.length != likelihoods.length) {
			throw new RuntimeException("Invalid input for PokemonDistribution");
		}
		int k = 0;
		for (int i = 0; i < kinds.length; i++) {
			for (int j = 0; j < likelihoods[i]; j++, k++) {
				pokemonArray[k] = kinds[i];
			}
		}
	}
	
	public PokemonKind getRandomPokemonKind() {
		return pokemonArray[r.nextInt(100)];
	}
}