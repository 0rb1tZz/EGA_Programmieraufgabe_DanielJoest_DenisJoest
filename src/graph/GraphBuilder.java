package graph;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

import java.util.*;

public class GraphBuilder {
    private final int nodeCount;
    private final int maxCapacity;
    private final Random random;
    private final Graph graph;

    public GraphBuilder(int nodeCount, int maxCapacity, int seed) {
        random = new Random(seed);
        this.nodeCount = nodeCount;
        this.maxCapacity = maxCapacity;
        this.graph = new Graph();
    }

    /**
     * Builds the graph according to the currently stored settings.
     * @param maxX the maxX coordinate for the node
     * @param maxY the maxY coordinate for the node
     * @return the generated graph
     */
    public Graph buildGraph(int maxX, int maxY) {
        generateNodes(nodeCount, maxX, maxY);
        generateEdges();
        return graph;
    }

    /**
     * Generates the nodes of the graph.
     * @param nodeCount the number of nodes to generate
     * @param maxX the maxX coordinate for the node
     * @param maxY the maxY coordinate for the node
     */
    private void generateNodes(int nodeCount, int maxX, int maxY) {
        for (int i = 0; i < nodeCount; i++) {
            graph.addNode(new Node(i, random.nextInt(maxX), random.nextInt(maxY)));
        }
        graph.setSourceNode(graph.getNodes().getFirst());
        graph.setTargetNode(graph.getNodes().getLast());
    }

    private void generateEdges() {
        var nodes = graph.getNodes();

        List<Vector2D> points = new ArrayList<>(nodes.size());
        for (var node : nodes) {
            points.add(new Vector2D(node.getX(), node.getY()));
        }

        DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(points);
        try {
            delaunayTriangulator.triangulate();
        } catch (NotEnoughPointsException e) {
            throw new IllegalStateException("Need at least three points to triangulate", e);
        }

        Map<Vector2D, Node>  pointToNode = new IdentityHashMap<>();
        for (int i = 0; i < nodeCount; i++)
            pointToNode.put(points.get(i), nodes.get(i));

        boolean[][] edgeMap = new boolean[nodeCount][nodeCount];
        for(Triangle2D triangle : delaunayTriangulator.getTriangles()){
            addUndirectedEdgeOnce(pointToNode.get(triangle.a), pointToNode.get(triangle.b), edgeMap);
            addUndirectedEdgeOnce(pointToNode.get(triangle.b), pointToNode.get(triangle.c), edgeMap);
            addUndirectedEdgeOnce(pointToNode.get(triangle.c), pointToNode.get(triangle.a), edgeMap);
        }
    }

    private void addUndirectedEdgeOnce(Node a, Node b, boolean[][] edgeMap) {
        if(edgeMap[a.getId()][b.getId()])
            return;
        edgeMap[a.getId()][b.getId()] = true;
        edgeMap[b.getId()][a.getId()] = true;
        Edge edge = new Edge(a, b, random.nextInt(maxCapacity));
        graph.addEdge(edge,random.nextInt(maxCapacity));
    }


    /**
     * Generates the edges of the graph.
     * First construct all possible edges, then removes any that intersect.
     */
    private void generateEdges2() {
        var nodes = graph.getNodes();

        //Generate all basic edges
        var edges = new LinkedList<Edge>();

        for (int i = 0; i < nodes.size(); i++) {
            var node1 = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                var node2 = nodes.get(j);

                int capacity = random.nextInt(maxCapacity);
                edges.add(new Edge(node1, node2, capacity));
            }
        }

        edges.sort((e1, e2) -> Double.compare(length(e1), length(e2)));

        //test for intersection and add to graph
        for (int i = 0; i < edges.size(); i++) {
            var edge1 = edges.get(i);
            for (int j = i + 1; j < edges.size(); j++) {
                var edge2 = edges.get(j);
                if (intersects(edge1, edge2)) {
                    edges.remove(j);
                    edge2.getSourceNode().getOutgoingEdges().remove(edge2);
                    j--;
                }
            }
            graph.addEdge(edge1, random.nextInt(maxCapacity));
        }
    }

    /**
     * @param e the edge
     * @return the length of the edge
     */
    private double length(Edge e) {
        return Math.sqrt(Math.pow(e.getTargetNode().getX() - e.getSourceNode().getX(), 2) + Math.pow(e.getTargetNode().getY() - e.getSourceNode().getY(), 2));
    }

    /**
     * Checks if two edges intersect.
     * @param e1 the first edge
     * @param e2 the second edge
     * @return true if they intersect, false otherwise
     */
    private boolean intersects(Edge e1, Edge e2) {

        var x1 = e1.getSourceNode().getX();
        var y1 = e1.getSourceNode().getY();
        var x2 = e1.getTargetNode().getX();
        var y2 = e1.getTargetNode().getY();
        var x3 = e2.getSourceNode().getX();
        var y3 = e2.getSourceNode().getY();
        var x4 = e2.getTargetNode().getX();
        var y4 = e2.getTargetNode().getY();

        double uA = ((double) ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
        double uB = ((double) ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))) / ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));

        return uA > 0 && uA < 1 && uB > 0 && uB < 1;
    }

}
