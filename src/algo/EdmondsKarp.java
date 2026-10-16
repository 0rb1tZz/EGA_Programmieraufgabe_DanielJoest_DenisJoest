package algo;

import graph.Edge;
import graph.Graph;
import graph.Node;

import java.util.LinkedList;
import java.util.Queue;

public class EdmondsKarp extends BaseAlgorithm {

    public EdmondsKarp(Graph graph) {
        super(graph);
    }

    /**
     * Implementation of the abstract method getPathToSink().
     * Uses BFS as its search algorithm.
     * @return a path from the source to the target node as a LinkedList of Edges, or null if there is no such path.
     */
    @Override
    public LinkedList<Edge> getPathToSink() {
        Queue<Node> toLookAt = new LinkedList<>();
        LinkedList<Edge> edgePath = new LinkedList<>();
        LinkedList<Node> nodePath = new LinkedList<>();
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        toLookAt.add(sourceNode);
        sourceNode.setVisitedInCurrentSearch(true);

        // runs till a path with remaining capacity to target is found
        while (!toLookAt.isEmpty()) {
            Node current = toLookAt.poll();

            // reconstruct a path from source to target if target node is found
            if(current == targetNode){
                if (!isTestRun)     // the default case
                    return reconstructPath();
                else {              // once sets the shortest path, that is used in test runs to alternate the graph for a different flow value
                    if (residualGraph.getInitialShortestPath() != null){
                        return reconstructPath();
                    }

                    LinkedList<Edge> currentPath = reconstructPath();
                    int[] pathAsIDs = new int[currentPath.size()];
                    int i = 0;
                    for (Edge e: currentPath) {
                        pathAsIDs[i++] = e.id;
                    }
                    residualGraph.setInitialShortestPath(pathAsIDs);
                    return currentPath;
                }
            }

            // for all edges of the current node with remaining capacity:
            // add all nodes that are not already visited to the queue and set them to visited
            for (Edge e : current.getOutgoingEdges()){
                if(!e.getTargetNode().isVisitedInCurrentSearch() && e.getRemainingCapacity() > 0){
                    Node target = e.getTargetNode();
                    toLookAt.add(target);
                    target.setVisitedInCurrentSearch(true);
                    target.setCameFromInCurrentSearch(e);
                }
            }
        }

        return null;
    }

    /**
     * A method used to reconstruct the path from target to source, based on the parameters set in the graph after running BFS.
     * @return a path from source to target.
     */
    private LinkedList<Edge> reconstructPath() {
        LinkedList<Edge> path = new LinkedList<>();
        Node source = residualGraph.getSourceNode();
        Node current = residualGraph.getTargetNode(); // start at the target node

        // traverse backwards until the source node is reached
        // in each iteration: add the node from which the algo reached the current node to the beginning of the list
        // make it the new current node
        while (source != current) {
            path.addFirst(current.getCameFromInCurrentSearch());
            current = current.getCameFromInCurrentSearch().getSourceNode();
        }

        return path;
    }
}
