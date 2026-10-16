package algo;

import graph.Edge;
import graph.Graph;
import graph.Node;
import gui.GraphGUI;
import gui.GraphPanel;

import javax.swing.*;
import java.util.*;

public abstract class BaseAlgorithm {

//Schritt 1: basic setup
    //Schritt 2: Pfad holen
    //Schritt 3: kritische Kante finden
    //Schritt 4: flow entlang des Pfades anpassen
    //von Schritt 2 weitermachen


    // protected ResidualGraph residualGraph;
    protected Graph residualGraph;
    protected LinkedList<Edge> currentAugmentingPath = new LinkedList<Edge>();      // is also used for highlighting in the gui
    protected LinkedList<Node> currentNodesToHighlight = new LinkedList<Node>();    // is solely used for highlighting in the gui
    protected boolean noMoreAugmentingPath = false;

    protected List<Node> activeNodes = new LinkedList<Node>();
    protected boolean[] isNodeActive;

    protected boolean takeStep = false;
    protected boolean autoRun = false;
    protected long stepSizeInMillis = 500L;
    protected GraphGUI graphGUI;
    protected GraphPanel graphPanel;

    protected boolean isTestRun;

    public BaseAlgorithm(Graph graph) {
        residualGraph = graph;
        isNodeActive = new boolean[graph.getNodes().size()];
        // residualGraph = new ResidualGraph(graph);
    }


    /**
     * Method for starting an algorithm in a new thread. Used for running without interfering with the GUI.
     */
    public void startAlgorithmFromGUI() {
        new Thread(() -> {
            try {
                int maxFlow = runAlgorithm(); // run the algo
                graphGUI.onAlgoFinished(maxFlow); // show the max flow in the GUI
                System.out.println("Algorithm finished. Max Flow: " + maxFlow);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }


    /**
     * This describes how an Algorithm is run.
     * @return an int, which is the maximum flow of the graph.
     * @throws InterruptedException if any thread has interrupted the current thread.
     */
    public int runAlgorithm() throws InterruptedException {
        while(!noMoreAugmentingPath){
            // the following handles taking steps based on GUI inputs.
            if (stepSizeInMillis != 0) {
                Thread.sleep(stepSizeInMillis);
            }

            while(!takeStep && !autoRun){
                Thread.sleep(stepSizeInMillis);
            }
            takeStep = false;

            // calculates the current augmenting path
            currentAugmentingPath = getPathToSink();
            if(currentAugmentingPath == null) // if no path left the max flow is reached
                return residualGraph.getMaxFlow();

            // calculate bottleneck of current augmenting path, add its value to each edge
            int bottleneck = bottleneckOfPath(currentAugmentingPath);
            for(Edge e: currentAugmentingPath){
                e.addFlow(bottleneck);
                e.getReverseEdge().addFlow(-bottleneck);
            }

            // resets nodes for the next iteration
            for(Node n: residualGraph.getNodes())
                n.setVisitedInCurrentSearch(false);

            // repaint GUI if using GUI
            if (!isTestRun) SwingUtilities.invokeLater(() -> graphPanel.repaint());
        }
        return residualGraph.getMaxFlow();
    }


    /**
     * Method for finding a path from the source to the target node.
     * Each path fulfills the condition of only using Edges with a remaining capacity > 0.
     * @return a path from the source to the target node as a LinkedList of Edges, or null if there is no such path.
     */
    public abstract LinkedList<Edge> getPathToSink();


    /**
     * Calculates the bottleneck of a given path as the lowest remaining capacity of all edges.
     * @param path, a List of Edges, of which the bottleneck is to be determined.
     * @return the bottleneck value as an int.
     */
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

    public void setStepSizeInMillis(long stepSizeInMillis) {
        this.stepSizeInMillis = stepSizeInMillis;
    }

    public List<Edge> getCurrentAugmentingPath() {
        return currentAugmentingPath;
    }

    public List<Node> getCurrentNodesToHighlight() {
        return currentNodesToHighlight;
    }

    public void setGraphGUI(GraphGUI graphGUI) {
        this.graphGUI = graphGUI;
    }

    public void setGraphPanel(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
    }

    public void setIsTestRun(boolean isTestRun) {
        this.isTestRun = isTestRun;
    }
}
