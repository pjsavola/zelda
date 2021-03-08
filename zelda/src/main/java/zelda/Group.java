package zelda;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Group {
    private final Set<Character> members = new HashSet<>();

    public static Group from(Character... characters) {
        final Group group = new Group();
        for (Character c : characters) {
            group.add(c);
            c.group = group;
        }
        return group;
    }

    public void remove(Character c) {
        members.remove(c);
        c.group = null;
    }

    public void add(Character c) {
        members.add(c);
        c.group = this;
    }

    public Stream<Character> members() {
        return members.stream();
    }
}
