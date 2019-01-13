package bc19;

public class Path<T> implements Comparable {
    public T point;
    public Double f;
    public Double g;
    public Path parent;

    public Path() {
        parent = null;
        point = null;
        g = f = 0.0;
    }

    public Path(Path p) {
        this();
        parent = p;
        g = p.g;
        f = p.f;
    }

    public int compareTo(Object o) {
        Path p = (Path) o;
        return (int) (f - p.f);
    }

    public T getPoint() {
        return point;
    }

    public void setPoint(T p) {
        point = p;
    }
}