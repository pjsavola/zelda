package zelda;

public class Util {
    public static int tile(int point) {
        return point / Zelda.tileSize;
    }

    public static int center(int tx) {
        return tx * Zelda.tileSize + Zelda.tileSize / 2;
    }
}
