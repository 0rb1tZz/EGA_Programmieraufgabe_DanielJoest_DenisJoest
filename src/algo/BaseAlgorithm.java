package algo;

import graph.Edge;
import graph.Graph;
import graph.Node;
import graph.ResidualGraph;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseAlgorithm {

//Schritt 1: basic setup
    //Schritt 2: Pfad holen
    //Schritt 3: kritische Kante finden
    //Schritt 4: flow entlang des Pfades anpassen
    //von Schritt 2 weitermachen

    protected ResidualGraph residualGraph;
    protected boolean noMoreAugmentingPath = false;

    public BaseAlgorithm(Graph graph) {
        residualGraph = new ResidualGraph(graph);
    }

    public int runAlgorithm(){
        while(!noMoreAugmentingPath){
            LinkedList<Edge> path = getPathToSink();
            if(path == null)
                return residualGraph.getMaxFlow();

            int bottleneck = bottleneckOfPath(path);
            for(Edge e: path){
                e.addFlow(bottleneck);
                e.getReverseEdge().addFlow(-bottleneck);
            }
            for(Node n: residualGraph.getNodes())
                n.setVisitedInCurrentSearch(false);
        }
        return residualGraph.getMaxFlow();
    }





    public abstract LinkedList<Edge> getPathToSink();

    public int bottleneckOfPath(List<Edge> path){
        int bottleneck = Integer.MAX_VALUE;
        for(Edge e: path){
            bottleneck = Math.min(e.getRemainingCapacity(), bottleneck);
        }
        return bottleneck;
    }

}
