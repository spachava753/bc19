package bc19;

public class Node {
    public int x;
    public int y;

    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Node node) {
        if(x == node.x && y == node.y)
            return true;
        return false;
    }

    public String toString() {
        return "(" + x + ", " + y + ") ";
    }
}