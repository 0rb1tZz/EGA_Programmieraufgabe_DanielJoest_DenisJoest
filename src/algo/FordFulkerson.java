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
     *
     * @return the found path from source to target, or null if there is no such path
     */
    @Override
    public LinkedList<Edge> getPathToSink() {
        LinkedList<Edge> edgePath = new LinkedList<>();
        LinkedList<Node> nodePath = new LinkedList<>();
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        nodePath.add(sourceNode);
        sourceNode.setVisitedInCurrentSearch(true);
        while (nodePath.peekLast() != targetNode) {
            if(nodePath.isEmpty())
                return null;
            Node currentNode = nodePath.getLast();
            boolean hasOnlyVisitedChildren = true;
            for (Edge edge : currentNode.getOutgoingEdges()) {
                if(!edge.getTargetNode().isVisitedInCurrentSearch() && edge.getRemainingCapacity() > 0){
                    nodePath.add(edge.getTargetNode());
                    edge.getTargetNode().setVisitedInCurrentSearch(true);
                    edgePath.add(edge);
                    hasOnlyVisitedChildren = false;
                    break;
                }
            }
            if (hasOnlyVisitedChildren) {
                nodePath.removeLast();
                if(!edgePath.isEmpty())
                    edgePath.removeLast();
            }
        }

        return edgePath;
    }
}
