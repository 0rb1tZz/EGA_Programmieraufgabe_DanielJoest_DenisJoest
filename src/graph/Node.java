package graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Node {

    private int id;

    private int x;
    private int y;

    private boolean visitedInCurrentSearch;
    private Edge cameFromInCurrentSearch;

    private final List<Edge> outgoingEdges = new LinkedList<Edge>();


    public Node(int id, int x, int y){
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public void addOutgoingEdge(Edge edge){
        outgoingEdges.add(edge);
    }

    public boolean isVisitedInCurrentSearch() {
        return visitedInCurrentSearch;
    }

    public void setVisitedInCurrentSearch(boolean visitedInCurrentSearch) {
        this.visitedInCurrentSearch = visitedInCurrentSearch;
    }

    public void setCameFromInCurrentSearch(Edge e){
        cameFromInCurrentSearch = e;
    }

    public Edge getCameFromInCurrentSearch(){
        return cameFromInCurrentSearch;
    }

    public Node copy(){
        return new Node(this.id, this.x, this.y);
    }

    @Override
    public String toString() {
        return "N: " + id;
    }
}
