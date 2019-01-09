package bc19;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AStar {


    private LinkedList<Path> paths;
    private HashMap<Node, Double> mindists;
    private Double lastCost;
    private int expandedCounter;

    public AStar() {
        paths = new LinkedList<>();
        mindists = new HashMap<Node, Double>();
        expandedCounter = 0;
        lastCost = 0.0;
    }


    protected abstract boolean isGoal(Node node);

    protected abstract Double g(Node from, Node to);

    protected abstract Double h(Node from, Node to);

    protected abstract List<Node> generateSuccessors(Node node);

    public int getExpandedCounter() {
        return expandedCounter;
    }

    protected Double f(Path p, Node from, Node to) {
        Double g = g(from, to) + ((p.parent != null) ? p.parent.g : 0.0);
        Double h = h(from, to);

        p.g = g;
        p.f = g + h;

        return p.f;
    }

    private void expand(Path path) {
        Node p = path.getPoint();
        Double min = mindists.get(path.getPoint());


        if (min == null || min.doubleValue() > path.f.doubleValue())
            mindists.put(path.getPoint(), path.f);
        else
            return;


        List<Node> successors = generateSuccessors(p);


        for (Node t : successors) {

            Path newPath = new Path(path);
            newPath.setPoint(t);
            f(newPath, path.getPoint(), t);
            paths.add(newPath);
        }

        expandedCounter++;

    }

    public Double getCost() {
        return lastCost;
    }


    public List<Node> compute(Node start) {
        try {
            Path root = new Path();
            root.setPoint(start);

            f(root, start, start);

            expand(root);

            for (; ; ) {
                Path p = paths.poll();

                if (p == null) {
                    lastCost = Double.MAX_VALUE;
                    return null;
                }

                Node last = p.getPoint();

                lastCost = p.g;

                if (isGoal(last)) {
                    LinkedList<Node> retPath = new LinkedList<Node>();

                    for (Path i = p; i != null; i = i.parent) {
                        retPath.addFirst(i.getPoint());
                    }

                    return retPath;
                }
                expand(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
