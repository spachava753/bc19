package bc19;

public class Path implements Comparable {
    public Node point;
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

    public Node getPoint() {
        return point;
    }

    public void setPoint(Node p) {
        point = p;
    }
}