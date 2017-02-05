package pgs;

import java.awt.image.BufferedImage;

public enum SpecialObject {
	EMPTY("images/Empty.png"), POKESTOP("images/Pokestop.png"), START("images/Start.png");

	private final String path;

	private SpecialObject(String path) {
		this.path = path;
	}

	public BufferedImage getImage() {
		return path == null ? null : ImageCache.getImage(path);
	}
}
