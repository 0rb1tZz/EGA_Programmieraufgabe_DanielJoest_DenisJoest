package graph;

import java.util.LinkedList;
import java.util.Random;

public class Graph {

    private Node sourceNode;
    private Node targetNode;

    private LinkedList<Node> nodes = new LinkedList<Node>();
    private LinkedList<Edge> edges = new LinkedList<Edge>();


    public void addNode(Node node) {
        nodes.add(node);
    }

    /**
     * Adds this edge to the graph and constructs the reverse edge and also adds it.
     * @param edge the edge to add
     */
    public void addEdge(Edge edge, int maxCapacity) { // evtl. random capacity statt identische für reverse Edge
        var reverseEdgeCapacity = new Random().nextInt(maxCapacity);
        Edge reverseEdge = new Edge(edge.getTargetNode(), edge.getSourceNode(), reverseEdgeCapacity);
        edge.setReverseEdge(reverseEdge);
        reverseEdge.setReverseEdge(edge);
        edges.add(edge);
        edges.add(reverseEdge);
    }

    public LinkedList<Node> getNodes() {
        return nodes;
    }

    public LinkedList<Edge> getEdges() {
        return edges;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public void setNodes(LinkedList<Node> nodes) {
        this.nodes = nodes;
    }

    public void setEdges(LinkedList<Edge> edges) {
        this.edges = edges;
    }
}
