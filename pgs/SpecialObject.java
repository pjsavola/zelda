package pgs;

import java.awt.image.BufferedImage;

public enum SpecialObject {
	EMPTY("images/Empty.png", 0xffffffff),
	POKESTOP("images/Pokestop.png", 0x7fffffff),
	START("images/Start.png", 0x64ffffff);

	private final String path;
	private final int alphaMask;

	private SpecialObject(String path, int alphaMask) {
		this.path = path;
		this.alphaMask = alphaMask;
	}

	public BufferedImage getImage() {
		return ImageCache.getImage(path);
	}

	public int getAlphaMask() {
		return alphaMask;
	}
}
