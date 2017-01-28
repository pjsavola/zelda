package pgs;

public enum Terrain {
	
	// Basic terrains
	WATER("Water", 0, 0.0f, PokemonDistribution.GRASS),
	GRASS("Grass", 4.0f, 0.0f, PokemonDistribution.GRASS),
	SAND("Sand", 3.0f, 0.0f, PokemonDistribution.GRASS),
	SNOW("Snow", 3.0f, 0.0f, PokemonDistribution.GRASS),
	
	// Plants
	BUSHES("Bushes", 3.5f, 0.03f, PokemonDistribution.GRASS),
	SPARSE_FOREST("Sparse forest", 3.0f, 0.07f, PokemonDistribution.GRASS),
	FOREST("Forest", 2.2f, 0.12f, PokemonDistribution.GRASS),
	THICK_FOREST("Thick forest", 1.5f, 0.2f, PokemonDistribution.GRASS),
	BIG_TREE("Big tree", 1.5f, 0.12f, PokemonDistribution.GRASS),
	CACTUS("Cactus", 1.5f, 0.04f, PokemonDistribution.GRASS),
	
	// Hilly areas
	HILLS("Hills", 2.0f, 0.05f, PokemonDistribution.GRASS),
	MOUNTAINS("Mountains", 1.0f, 0.2f, PokemonDistribution.GRASS),
	HIGH_MOUNTAINS("High mountains", 0, 0.3f, PokemonDistribution.GRASS),
	
	// Fields
	EMPTY_FIELD("Empty field", 3.0f, 0.0f, PokemonDistribution.GRASS),
	WHEAT_FIELD("Wheat field", 2.0f, 0.1f, PokemonDistribution.GRASS),
	ROCKS("Rocks", 2.0f, 0.2f, PokemonDistribution.GRASS),

	// Stairs
	STAIRS_B("Stairs B", 2.0f, 0.12f, PokemonDistribution.GRASS),
	STAIRS_T("Stairs T", 2.0f, 0.12f, PokemonDistribution.GRASS),
	STAIRS_L("Stairs L", 2.0f, 0.12f, PokemonDistribution.GRASS),
	STAIRS_R("Stairs R", 2.0f, 0.12f, PokemonDistribution.GRASS),
	
	// Bridges
	BRIDGE_H("Stairs H", 6.0f, 0.01f, PokemonDistribution.GRASS),
	BRIDGE_V("Stairs V", 6.0f, 0.01f, PokemonDistribution.GRASS),
	DOCKS_H("Docks H", 6.0f, 0.01f, PokemonDistribution.GRASS),
	DOCKS_V("Docks V", 6.0f, 0.01f, PokemonDistribution.GRASS),
	
	// Floor
	WOODEN_FLOOR("Wooden floor", 5.0f, 0.0f, PokemonDistribution.GRASS),
	WOODEN_FLOOR_V("Wooden floor V", 5.0f, 0.0f, PokemonDistribution.GRASS),
	WOODEN_FLOOR_H("Wooden floor H", 5.0f, 0.0f, PokemonDistribution.GRASS),
	BRICK_FLOOR("Brick floor", 5.0f, 0.0f, PokemonDistribution.GRASS),
	STONE_FLOOR("Stone floor", 5.0f, 0.0f, PokemonDistribution.GRASS),
	STONE_GROUND("Stone ground", 5.0f, 0.0f, PokemonDistribution.GRASS),

	// Walls
	DOOR("Door", 0, 1.0f, PokemonDistribution.GRASS),
	RED_WALL("Red wall", 0, 1.0f, PokemonDistribution.GRASS),
	ROCK_WALL("Rock wall", 0, 1.0f, PokemonDistribution.GRASS),
	STONE_WALL("Stone wall", 0, 1.0f, PokemonDistribution.GRASS),
	STONE_WALL_DARK("Stone wall dark", 0, 1.0f, PokemonDistribution.GRASS),
	STONE_WALL_WINDOW("Stone wall dark", 0, 0.05f, PokemonDistribution.GRASS),
	
	// Road
	ROAD("Road", 8.0f, 0.0f, PokemonDistribution.GRASS),
	
	// Lava
	LAVA("Lava", 0, 0.0f, PokemonDistribution.GRASS);

	public static int tileSize = 16;

	private final String name;
	private final float velocity;
	private final float opacity;
	private final PokemonDistribution distribution;
	
	private Terrain(String name, float velocity, float opacity, PokemonDistribution distribution) {
		this.name = name;
		this.velocity = velocity;
		this.opacity = opacity;
		this.distribution = distribution;
	}
	
	public String getName() {
		return name;
	}
	
	public float getVelocity() {
		return velocity;
	}
	
	public float getOpacity() {
		return opacity;
	}

	public PokemonKind getRandomPokemonKind() {
		return distribution.getRandomPokemonKind();
	}
}
