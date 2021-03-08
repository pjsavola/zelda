package zelda;

public class Link extends Character {
    public Link(Zelda zelda, int x, int y) {
        super(zelda, x, y);
    }

    @Override
    public void calculateVision() {
        super.calculateVision();
        zelda.updateExploredArea(this);
    }
}
