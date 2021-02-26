package zelda;

import java.util.*;

public class Animator {
    private Deque<Zelda.Character> chars = new ArrayDeque<>();
    private final Timer timer = new Timer();

    public Animator(Zelda zelda) {
        final int fps = 20;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!chars.isEmpty())
                {
                    synchronized (this) {
                        final Iterator<Zelda.Character> it = chars.iterator();
                        while (it.hasNext()) {
                            final Zelda.Character c = it.next();
                            c.animOpacity -= c.animOpacityDropPerSec / fps;
                            if (c.animOpacity <= 0.f) {
                                it.remove();
                            }
                        }
                        zelda.repaint();
                    }
                }
            }
        }, 0, 1000 / fps);
    }

    public synchronized void addGlow(Zelda.Character c, float opacity, float lossPerSec) {
        chars.add(c);
        c.animOpacity = Math.min(1.f, opacity);
        c.animOpacityDropPerSec = lossPerSec;
    }

    public void terminate() {
        timer.cancel();
    }
}
