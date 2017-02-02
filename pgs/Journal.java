package pgs;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Journal {
	
	public static class Entry {
		private String text;
		public Entry(String text) {
			this.text = text;
		}
	}

	private List<Entry> entries = new ArrayList<>();

	void add(Entry entry) {
		entries.add(0, entry);
	}
	
	void amend(String text) {
		entries.get(0).text += text;
	}
	
	void paint(Graphics g, int x, int y) {
		Color color = Color.BLUE;
		Iterator<Entry> it = entries.iterator();
		int count = 0;
		while (it.hasNext()) {
			Entry entry = it.next();
			g.setColor(color);
			g.drawString(entry.text, x, y + 15 * count);
			color = color.darker();
			if (++count > 4) {
				break;
			}
		}
	}
}
