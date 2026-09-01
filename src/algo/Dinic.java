package algo;

import graph.Edge;
import graph.Graph;

import java.util.LinkedList;

public class Dinic extends BaseAlgorithm {

    public Dinic(Graph graph) {
        super(graph);
    }

    @Override
    public LinkedList<Edge> getPathToSink() {
        return null;
    }
}
