package pgs;

public enum Terrain {
	
	// Basic terrains
	WATER("Water", null, 0, 0.0f, PokemonDistribution.GRASS),
	GRASS("Grass", null, 4.0f, 0.0f, PokemonDistribution.GRASS),
	SAND("Sand", null, 3.0f, 0.0f, PokemonDistribution.GRASS),
	SNOW("Snow", null, 3.0f, 0.0f, PokemonDistribution.GRASS),
	
	// Plants
	BUSHES_GRASS("Bushes", GRASS, 3.5f, 0.03f, PokemonDistribution.GRASS),
	SPARSE_FOREST_GRASS("Sparse forest", GRASS, 3.0f, 0.07f, PokemonDistribution.GRASS),
	FOREST_GRASS("Forest", GRASS, 2.2f, 0.12f, PokemonDistribution.GRASS),
	THICK_FOREST_GRASS("Thick forest", GRASS, 1.5f, 0.2f, PokemonDistribution.GRASS),
	BIG_TREE_GRASS("Big tree", GRASS, 1.5f, 0.12f, PokemonDistribution.GRASS),
	CACTUS("Cactus", SAND, 1.5f, 0.04f, PokemonDistribution.GRASS),
	BUSHES_SNOW("Bushes", SNOW, 3.5f, 0.03f, PokemonDistribution.GRASS),
	SPARSE_FOREST_SNOW("Sparse forest", SNOW, 3.0f, 0.07f, PokemonDistribution.GRASS),
	FOREST_SNOW("Forest", SNOW, 2.2f, 0.12f, PokemonDistribution.GRASS),
	THICK_FOREST_SNOW("Thick forest", SNOW, 1.5f, 0.2f, PokemonDistribution.GRASS),
	BIG_TREE_SNOW("Big tree", SNOW, 1.5f, 0.12f, PokemonDistribution.GRASS),
	
	// Hilly areas
	HILLS_GRASS("Hills", GRASS, 2.0f, 0.05f, PokemonDistribution.GRASS),
	MOUNTAINS_GRASS("Mountains", GRASS, 1.0f, 0.2f, PokemonDistribution.GRASS),
	HIGH_MOUNTAINS_GRASS("High mountains", GRASS, 0, 0.3f, PokemonDistribution.GRASS),
	HILLS_SAND("Hills", SAND, 2.0f, 0.05f, PokemonDistribution.GRASS),
	MOUNTAINS_SAND("Mountains", SAND, 1.0f, 0.2f, PokemonDistribution.GRASS),
	HIGH_MOUNTAINS_SAND("High mountains", SAND, 0, 0.3f, PokemonDistribution.GRASS),
	HILLS_SNOW("Hills", SNOW, 2.0f, 0.05f, PokemonDistribution.GRASS),
	MOUNTAINS_SNOW("Mountains", SNOW, 1.0f, 0.2f, PokemonDistribution.GRASS),
	HIGH_MOUNTAINS_SNOW("High mountains", SNOW, 0, 0.3f, PokemonDistribution.GRASS),
	
	// Fields
	EMPTY_FIELD_GRASS("Empty field", GRASS, 3.0f, 0.0f, PokemonDistribution.GRASS),
	WHEAT_FIELD_GRASS("Wheat field", GRASS, 2.0f, 0.1f, PokemonDistribution.GRASS),
	ROCKS_GRASS("Rocks", GRASS, 2.0f, 0.2f, PokemonDistribution.GRASS),
	EMPTY_FIELD_SAND("Empty field", SAND, 3.0f, 0.0f, PokemonDistribution.GRASS),
	WHEAT_FIELD_SAND("Wheat field", SAND, 2.0f, 0.1f, PokemonDistribution.GRASS),
	ROCKS_SAND("Rocks", SAND, 2.0f, 0.2f, PokemonDistribution.GRASS),
	EMPTY_FIELD_SNOW("Empty field", SNOW, 3.0f, 0.0f, PokemonDistribution.GRASS),
	WHEAT_FIELD_SNOW("Wheat field", SNOW, 2.0f, 0.1f, PokemonDistribution.GRASS),
	ROCKS_SNOW("Rocks", SNOW, 2.0f, 0.2f, PokemonDistribution.GRASS),

	// Stairs
	STAIRS_B("Stairs B", null, 2.0f, 0.12f, PokemonDistribution.GRASS),
	STAIRS_T("Stairs T", null, 2.0f, 0.12f, PokemonDistribution.GRASS),
	STAIRS_L("Stairs L", null, 2.0f, 0.12f, PokemonDistribution.GRASS),
	STAIRS_R("Stairs R", null, 2.0f, 0.12f, PokemonDistribution.GRASS),
	
	// Bridges
	BRIDGE_H("Stairs H", null, 6.0f, 0.01f, PokemonDistribution.GRASS),
	BRIDGE_V("Stairs V", null, 6.0f, 0.01f, PokemonDistribution.GRASS),
	DOCKS_H("Docks H", null, 6.0f, 0.01f, PokemonDistribution.GRASS),
	DOCKS_V("Docks V", null, 6.0f, 0.01f, PokemonDistribution.GRASS),
	
	// Floor
	WOODEN_FLOOR("Wooden floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS),
	WOODEN_FLOOR_V("Wooden floor V", null, 5.0f, 0.0f, PokemonDistribution.GRASS),
	WOODEN_FLOOR_H("Wooden floor H", null, 5.0f, 0.0f, PokemonDistribution.GRASS),
	BRICK_FLOOR("Brick floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS),
	STONE_FLOOR("Stone floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS),
	STONE_GROUND("Stone ground", null, 5.0f, 0.0f, PokemonDistribution.GRASS),

	// Walls
	DOOR("Door", null, 0, 1.0f, PokemonDistribution.GRASS),
	RED_WALL("Red wall", null, 0, 1.0f, PokemonDistribution.GRASS),
	ROCK_WALL("Rock wall", null, 0, 1.0f, PokemonDistribution.GRASS),
	STONE_WALL("Stone wall", null, 0, 1.0f, PokemonDistribution.GRASS),
	STONE_WALL_DARK("Stone wall dark", null, 0, 1.0f, PokemonDistribution.GRASS),
	STONE_WALL_WINDOW("Stone wall dark", null, 0, 0.05f, PokemonDistribution.GRASS),
	
	// Road
	ROAD_GRASS("Road", GRASS, 8.0f, 0.0f, PokemonDistribution.GRASS),
	ROAD_SAND("Road", SAND, 8.0f, 0.0f, PokemonDistribution.GRASS),
	ROAD_SNOW("Road", SNOW, 8.0f, 0.0f, PokemonDistribution.GRASS),
	
	// Lava
	LAVA("Lava", null, 0, 0.0f, PokemonDistribution.GRASS);

	public static int tileSize = 16;

	private final String name;
	private final float velocity;
	private final float opacity;
	private final PokemonDistribution distribution;
	private final Terrain theme;
	
	private Terrain(String name, Terrain theme, float velocity, float opacity, PokemonDistribution distribution) {
		this.name = name;
		this.velocity = velocity;
		this.opacity = opacity;
		this.distribution = distribution;
		this.theme = theme;
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
	
	public Terrain getTheme() {
		return theme;
	}
}
