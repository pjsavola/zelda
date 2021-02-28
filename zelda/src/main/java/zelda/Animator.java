package zelda;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Animator {
    private Deque<Character> chars = new ArrayDeque<>();
    private Deque<Point> arrowPath;
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
                if (arrowPath != null) {
                    if (arrowPath.size() < 12) {
                        arrowPath = null;
                        zelda.a0 = null;
                        zelda.a1 = null;
                    } else {
                        for (int i = 0; i < 12; ++i) {
                            zelda.a1 = arrowPath.removeFirst();
                            if (i == 0) zelda.a0 = zelda.a1;
                        }
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

    public void addArrow(Deque<Point> points) {
        arrowPath = points;
    }

    public void terminate() {
        timer.cancel();
    }
}
