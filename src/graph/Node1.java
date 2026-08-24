package graph;

import java.util.List;

public class Node1<T extends Comparable<T>> implements Comparable<Node1<T>> {
    private final Graph1<T> graph;
    private final int id;
    private static int idCounter = 0;
    private T data;
    private String label;

    protected Node1(Graph1<T> graph) {
        this.graph = graph;
        this.id = idCounter++;
    }

    public int getId() {
        return id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public int compareTo(Node1<T> otherNode) {
        return this.data.compareTo(otherNode.data);
    }

    public List<Node1<T>> getNeighbors() {
        return graph.getNeighbors(this);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
