package pgs;

public enum Terrain {
	
	// Basic terrains
	WATER("Water", null, 0, 0.0f, PokemonDistribution.GRASS, 0x000000ff),
	GRASS("Grass", null, 4.0f, 0.0f, PokemonDistribution.GRASS, 0x0000ff00),
	SAND("Sand", null, 3.0f, 0.0f, PokemonDistribution.GRASS, 0x00ffff00),
	SNOW("Snow", null, 3.0f, 0.0f, PokemonDistribution.GRASS, 0x00ffffff),
	LAVA("Lava", null, 0, 0.0f, PokemonDistribution.GRASS, 0x00ff9600),
	
	// Plants
	BUSHES_GRASS("Bushes", GRASS, 3.5f, 0.03f, PokemonDistribution.GRASS, 0x0000dd00),
	SPARSE_FOREST_GRASS("Sparse forest", GRASS, 3.0f, 0.07f, PokemonDistribution.GRASS, 0x0000bb00),
	FOREST_GRASS("Forest", GRASS, 2.2f, 0.12f, PokemonDistribution.GRASS, 0x00009900),
	THICK_FOREST_GRASS("Thick forest", GRASS, 1.5f, 0.2f, PokemonDistribution.GRASS, 0x00007700),
	BIG_TREE_GRASS("Big tree", GRASS, 1.5f, 0.12f, PokemonDistribution.GRASS, 0x00005500),
	CACTUS("Cactus", SAND, 1.5f, 0.04f, PokemonDistribution.GRASS, 0x0000aa00),
	BUSHES_SNOW("Bushes snow", SNOW, 3.5f, 0.03f, PokemonDistribution.GRASS, 0x0044dd44),
	SPARSE_FOREST_SNOW("Sparse forest snow", SNOW, 3.0f, 0.07f, PokemonDistribution.GRASS, 0x0044bb44),
	FOREST_SNOW("Forest snow", SNOW, 2.2f, 0.12f, PokemonDistribution.GRASS, 0x00449944),
	THICK_FOREST_SNOW("Thick forest snow", SNOW, 1.5f, 0.2f, PokemonDistribution.GRASS, 0x00447744),
	BIG_TREE_SNOW("Big tree snow", SNOW, 1.5f, 0.12f, PokemonDistribution.GRASS, 0x00445544),
	
	// Hilly areas
	HILLS_GRASS("Hills", GRASS, 2.0f, 0.05f, PokemonDistribution.GRASS, 0x00aaaaaa),
	MOUNTAINS_GRASS("Mountains", GRASS, 1.0f, 0.2f, PokemonDistribution.GRASS, 0x00888888),
	HIGH_MOUNTAINS_GRASS("High mountains", GRASS, 0, 0.3f, PokemonDistribution.GRASS, 0x00666666),
	HILLS_SAND("Hills dark", SAND, 2.0f, 0.05f, PokemonDistribution.GRASS, 0x00ccccaa),
	MOUNTAINS_SAND("Mountains dark", SAND, 1.0f, 0.2f, PokemonDistribution.GRASS, 0x00cccc88),
	HIGH_MOUNTAINS_SAND("High mountains dark", SAND, 0, 0.3f, PokemonDistribution.GRASS, 0x00cccc66),
	HILLS_SNOW("Hills dark", SNOW, 2.0f, 0.05f, PokemonDistribution.GRASS, 0x00aaaacc),
	MOUNTAINS_SNOW("Mountains dark", SNOW, 1.0f, 0.2f, PokemonDistribution.GRASS, 0x008888cc),
	HIGH_MOUNTAINS_SNOW("High mountains dark", SNOW, 0, 0.3f, PokemonDistribution.GRASS, 0x006666cc),
	
	// Fields
	EMPTY_FIELD("Empty field", GRASS, 3.0f, 0.0f, PokemonDistribution.GRASS, 0x00884400),
	WHEAT_FIELD("Wheat field", GRASS, 2.0f, 0.1f, PokemonDistribution.GRASS, 0x00ddaa00),
	SWAMP("Swamp", GRASS, 1.0f, 0.03f, PokemonDistribution.GRASS, 0x0000ffff),
	ROCKS_GRASS("Rocks", GRASS, 2.0f, 0.2f, PokemonDistribution.GRASS, 0x00aabbaa),
	ROCKS_SAND("Rocks", SAND, 2.0f, 0.2f, PokemonDistribution.GRASS, 0x00ddffaa),
	ROCKS_SNOW("Rocks", SNOW, 2.0f, 0.2f, PokemonDistribution.GRASS, 0x00aabbcc),

	// Stairs
	STAIRS_B("Stairs B", null, 2.0f, 0.12f, PokemonDistribution.GRASS, 0x00ff0000),
	STAIRS_T("Stairs T", null, 2.0f, 0.12f, PokemonDistribution.GRASS, 0x00cc0000),
	STAIRS_L("Stairs L", null, 2.0f, 0.12f, PokemonDistribution.GRASS, 0x00aa0000),
	STAIRS_R("Stairs R", null, 2.0f, 0.12f, PokemonDistribution.GRASS, 0x00880000),
	
	// Bridges
	BRIDGE("Bridge", null, 6.0f, 0.01f, PokemonDistribution.GRASS, 0x00aa6688),
	DOCKS("Docks", null, 6.0f, 0.01f, PokemonDistribution.GRASS, 0x00aa66bb),
	
	// Floor
	WOODEN_FLOOR("Wooden floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0x00885500),
	WOODEN_FLOOR_V("Wooden floor V", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0x00886600),
	WOODEN_FLOOR_H("Wooden floor H", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0x00775500),
	BRICK_FLOOR("Brick floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0x00bb0000),
	STONE_FLOOR("Stone floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0x00222222),
	STONE_GROUND("Stone ground", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0x00333333),

	// Walls
	DOOR("Door", null, 0, 1.0f, PokemonDistribution.GRASS, 0x00999922),
	RED_WALL("Red wall", null, 0, 1.0f, PokemonDistribution.GRASS, 0x00cc4444),
	ROCK_WALL("Rock wall", null, 0, 1.0f, PokemonDistribution.GRASS, 0x00444444),
	STONE_WALL("Stone wall", null, 0, 1.0f, PokemonDistribution.GRASS, 0x00999999),
	STONE_WALL_DARK("Stone wall dark", null, 0, 1.0f, PokemonDistribution.GRASS, 0x00111111),
	STONE_WALL_WINDOW("Stone wall window", null, 0, 0.05f, PokemonDistribution.GRASS, 0x00eeeeee),
	
	// Road
	ROAD_GRASS("Road", GRASS, 8.0f, 0.0f, PokemonDistribution.GRASS, 0x00aa6600),
	ROAD_SAND("Road", SAND, 8.0f, 0.0f, PokemonDistribution.GRASS, 0x00cc8800),
	ROAD_SNOW("Road", SNOW, 8.0f, 0.0f, PokemonDistribution.GRASS, 0x00aa6644);

	public static final int tileSize = 16;

	private final String name;
	private final float velocity;
	private final float opacity;
	private final PokemonDistribution distribution;
	private final Terrain theme;
	private final int mask;
	
	private Terrain(String name, Terrain theme, float velocity, float opacity, PokemonDistribution distribution, int mask) {
		this.name = name;
		this.velocity = velocity;
		this.opacity = opacity;
		this.distribution = distribution;
		this.theme = theme;
		this.mask = mask;
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
	
	public int getMask() {
		return mask;
	}
}
