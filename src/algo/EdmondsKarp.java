package algo;

import graph.Edge;
import graph.Graph;

import java.util.LinkedList;

public class EdmondsKarp extends BaseAlgorithm {

    EdmondsKarp(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return the found path from source to target, or null if there is no such path
     */
    @Override
    public LinkedList<Edge> getPathToSink() {
        return null;
    }
}
