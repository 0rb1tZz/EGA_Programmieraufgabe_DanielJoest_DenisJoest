package graph;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class ResidualGraph {

    private Node sourceNode;
    private Node targetNode;

    private LinkedList<Node> nodes = new LinkedList<Node>();
    private LinkedList<Edge> edges = new LinkedList<Edge>();
    private final Map<Node, Node> oldAndNewNodes = new HashMap<Node, Node>();
    private final Map<Edge, Edge> oldAndNewEdges = new LinkedHashMap<Edge, Edge>();


    public ResidualGraph(Graph graph) {
        for(Node node : graph.getNodes()) {
            Node newNode = node.copy();
            nodes.add(newNode);
            oldAndNewNodes.put(node, newNode);
        }
        sourceNode = oldAndNewNodes.get(graph.getSourceNode());
        targetNode = oldAndNewNodes.get(graph.getTargetNode());

        for(int i = 0; i < graph.getEdges().size(); i = i + 2) {
            Edge oldEdge = graph.getEdges().get(i);
            Edge edge = copy(oldEdge);
            Edge revEdge = copy(oldEdge.getReverseEdge());

            edges.add(edge);
            edges.add(revEdge);

            edge.setReverseEdge(revEdge);
            revEdge.setReverseEdge(edge);
            oldAndNewEdges.put(oldEdge, edge);
            oldAndNewEdges.put(oldEdge.getReverseEdge(), revEdge);
        }
    }

    public Edge copy(Edge edge){
        return new Edge(oldAndNewNodes.get(edge.getSourceNode()), oldAndNewNodes.get(edge.getTargetNode()), edge.getCapacity());
    }

    // to be run after the algo terminated to get the final max flow value
    public int getMaxFlow(){
        int flow = 0;
        for(Edge edge : targetNode.getOutgoingEdges()){
            flow += edge.getReverseEdge().getFlow();
        }
        return flow;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public LinkedList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(LinkedList<Node> nodes) {
        this.nodes = nodes;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }
}
