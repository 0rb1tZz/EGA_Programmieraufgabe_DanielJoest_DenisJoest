package algo;

import graph.Edge;
import graph.Graph;
import graph.Node;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

public class Dinic extends BaseAlgorithm {

    public Dinic(Graph graph) {
        super(graph);
    }

    @Override
    protected int runAlgorithm() throws InterruptedException {
        boolean keepRunning = true;
        while (keepRunning) {
            Thread.sleep(stepSizeInMillis);
            while(!takeStep && !autoRun){
                Thread.sleep(stepSizeInMillis);
            }
            takeStep = false;
            resetIteration();
            keepRunning = applyDepthLabels();
            dinicBlockingFlow();

            SwingUtilities.invokeLater(() -> graphPanel.repaint());
        }
        return residualGraph.getMaxFlow();
    }

    /**
     *
     * @return augmenting path, or null if non is found
     */
    private LinkedList<Edge> dinicBlockingFlow(){
        LinkedList<Edge> augmentingPath;
        int blockingFlow = 0;
        while((augmentingPath = dfs()) != null){

            int bottleneck = bottleneckOfPath(augmentingPath);
            blockingFlow += bottleneck;
            for(Edge e: augmentingPath){
                e.addFlow(bottleneck);
                e.getReverseEdge().addFlow(-bottleneck);
            }
        }
        graphGUI.setBlockingFlowLabelText(blockingFlow);
        return null;
    }

    private LinkedList<Edge> dfs(){
        LinkedList<Edge> edgePath = new LinkedList<>();
        LinkedList<Node> nodePath = new LinkedList<>();
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        nodePath.add(sourceNode);
        sourceNode.setVisitedInCurrentSearch(true);
        while (nodePath.peekLast() != targetNode) {
            if(nodePath.isEmpty())
                return null;
            Node currentNode = nodePath.getLast();
            boolean hasOnlyVisitedChildren = true;
            for (Edge edge : currentNode.getOutgoingEdges()) {
                if(!edge.getTargetNode().isVisitedInCurrentSearch() && edge.getRemainingCapacity() > 0 && edge.getSourceNode().getDepthLevelInCurrentIteration() < edge.getTargetNode().getDepthLevelInCurrentIteration() && !targetNode.isDestroyedForCurrentIteration()){
                    nodePath.add(edge.getTargetNode());
                    edge.getTargetNode().setVisitedInCurrentSearch(true);
                    edgePath.add(edge);
                    hasOnlyVisitedChildren = false;
                    break;
                }
            }
            if (hasOnlyVisitedChildren) {
                nodePath.getLast().setDestroyedForCurrentIteration(true);
                nodePath.removeLast();
                if(!edgePath.isEmpty())
                    edgePath.removeLast();
            }
        }

        resetVisited();
        return edgePath;
    }

    private void resetVisited(){
        for (Node node : residualGraph.getNodes()) {
            node.setVisitedInCurrentSearch(false);
        }
    }

    private void resetIteration(){
        for (Node node : residualGraph.getNodes()) {
            node.setDestroyedForCurrentIteration(false);
            node.setDepthLevelInCurrentIteration(-1);
        }
        currentAugmentingPath = new LinkedList<Edge>();
    }

    /**
     *
     * @return true, if all Nodes up to the depth of the target node got labled and false, if the target node could not be found
     */
    private boolean applyDepthLabels(){
        Queue<Node> toLookAt = new LinkedList<>();
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        toLookAt.add(sourceNode);
        sourceNode.setDepthLevelInCurrentIteration(0);
        while (!toLookAt.isEmpty()) {
            Node current = toLookAt.poll();
            if(current.getDepthLevelInCurrentIteration() == targetNode.getDepthLevelInCurrentIteration()){
                labelNodesOnShortestPaths(targetNode);
                cleanDepthLabels();
                return true;
            }
            for (Edge e : current.getOutgoingEdges()){
                if(e.getTargetNode().getDepthLevelInCurrentIteration() < 0 && e.getRemainingCapacity() > 0){
                    Node target = e.getTargetNode();
                    toLookAt.add(target);
                    target.setDepthLevelInCurrentIteration(e.getSourceNode().getDepthLevelInCurrentIteration()+1);
                }
            }
        }

        return false;
    }

    private void labelNodesOnShortestPaths(Node n){
        n.setVisitedInCurrentSearch(true);
        for (Edge e : n.getOutgoingEdges()){
            if(e.getTargetNode().getDepthLevelInCurrentIteration()+1 == n.getDepthLevelInCurrentIteration()){
                labelNodesOnShortestPaths(e.getTargetNode());
                currentAugmentingPath.add(e.getReverseEdge());
            }
        }
    }

    private void cleanDepthLabels(){
        for (Node n : residualGraph.getNodes())
            if(!n.isVisitedInCurrentSearch())
                n.setDepthLevelInCurrentIteration(-1);
            else
                n.setVisitedInCurrentSearch(false);
    }

    @Override
    public LinkedList<Edge> getPathToSink() {
        return null;
    }
}
