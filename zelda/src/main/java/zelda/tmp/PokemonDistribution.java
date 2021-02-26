package zelda.tmp;


public enum PokemonDistribution {	

	GRASS(new PokemonKind[] {
			PokemonKind.BULBASAUR, PokemonKind.IVYSAUR, PokemonKind.VENUSAUR,
			PokemonKind.PIDGEY, PokemonKind.PIDGEOTTO, PokemonKind.PIDGEOT,
			PokemonKind.DRATINI, PokemonKind.DRAGONAIR, PokemonKind.DRAGONITE},
			new int[] {10, 6, 3, 32, 20, 10, 10, 6, 3});

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
		return pokemonArray[Randomizer.r.nextInt(100)];
	}
}