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

    public int runAlgorithm() throws InterruptedException {
        boolean keepRunning = true;
        while (keepRunning) {
            Thread.sleep(stepSizeInMillis);
            while(!takeStep && !autoRun){
                Thread.sleep(stepSizeInMillis);
            }
            takeStep = false;
            if(firstStep){
                goldbergTarjanInductionBasis();
                firstStep = false;
                if (!isTestRun) SwingUtilities.invokeLater(() -> graphPanel.repaint());
                continue;
            }
            if (activeNodes.isEmpty())
                return residualGraph.getMaxFlow();
            resetIteration();
            // keepRunning = applyDepthLabels();
            goldbergTarjanInductionStep();

            if (!isTestRun) SwingUtilities.invokeLater(() -> graphPanel.repaint());
        }
        return residualGraph.getMaxFlow();
    }

    private void goldbergTarjanInductionBasis() {
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        for(Edge e: sourceNode.getOutgoingEdges()){
            e.setFlow(e.getCapacity());
            e.getReverseEdge().setFlow(-e.getCapacity());
            e.getTargetNode().setExcess(e.getTargetNode().getExcess() + e.getCapacity());
            if (e.getTargetNode() != sourceNode && e.getTargetNode() != targetNode) {
                activeNodes.add(e.getTargetNode());
                isNodeActive[e.getTargetNode().getId()] = true;
            }
        }

        targetNode.setDepthLevelInCurrentIteration(0);
        LinkedList<Node> nodesForDistanceLabeling = new LinkedList<>();
        nodesForDistanceLabeling.add(targetNode);
        computeValidDistanceLabeling(nodesForDistanceLabeling);
        sourceNode.setDepthLevelInCurrentIteration(residualGraph.getNodes().size());
    }

    private void computeValidDistanceLabeling(LinkedList<Node> nodes) {
        int currentDepth = 0;
        while(!nodes.isEmpty()){
            Node first = nodes.removeFirst();
            if (first.getDepthLevelInCurrentIteration() == currentDepth) {
                currentDepth += 1;
            }
            for(Edge e: first.getOutgoingEdges()) {
                Node targetNode = e.getTargetNode();
                if(targetNode.getDepthLevelInCurrentIteration() < 0){ // targetNode.getDepthLevelInCurrentIteration() < 0 || targetNode.getDepthLevelInCurrentIteration() > sourceNode.getDepthLevelInCurrentIteration()+1 --> dann neu beschreiben mit sourceNode + 1
                    targetNode.setDepthLevelInCurrentIteration(currentDepth);
                    nodes.add(targetNode);
                }
            }
        }
    }

    private void goldbergTarjanInductionStep() {
        Node v = activeNodes.getFirst();
        Edge currentEdge = v.getNextEdge();
        int dMin = Integer.MAX_VALUE;
        while (currentEdge != null && !(currentEdge.getTargetNode().getDepthLevelInCurrentIteration() == v.getDepthLevelInCurrentIteration() - 1 && currentEdge.getRemainingCapacity() > 0)) {
            currentEdge = v.getNextEdge();
        }

        if(currentEdge != null){
            Node w = currentEdge.getTargetNode();
            if (w != residualGraph.getSourceNode() && w != residualGraph.getTargetNode() && w.getExcess() == 0) {
                if (!isNodeActive[w.getId()])
                    activeNodes.add(w);
                isNodeActive[w.getId()] = true;
            }
            int pushFlow = Math.min(currentEdge.getRemainingCapacity(), v.getExcess());
            currentAugmentingPath.add(currentEdge);
            currentEdge.addFlow(pushFlow);
            currentEdge.getReverseEdge().addFlow(-pushFlow);
            if(currentEdge.getRemainingCapacity() > 0)
                v.stayOnCurrentEdge();

            w.setExcess(w.getExcess() + pushFlow);
            v.setExcess(v.getExcess() - pushFlow);
            if(v.getExcess() == 0){
                activeNodes.removeFirst();
                isNodeActive[v.getId()] = false;
            }
        } else {
            v.resetCurrentEdge();
            currentEdge = v.getNextEdge();
            while (currentEdge != null) {
                if (currentEdge.getRemainingCapacity() > 0) {
                    dMin = Math.min(dMin, currentEdge.getTargetNode().getDepthLevelInCurrentIteration());
                    }
                currentEdge = v.getNextEdge();
            }
            v.setDepthLevelInCurrentIteration(dMin+1);
            v.resetCurrentEdge();
        }


    }

    private void resetIteration() {
        currentAugmentingPath =  new LinkedList<>();
    }

    @Override
    public LinkedList<Edge> getPathToSink() { return null; }
}
