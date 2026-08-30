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
     *
     * @return the found path from source to target, or null if there is no such path
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
        while (!toLookAt.isEmpty()) {
            Node current = toLookAt.poll();
            if(current == targetNode){
                return reconstructPath();
            }
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

    private LinkedList<Edge> reconstructPath(){
        LinkedList<Edge> path = new LinkedList<>();
        Node source = residualGraph.getSourceNode();
        Node current = residualGraph.getTargetNode();

        while (source != current){
            path.addFirst(current.getCameFromInCurrentSearch());
            current = current.getCameFromInCurrentSearch().getSourceNode();
        }

        return path;
    }
}
