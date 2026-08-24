package graph;

public class Edge {

    private Node sourceNode;
    private Node targetNode;
    private int capacity;

    private Edge reverseEdge;


    public Edge(Node n1, Node n2, int capacity){
        sourceNode = n1;
        targetNode = n2;
        this.capacity = capacity;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Edge getReverseEdge() {
        return reverseEdge;
    }

    public void setReverseEdge(Edge reverseEdge) {
        this.reverseEdge = reverseEdge;
    }

    @Override
    public String toString() {
        return sourceNode.toString() + " -> " + targetNode.toString();
    }
}
