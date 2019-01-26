package bc19;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class AStar<T> {


    protected abstract boolean isGoal(T node);

    protected abstract Double g(T from, T to);

    protected abstract Double h(T from, T to);


    protected abstract List<T> generateSuccessors(T node);


    private PriorityQueue<Path> paths;
    private HashMap<T, Double> mindists;
    private Double lastCost;
    private int expandedCounter;

    public int getExpandedCounter() {
        return expandedCounter;
    }


    public AStar() {
        paths = new PriorityQueue<Path>();
        mindists = new HashMap<T, Double>();
        expandedCounter = 0;
        lastCost = 0.0;
    }


    protected Double f(Path p, T from, T to) {
        Double g = g(from, to) + ((p.parent != null) ? p.parent.g : 0.0);
        Double h = h(from, to);

        p.g = g;
        p.f = g + h;

        return p.f;
    }


    private void expand(Path<T> path) {
        //RobotUtil.log("INSIDE EXPAND()");
        T p = path.getPoint();
        //RobotUtil.log("path.getPoint(): " + p);
        Double min = mindists.get(path.getPoint());
        //RobotUtil.log("MIN IS: " + min);

        if (min == null || min.doubleValue() > path.f.doubleValue()){
            //RobotUtil.log("min is null or min.doubleValue() > path.f.doubleValue()");
            mindists.put(path.getPoint(), path.f);
        }
        else
            return;

        List<T> successors = generateSuccessors(p);
        //RobotUtil.log("GENERATED SUCCESSORS");
        //RobotUtil.log("SUCCESSORS: " + successors);
        //RobotUtil.log("SUCCESSORS size: " + successors.size());

        for (T t : successors) {
            Path newPath = new Path(path);
            newPath.setPoint(t);
            f(newPath, path.getPoint(), t);
            paths.offer(newPath);
        }

        expandedCounter++;
    }


    public Double getCost() {
        return lastCost;
    }


    public List<T> compute(T start) {
        try {
            //RobotUtil.log("setting root path");
            Path root = new Path();
            root.setPoint(start);

            //RobotUtil.log("calculating f function");
            f(root, start, start);

            //RobotUtil.log("expanding root path");
            expand(root);

            //RobotUtil.log("going into for loop");
            for (; ; ) {
                //RobotUtil.log("POLLING PATHS");
                Path<T> p = paths.poll();
                //RobotUtil.log("PATH is: " + p);

                if (p == null) {
                    lastCost = Double.MAX_VALUE;
                    RobotUtil.log("p IS NULL");
                    return null;
                }

                T last = p.getPoint();
                //RobotUtil.log("LAST is: " + last);

                lastCost = p.g;

                if (isGoal(last)) {
                    LinkedList<T> retPath = new LinkedList<T>();

                    for (Path<T> i = p; i != null; i = i.parent) {
                        retPath.addFirst(i.getPoint());
                    }

                    // the first node we are getting is my position, so remove it
                    retPath.remove(0);
                    return retPath;
                }// if the expandedCounter is too large, just return
                else if(expandedCounter > 900){
                    LinkedList<T> retPath = new LinkedList<T>();

                    for (Path<T> i = p; i != null; i = i.parent) {
                        retPath.addFirst(i.getPoint());
                    }

                    // the first node we are getting is my position, so remove it
                    retPath.remove(0);
                    return retPath;
                }

                expand(p);
            }
        } catch (Exception e) {
            RobotUtil.log(e.toString());
        }
        return null;

    }
}
