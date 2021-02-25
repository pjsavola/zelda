package zelda;

public class Pair <T, U> {
	public final T first;
	public final U second;
	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;
        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

	@Override
	public boolean equals(Object o) {
		if (o instanceof Pair) {
			Pair<?, ?> p = (Pair<?, ?>) o;
			if (first == null && p.first == null) {
				return true;
			}
			if (second == null && p.second == null) {
				return true;
			}
			return first.equals(p.first) && second.equals(p.second);
		}
		return super.equals(o);
	}

	@Override
    public String toString()
    { 
		return "(" + first + ", " + second + ")"; 
    }

	public static Pair<Integer, Integer> intPair(int x, int y) {
		return new Pair<Integer, Integer>(x, y);
	}
}
