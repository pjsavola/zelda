package pgs;

public class DespawnData {
	int x;
	int y;
	long time;
	
	public DespawnData(int x, int y, long time) {
		this.x = x;
		this.y = y;
		this.time = time + 3600 * 5; // 5 game hours
	}
	
	public boolean despawn(Pokemon[][] grid, long time) {
		if (time > this.time) {
			grid[x][y] = null;
			return true;
		}
		return false;
	}
}
