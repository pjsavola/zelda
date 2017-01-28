package pgs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Layers {
	// TODO: Add LAVA
	private static Terrain getThemeTerrain(Terrain terrain) {
		if (terrain == null) {
			return null;
		}
		switch (terrain) {
		case WATER:
			return Terrain.WATER;
		case SAND:
			return Terrain.SAND;
		case SNOW:
			return Terrain.SNOW;
		case LAVA:
			return Terrain.LAVA;
		default:
			return Terrain.GRASS;
		}
	}
	
	private static boolean isHilly(Terrain terrain) {
		return terrain == Terrain.HILLS ||
			terrain == Terrain.MOUNTAINS ||
			terrain == Terrain.HIGH_MOUNTAINS;
	}
	
	private static boolean isRoad(Terrain terrain) {
		return terrain == Terrain.ROAD;
	}
	
	// initializes based on numpad directions, and null at the edges
	private static Terrain[] getAdjacentTerrains(Terrain[][] grid, int x, int y) {
		final Terrain[] n = new Terrain[10];
		n[5] = grid[x][y];
		if (x > 0) {
			n[4] = grid[x - 1][y];
			if (y > 0) n[7] = grid[x - 1][y - 1];
			if (y < grid[x].length - 1) n[1] = grid[x - 1][y + 1];
		}
		if (x < grid.length - 1) {
			n[6] = grid[x + 1][y];
			if (y > 0) n[9] = grid[x + 1][y - 1];
			if (y < grid[x].length - 1) n[3] = grid[x + 1][y + 1];
		}
		if (y > 0) n[8] = grid[x][y - 1];
		if (y < grid[x].length - 1) n[2] = grid[x][y + 1];
		return n;
	}
	
	private static String getHillyImageName(Terrain[] n) {
		final String name = n[5].getName();
		if (isHilly(n[6])) {
			if (isHilly(n[4])) {
				return name;
			} else {
				return name + " R";
			}
		} else if (isHilly(n[4])) {
			return name + " L";
		} else {
			return name + " M";
		}
	}
	
	private static String getRoadImageName(Terrain[] n) {
		final String name = n[5].getName();
		int code = 0;
		for (int i = 0, mul = 1; i < 4; i++, mul *= 2) {
			if (isRoad(n[i * 2 + 2])) {
				code += mul;
			}
		}
		switch (code) {
		case 1: return name + " V";
		case 2: return name + " H";
		case 3: return name + " curve BL";
		case 4: return name + " H";
		case 5: return name + " curve BR";
		case 6: return name + " H";
		case 7: return name + " cross B";
		case 8: return name + " V";
		case 9: return name + " V";
		case 10: return name + " curve TL";
		case 11: return name + " cross L";
		case 12: return name + " curve TR";
		case 13: return name + " cross R";
		case 14: return name + " cross T";
		case 15: return name + " plus";
		default:
		}
		return name;
	}
	
	public static BufferedImage createImage(Terrain[][] grid, int x, int y) {
		final List<String> imageNames = new ArrayList<>();
		final Terrain[] n = getAdjacentTerrains(grid, x, y);
		
		// Add layer for theme terrain
		Terrain t5 = getThemeTerrain(n[5]);
		imageNames.add(t5.getName());
		
		// Add layer for actual image
		if (isHilly(n[5])) {
			imageNames.add(getHillyImageName(n));
		} else if (isRoad(n[5])) {
			imageNames.add(getRoadImageName(n));
		} else {
			imageNames.add(n[5].getName());
		}
		
		// Add layer(s) for corners if suitable
		Terrain t2 = getThemeTerrain(n[2]);
		Terrain t4 = getThemeTerrain(n[4]);
		Terrain t6 = getThemeTerrain(n[6]);
		Terrain t8 = getThemeTerrain(n[8]);
		if (t2 != null && t2 != t5) {
			boolean w = t2 == Terrain.WATER;
			if (t2 == t4 && (w || t2 == getThemeTerrain(n[1])))
				imageNames.add(t2.getName() + " BL");
			if (t2 == t6 && (w || t2 == getThemeTerrain(n[3])))
				imageNames.add(t2.getName() + " BR");
		}
		if (t8 != null && t8 != t5) {
			boolean w = t8 == Terrain.WATER;
			if (t8 == t4 && (w || t8 == getThemeTerrain(n[7])))
				imageNames.add(t8.getName() + " TL");
			if (t8 == t6 && (w || t8 == getThemeTerrain(n[9])))
				imageNames.add(t8.getName() + " TR");
		}

		// No need for layers
		if (imageNames.size() == 1) {
			return ImageCache.getTerrainImage(imageNames.get(0));
		}
		return ImageCache.getLayeredTerrainImage(imageNames);
	}
}