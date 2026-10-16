package algo;

import graph.Edge;
import graph.Graph;
import graph.Node;

import javax.swing.*;
import java.util.LinkedList;

public class GoldbergTarjan extends BaseAlgorithm{

    boolean firstStep = true;
    public GoldbergTarjan(Graph graph) {
        super(graph);
    }


    /**
     * This describes how an Algorithm is run.
     * Replaces the original runAlgorithm() method since Goldberg-Tarjan (Preflow-Push / Push-Relabel) has different kinds of iterations.
     * @return an int, that is the maximum flow of the graph.
     * @throws InterruptedException if any thread has interrupted the current thread.
     */
    public int runAlgorithm() throws InterruptedException {
        while (true) {
            // the following handles taking steps based on GUI inputs.
            if (stepSizeInMillis == 0) {

            }else
                Thread.sleep(stepSizeInMillis);
            while(!takeStep && !autoRun){
                Thread.sleep(stepSizeInMillis);
            }
            takeStep = false;

            // Perform induction basis on the first iteration
            if(firstStep){
                goldbergTarjanInductionBasis();
                firstStep = false;
                // repaint GUI if using GUI
                if (!isTestRun) SwingUtilities.invokeLater(() -> graphPanel.repaint());
                continue;
            }

            // return the max flow if no active nodes are left
            if (activeNodes.isEmpty())
                return residualGraph.getMaxFlow();

            // reset iteration variables and perform an induction step
            resetIteration();
            goldbergTarjanInductionStep();

            // repaint GUI if using GUI
            if (!isTestRun) SwingUtilities.invokeLater(() -> graphPanel.repaint());
        }
    }


    /**
     * Implements the induction basis of the Goldberg-Tarjan (Preflow-Push / Push-Relabel) algorithm.
     */
    private void goldbergTarjanInductionBasis() {
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        // for all arcs out of source node: set flow to their max capacity
        // for all target nodes v of the edges: add pushed flow to their excess
        for(Edge e: sourceNode.getOutgoingEdges()){
            e.setFlow(e.getCapacity());
            e.getReverseEdge().setFlow(-e.getCapacity());

            Node v = e.getTargetNode();
            v.setExcess(v.getExcess() + e.getCapacity());
            // put v into S (the active node list)
            if (v != sourceNode && v != targetNode) {
                activeNodes.add(v);
                isNodeActive[v.getId()] = true;
            }
        }

        computeValidDistanceLabeling();
        sourceNode.setDepthLevelInCurrentIteration(residualGraph.getNodes().size()); // set d(s) = n = |V|
    }


    /**
     * Computes a valid distance labeling by assigning labels to every node containing their distance to the target node.
     * Sets the distance label of the source node to n = |V|.
     */
    private void computeValidDistanceLabeling() {
        // Create list for valid distance labeling and initialize it by adding the graph's target node.
        Node targetNode = residualGraph.getTargetNode();
        targetNode.setDepthLevelInCurrentIteration(0);
        LinkedList<Node> nodesForDistanceLabeling = new LinkedList<>();
        nodesForDistanceLabeling.add(targetNode);

        // Compute valid distance labels for all nodes in the graph via BFS
        int currentDepth = 0;
        while(!nodesForDistanceLabeling.isEmpty()){
            Node first = nodesForDistanceLabeling.removeFirst();
            // when looking at the first node of a new distance level, increase currentDepth
            if (first.getDepthLevelInCurrentIteration() == currentDepth) {
                currentDepth += 1;
            }
            // set distance labels of all adjacent nodes and add them to the list
            for(Edge e: first.getOutgoingEdges()) {
                targetNode = e.getTargetNode();
                if(targetNode.getDepthLevelInCurrentIteration() < 0){ // targetNode.getDepthLevelInCurrentIteration() < 0 || targetNode.getDepthLevelInCurrentIteration() > sourceNode.getDepthLevelInCurrentIteration()+1 --> dann neu beschreiben mit sourceNode + 1
                    targetNode.setDepthLevelInCurrentIteration(currentDepth);
                    nodesForDistanceLabeling.add(targetNode);
                }
            }
        }
    }


    /**
     * Implements an induction step of the Goldberg-Tarjan (Preflow-Push / Push-Relabel) algorithm.
     */
    private void goldbergTarjanInductionStep() {
        // get an active node from S
        Node v = activeNodes.getFirst();
        Edge currentEdge = v.getNextEdge();
        int dMin = Integer.MAX_VALUE;
        // iterate over edges till we find an admissible arc
        while (currentEdge != null && !(currentEdge.getTargetNode().getDepthLevelInCurrentIteration() == v.getDepthLevelInCurrentIteration() - 1 && currentEdge.getRemainingCapacity() > 0)) {
            currentEdge = v.getNextEdge();
        }

        // current arc is not void and admissible
        if(currentEdge != null){
            Node w = currentEdge.getTargetNode();
            // Makes sure the Target node of the edge has no excess and is neither source nor target node, then adds it to the active node list if it's not already in it.
            // 1. If w != s and w != t and e_f(w) = 0, insert w in S.
            if (w != residualGraph.getSourceNode() && w != residualGraph.getTargetNode() && w.getExcess() == 0) {
                if (!isNodeActive[w.getId()])
                    activeNodes.add(w);
                isNodeActive[w.getId()] = true;
            }
            // calc how much flow can be pushed over the looked at edge
            // 2. Increase the flow over (v,w) by the minimum of e_f(v) and the residual capacity of (v,w).
            int pushFlow = Math.min(currentEdge.getRemainingCapacity(), v.getExcess());
            if(!isTestRun) currentAugmentingPath.add(currentEdge);      // only used for visualisation in the GUI
            currentEdge.addFlow(pushFlow);
            currentEdge.getReverseEdge().addFlow(-pushFlow);
            if(currentEdge.getRemainingCapacity() > 0)
                v.stayOnCurrentEdge();

            // 3. Increase e_f(w) by that value and decrease e_f(v) by the same value.
            w.setExcess(w.getExcess() + pushFlow);
            v.setExcess(v.getExcess() - pushFlow);
            // 4. If e_f(v) = 0, extract v from S.
            if(v.getExcess() == 0){
                activeNodes.removeFirst();
                isNodeActive[v.getId()] = false;
            }
        // Otherwise:
        } else {
            // 1. Get the minimal label d_min of any arc in the residual network.
            v.resetCurrentEdge();
            currentEdge = v.getNextEdge();
            while (currentEdge != null) {
                if (currentEdge.getRemainingCapacity() > 0) {
                    dMin = Math.min(dMin, currentEdge.getTargetNode().getDepthLevelInCurrentIteration());
                    }
                currentEdge = v.getNextEdge();
            }
            if(!isTestRun) currentNodesToHighlight.add(v); // only for GUI
            // 2. [...]
            v.setDepthLevelInCurrentIteration(dMin+1);
            // 3. Reset the current arc of v to point to the beginning of the list of outgoing arcs of.
            v.resetCurrentEdge();
        }
    }


    /**
     * Resets the currentAugmentingPath and the currentNodesToHighlight lists.
     */
    private void resetIteration() {
        currentAugmentingPath =  new LinkedList<>();
        currentNodesToHighlight = new LinkedList<>();
    }

    @Override
    public LinkedList<Edge> getPathToSink() { return null; }
}
