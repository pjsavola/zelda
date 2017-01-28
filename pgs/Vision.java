package pgs;

public class Vision {

	final int minX;
	final int minY;
	final int startx;
	final int starty;
	final float radius;
	final int width;
	final int height;
	final float[][] resistanceMap;
	final float[][] lightMap;
	
	public Vision(int minX, int x, int maxX, int minY, int y, int maxY, Terrain[][] grid, float radius) {
		this.minX = minX;
		this.minY = minY;
		startx = x - minX;
		starty = y - minY;
		width = maxX - minX;
		height = maxY - minY;
		resistanceMap = new float[width][height];
		for (int i = minX, rx = 0; i < maxX; i++, rx++) {
			for (int j = minY, ry = 0; j < maxY; j++, ry++) {
				resistanceMap[rx][ry] = grid[i][j].getOpacity();
			}
		}
	    lightMap = new float[width][height];
	    this.radius = radius;
	}
	
	public void calculateFOV() {
	    lightMap[startx][starty] = 1.0f;
        castLight(1, 1.0f, 0.0f, 0, 1, 1, 0);
        castLight(1, 1.0f, 0.0f, 1, 0, 0, 1);
        castLight(1, 1.0f, 0.0f, 0, 1, -1, 0);
        castLight(1, 1.0f, 0.0f, 1, 0, 0, -1);
        castLight(1, 1.0f, 0.0f, 0, -1, 1, 0);
        castLight(1, 1.0f, 0.0f, -1, 0, 0, 1);
        castLight(1, 1.0f, 0.0f, 0, -1, -1, 0);
        castLight(1, 1.0f, 0.0f, -1, 0, 0, -1);
	}
	
	public float getLightness(int x, int y) {
		return lightMap[x - minX][y - minY];
	}
	
	public float getRadius() {
		return radius;
	}
	
	private static float radius(int dx, int dy) {
		return (float) Math.hypot(dx, dy);
	}
	
	private void castLight(int row, float start, float end, int xx, int xy, int yx, int yy) {
	    float newStart = 0.0f;
	    if (start < end) {
	        return;
	    }
	    boolean blocked = false;
	    for (int distance = row; distance <= radius && !blocked; distance++) {
	        int deltaY = -distance;
	        for (int deltaX = -distance; deltaX <= 0; deltaX++) {
	            int currentX = startx + deltaX * xx + deltaY * xy;
	            int currentY = starty + deltaX * yx + deltaY * yy;
	            float leftSlope = (deltaX - 0.5f) / (deltaY + 0.5f);
	            float rightSlope = (deltaX + 0.5f) / (deltaY - 0.5f);
	 
	            if (!(currentX >= 0 && currentY >= 0 && currentX < this.width && currentY < this.height) || start < rightSlope) {
	                continue;
	            } else if (end > leftSlope) {
	                break;
	            }
	 
	            // check if it's within the lightable area and light if needed
	            float r = radius(deltaX, deltaY);
	            if (r <= radius) {
	                float bright = 1.0f - r / radius;
	                float resistance = getResistance(currentX, currentY, r);
	                bright -= resistance;
	                if (bright < 0) {
	                	bright = 0;
	                }
	                lightMap[currentX][currentY] = bright;
	            }
	 
	            if (blocked) { // previous cell was a blocking one
	                if (resistanceMap[currentX][currentY] >= 1) {//hit a wall
	                    newStart = rightSlope;
	                    continue;
	                } else {
	                    blocked = false;
	                    start = newStart;
	                }
	            } else {
	                if (resistanceMap[currentX][currentY] >= 1 && distance < radius) {//hit a wall within sight line
	                    blocked = true;
	                    castLight(distance + 1, start, leftSlope, xx, xy, yx, yy);
	                    newStart = rightSlope;
	                }
	            }
	        }
	    }
	}

	private float getResistance(int x2, int y2, float r) {
		int x1 = startx;
		int y1 = starty;
		float sum = 0;
		int tiles = 0;
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int sx = (x1 < x2) ? 1 : -1;
		int sy = (y1 < y2) ? 1 : -1;
		int err = dx - dy;
		while (true) {
			if (x1 == x2 && y1 == y2) {
				break;
			}
			float res = resistanceMap[x1][y1];
			// res < 1 is in here in order to see walls
			// when you're standing next to them
			if (res < 1 && tiles++ > 0) {
				sum += res;
			}
			int e2 = 2 * err;
			if (e2 > -dy) {
				err -= dy;
				x1 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y1 += sy;
			}
		}
		return sum;
	}
}