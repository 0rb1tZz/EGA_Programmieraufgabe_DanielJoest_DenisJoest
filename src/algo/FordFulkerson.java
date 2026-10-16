package algo;

import graph.Edge;
import graph.Graph;
import graph.Node;

import java.util.LinkedList;

public class FordFulkerson extends BaseAlgorithm {


    public FordFulkerson(Graph graph) {
        super(graph);
    }

    /**
     * Implementation of the abstract method getPathToSink().
     * Uses DFS as its search algorithm.
     * @return a path from the source to the target node as a LinkedList of Edges, or null if there is no such path.
     */
    @Override
    public LinkedList<Edge> getPathToSink() {
        LinkedList<Edge> edgePath = new LinkedList<>();
        LinkedList<Node> nodePath = new LinkedList<>();
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        nodePath.add(sourceNode);
        sourceNode.setVisitedInCurrentSearch(true);

        // runs till a path with remaining capacity to target is found
        while (nodePath.peekLast() != targetNode) {
            // if the list is empty there is no path to target with remaining capacity
            if(nodePath.isEmpty())
                return null;

            // test edges of the last node in the path for any nodes to add to the path
            Node currentNode = nodePath.getLast();
            boolean hasOnlyVisitedChildren = true;
            Edge edge;
            while((edge = currentNode.getNextEdge()) != null) {     // looking through the edges of a node
                if(!edge.getTargetNode().isVisitedInCurrentSearch() && edge.getRemainingCapacity() > 0){
                    nodePath.add(edge.getTargetNode());
                    edge.getTargetNode().setVisitedInCurrentSearch(true);
                    edgePath.add(edge);
                    hasOnlyVisitedChildren = false;
                    break;
                }
            }

            // if the dfs runs into a dead end remove the last node and edge, aka backtracks one step
            if (hasOnlyVisitedChildren) {
                nodePath.removeLast();
                if(!edgePath.isEmpty())
                    edgePath.removeLast();
            }
        }

        // resets edge iterator of each node as prep for next algo iteration
        for (Node n: residualGraph.getNodes()){
            n.resetCurrentEdge();
        }
        return edgePath;
    }
}
