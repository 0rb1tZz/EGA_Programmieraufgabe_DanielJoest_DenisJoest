package algo;

import graph.Edge;
import graph.Graph;
import graph.Node;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class GoldbergTarjan extends BaseAlgorithm{

    boolean firstStep = true;
    public GoldbergTarjan(Graph graph) {
        super(graph);
    }

    protected int runAlgorithm() throws InterruptedException {
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
                continue;
            }
            resetIteration();
            // keepRunning = applyDepthLabels();
            goldbergTarjanInductionStep();

            SwingUtilities.invokeLater(() -> graphPanel.repaint());
        }
        return residualGraph.getMaxFlow();
    }

    private void goldbergTarjanInductionBasis() {
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        for(Edge e: sourceNode.getOutgoingEdges()){
            e.setFlow(e.getCapacity());
            activeNodes.add(e.getTargetNode());
            isNodeActive[e.getTargetNode().getId()] = true;
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


    }

    private void resetIteration() {

    }

    @Override
    public LinkedList<Edge> getPathToSink() { return null; }
}
