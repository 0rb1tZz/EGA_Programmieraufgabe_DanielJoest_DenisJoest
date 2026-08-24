package graph;

import java.util.*;
import java.util.function.Function;

public class Graph1<T extends Comparable<T>> {
    private final Function<Node1<T>, Integer> nodeHash;
    public Map<Integer, Node1<T>> nodes = new HashMap<>();
    public Map<Integer, List<Node1<T>>> outgoing = new HashMap<>();

    public Graph1(){
        nodeHash = Node1::getId;
    }

    public Graph1(Function<Node1<T>, Integer> nodeHash){
        this.nodeHash = nodeHash;
    }

    public void addNode(Node1<T> node) {
        nodes.put(nodeHash.apply(node), node);
    }

    public Node1<T> newNode() {
        var node = new Node1<>(this);
        addNode(node);
        return node;
    }

    public Node1<T> newNode(T data) {
        var node = new Node1<>(this);
        node.setData(data);
        addNode(node);
        return node;
    }

    public void newBiEdge(Node1<T> node1, Node1<T> node2) {
        this.outgoing.computeIfAbsent(node1.getId(), k -> new ArrayList<>()).add(node2);
        this.outgoing.computeIfAbsent(node2.getId(), k -> new ArrayList<>()).add(node1);
    }

    public void show(){
        throw new UnsupportedOperationException("The base graph does not have a visualizer.");
    }

    public List<Node1<T>> getNeighbors(Node1<T> node) {
        return outgoing.getOrDefault(node.getId(), List.of());
    }

    public List<Node1<T>> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }
}

