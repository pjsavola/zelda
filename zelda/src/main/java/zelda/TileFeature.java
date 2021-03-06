package zelda;

public interface TileFeature {
    default boolean isPassable() {
        return false;
    }
    default boolean blocksFlying() {
        return false;
    }
    default boolean blocksProjectiles() {
        return false;
    }
    default boolean isJumpable() {
        return false;
    }
    default boolean isPushable() {
        return false;
    }
}
