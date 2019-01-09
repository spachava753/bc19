package bc19;

public class Node {
    public int x;
    public int y;

    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + ", " + y + ") ";
    }
}