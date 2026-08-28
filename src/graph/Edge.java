package graph;

public class Edge {

    private Node sourceNode;
    private Node targetNode;
    private int capacity;

    private int flow;

    private Edge reverseEdge;


    public Edge(Node n1, Node n2, int capacity){
        sourceNode = n1;
        sourceNode.addOutgoingEdge(this);
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

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public void  addFlow(int flow){
        if (this.flow + flow > capacity){
            throw new IllegalArgumentException("The added flow can't exceed the remaining capacity.");
        }
        this.flow += flow;
    } // reverseEdge.addFlow(-flow); an der jeweiligen Stelle des Aufrufs

    public int getRemainingCapacity(){
        return capacity - flow;
    }

    @Override
    public String toString() {
        return sourceNode.toString() + " -> " + targetNode.toString();
    }
}
