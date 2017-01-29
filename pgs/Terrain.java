package pgs;

public enum Terrain {
	
	// Basic terrains
	WATER("Water", null, 0, 0.0f, PokemonDistribution.GRASS, 0xff0000ff),
	GRASS("Grass", null, 4.0f, 0.0f, PokemonDistribution.GRASS, 0xff00ff00),
	SAND("Sand", null, 3.0f, 0.0f, PokemonDistribution.GRASS, 0xffffff00),
	SNOW("Snow", null, 3.0f, 0.0f, PokemonDistribution.GRASS, 0xffffffff),
	LAVA("Lava", null, 0, 0.0f, PokemonDistribution.GRASS, 0xffff9600),
	
	// Plants
	BUSHES_GRASS("Bushes", GRASS, 3.5f, 0.03f, PokemonDistribution.GRASS, 0xff00dd00),
	SPARSE_FOREST_GRASS("Sparse forest", GRASS, 3.0f, 0.07f, PokemonDistribution.GRASS, 0xff00bb00),
	FOREST_GRASS("Forest", GRASS, 2.2f, 0.12f, PokemonDistribution.GRASS, 0xff009900),
	THICK_FOREST_GRASS("Thick forest", GRASS, 1.5f, 0.2f, PokemonDistribution.GRASS, 0xff007700),
	BIG_TREE_GRASS("Big tree", GRASS, 1.5f, 0.12f, PokemonDistribution.GRASS, 0xff005500),
	CACTUS("Cactus", SAND, 1.5f, 0.04f, PokemonDistribution.GRASS, 0xff00aa00),
	BUSHES_SNOW("Bushes", SNOW, 3.5f, 0.03f, PokemonDistribution.GRASS, 0xff44dd44),
	SPARSE_FOREST_SNOW("Sparse forest", SNOW, 3.0f, 0.07f, PokemonDistribution.GRASS, 0xff44bb44),
	FOREST_SNOW("Forest", SNOW, 2.2f, 0.12f, PokemonDistribution.GRASS, 0xff449944),
	THICK_FOREST_SNOW("Thick forest", SNOW, 1.5f, 0.2f, PokemonDistribution.GRASS, 0xff447744),
	BIG_TREE_SNOW("Big tree", SNOW, 1.5f, 0.12f, PokemonDistribution.GRASS, 0xff445544),
	
	// Hilly areas
	HILLS_GRASS("Hills", GRASS, 2.0f, 0.05f, PokemonDistribution.GRASS, 0xffaaaaaa),
	MOUNTAINS_GRASS("Mountains", GRASS, 1.0f, 0.2f, PokemonDistribution.GRASS, 0xff888888),
	HIGH_MOUNTAINS_GRASS("High mountains", GRASS, 0, 0.3f, PokemonDistribution.GRASS, 0xff666666),
	HILLS_SAND("Hills", SAND, 2.0f, 0.05f, PokemonDistribution.GRASS, 0xffccccaa),
	MOUNTAINS_SAND("Mountains", SAND, 1.0f, 0.2f, PokemonDistribution.GRASS, 0xffcccc88),
	HIGH_MOUNTAINS_SAND("High mountains", SAND, 0, 0.3f, PokemonDistribution.GRASS, 0xffcccc66),
	HILLS_SNOW("Hills", SNOW, 2.0f, 0.05f, PokemonDistribution.GRASS, 0xffaaaacc),
	MOUNTAINS_SNOW("Mountains", SNOW, 1.0f, 0.2f, PokemonDistribution.GRASS, 0xff8888cc),
	HIGH_MOUNTAINS_SNOW("High mountains", SNOW, 0, 0.3f, PokemonDistribution.GRASS, 0xff6666cc),
	
	// Fields
	EMPTY_FIELD("Empty field", GRASS, 3.0f, 0.0f, PokemonDistribution.GRASS, 0xff884400),
	WHEAT_FIELD("Wheat field", GRASS, 2.0f, 0.1f, PokemonDistribution.GRASS, 0xffddaa00),
	ROCKS_GRASS("Rocks", GRASS, 2.0f, 0.2f, PokemonDistribution.GRASS, 0xffaabbaa),
	ROCKS_SAND("Rocks", SAND, 2.0f, 0.2f, PokemonDistribution.GRASS, 0xffddffaa),
	ROCKS_SNOW("Rocks", SNOW, 2.0f, 0.2f, PokemonDistribution.GRASS, 0xffaabbcc),

	// Stairs
	STAIRS_B("Stairs B", null, 2.0f, 0.12f, PokemonDistribution.GRASS, 0xffff0000),
	STAIRS_T("Stairs T", null, 2.0f, 0.12f, PokemonDistribution.GRASS, 0xffcc0000),
	STAIRS_L("Stairs L", null, 2.0f, 0.12f, PokemonDistribution.GRASS, 0xffaa0000),
	STAIRS_R("Stairs R", null, 2.0f, 0.12f, PokemonDistribution.GRASS, 0xff880000),
	
	// Bridges
	BRIDGE_H("Bridge H", null, 6.0f, 0.01f, PokemonDistribution.GRASS, 0xffaa6688),
	BRIDGE_V("Bridge V", null, 6.0f, 0.01f, PokemonDistribution.GRASS, 0xffaa6688),
	DOCKS_H("Docks H", null, 6.0f, 0.01f, PokemonDistribution.GRASS, 0xffaa66aa),
	DOCKS_V("Docks V", null, 6.0f, 0.01f, PokemonDistribution.GRASS, 0xffaa66aa),
	
	// Floor
	WOODEN_FLOOR("Wooden floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0xff885500),
	WOODEN_FLOOR_V("Wooden floor V", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0xff886600),
	WOODEN_FLOOR_H("Wooden floor H", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0xff775500),
	BRICK_FLOOR("Brick floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0xffaa0000),
	STONE_FLOOR("Stone floor", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0xff222222),
	STONE_GROUND("Stone ground", null, 5.0f, 0.0f, PokemonDistribution.GRASS, 0xff333333),

	// Walls
	DOOR("Door", null, 0, 1.0f, PokemonDistribution.GRASS, 0xff999922),
	RED_WALL("Red wall", null, 0, 1.0f, PokemonDistribution.GRASS, 0xffcc4444),
	ROCK_WALL("Rock wall", null, 0, 1.0f, PokemonDistribution.GRASS, 0xff444444),
	STONE_WALL("Stone wall", null, 0, 1.0f, PokemonDistribution.GRASS, 0xff999999),
	STONE_WALL_DARK("Stone wall dark", null, 0, 1.0f, PokemonDistribution.GRASS, 0xff111111),
	STONE_WALL_WINDOW("Stone wall dark", null, 0, 0.05f, PokemonDistribution.GRASS, 0xffeeeeee),
	
	// Road
	ROAD_GRASS("Road", GRASS, 8.0f, 0.0f, PokemonDistribution.GRASS, 0xffaa6600),
	ROAD_SAND("Road", SAND, 8.0f, 0.0f, PokemonDistribution.GRASS, 0xffcc8800),
	ROAD_SNOW("Road", SNOW, 8.0f, 0.0f, PokemonDistribution.GRASS, 0xffaa6644);

	public static int tileSize = 16;

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
