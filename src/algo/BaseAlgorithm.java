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


    // protected ResidualGraph residualGraph;
    protected Graph residualGraph;
    protected LinkedList<Edge> currentAugmentingPath = new LinkedList<Edge>();
    protected boolean noMoreAugmentingPath = false;
    private boolean takeStep = true;
    private boolean autoRun = true; //false;

    public BaseAlgorithm(Graph graph) {
        residualGraph = graph; // residualGraph = new ResidualGraph(graph);
    }

    public int runAlgorithm() throws InterruptedException {
        while(!noMoreAugmentingPath){
            System.out.println("Iter");
            Thread.sleep(400);
            while(!takeStep && !autoRun){
                Thread.sleep(400);
            }
            takeStep = false;

            //LinkedList<Edge> path = getPathToSink(); // und alle "currentAugmentingPath" ersetzen
            currentAugmentingPath = getPathToSink();
            if(currentAugmentingPath == null)
                return residualGraph.getMaxFlow();

            int bottleneck = bottleneckOfPath(currentAugmentingPath);
            for(Edge e: currentAugmentingPath){
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

    public void setTakeStep(boolean takeStep){
        this.takeStep = takeStep;
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

    public List<Edge> getCurrentAugmentingPath() {
        return currentAugmentingPath;
    }
}
