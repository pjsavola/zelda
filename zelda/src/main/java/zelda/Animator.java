package zelda;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Animator {
    private Deque<Character> chars = new ArrayDeque<>();
    private int maxArrowIndex = -1;
    private final Timer timer = new Timer();

    public Animator(Zelda zelda) {
        final int fps = 20;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!chars.isEmpty())
                {
                    synchronized (this) {
                        final Iterator<Character> it = chars.iterator();
                        while (it.hasNext()) {
                            final Character c = it.next();
                            c.animOpacity -= c.animOpacityDropPerSec / fps;
                            if (c.animOpacity <= 0.f) {
                                it.remove();
                            }
                        }
                        zelda.repaint();
                    }
                }
                if (maxArrowIndex >= 0) {
                    zelda.arrowIndex += 12;
                    if (zelda.arrowIndex > maxArrowIndex) {
                        maxArrowIndex = -1;
                        zelda.hitArrow();
                    }
                    zelda.repaint();
                }
            }
        }, 0, 1000 / fps);
    }

    public synchronized void addGlow(Character c, float opacity, float lossPerSec) {
        chars.add(c);
        c.animOpacity = Math.min(1.f, opacity);
        c.animOpacityDropPerSec = lossPerSec;
    }

    public void addArrow(int maxArrowIndex) {
        this.maxArrowIndex = maxArrowIndex;
    }

    public void terminate() {
        timer.cancel();
    }
}
