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


    /**
     * This describes how an Algorithm is run.
     * Replaces the original runAlgorithm() method since Dinic requires the calculation of a blocking flow each iteration.
     * @return an int, that is the maximum flow of the graph.
     * @throws InterruptedException if any thread has interrupted the current thread.
     */
    @Override
    public int runAlgorithm() throws InterruptedException {
        boolean maxFlowReached = false;
        while (!maxFlowReached) {
            // the following handles taking steps based on GUI inputs.
            if (stepSizeInMillis == 0) {

            }else
                Thread.sleep(stepSizeInMillis);
            while(!takeStep && !autoRun){
                Thread.sleep(stepSizeInMillis);
            }
            takeStep = false;

            // prepares the current iteration, by resetting and applying depth labels, calculates teh blocking flow after.
            resetIteration();
            maxFlowReached = !applyDepthLabels();
            dinicBlockingFlow();

            // repaint GUI if using GUI
            if (!isTestRun) SwingUtilities.invokeLater(() -> graphPanel.repaint());
        }
        return residualGraph.getMaxFlow();
    }


    /**
     * Applies depth level labels for all the nodes as their distance to the source node.
     * @return true, if all Nodes up to the depth of the target node got labeled and false, if the target node could not be found
     */
    private boolean applyDepthLabels() {
        Queue<Node> toLookAt = new LinkedList<>();
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        toLookAt.add(sourceNode);
        sourceNode.setDepthLevelInCurrentIteration(0);
        // labels all nodes up to the depth of the target node
        while (!toLookAt.isEmpty()) {
            Node current = toLookAt.poll();
            if(current.getDepthLevelInCurrentIteration() == targetNode.getDepthLevelInCurrentIteration()){ // removes
                labelNodesOnShortestPaths(targetNode); // labels all nodes that a part of a minimal path
                cleanDepthLabels(); // remove depth levels of nodes, that are not part of the subgraph
                return true;
            }
            for (Edge e : current.getOutgoingEdges()){      // apply depth level labels to all nodes with depth <= target
                if(e.getTargetNode().getDepthLevelInCurrentIteration() < 0 && e.getRemainingCapacity() > 0){
                    Node target = e.getTargetNode();
                    toLookAt.add(target);
                    target.setDepthLevelInCurrentIteration(e.getSourceNode().getDepthLevelInCurrentIteration()+1);
                }
            }
        }

        return false;
    }


    /**
     * Defines which nodes are part of the current subgraph by setting their visitedInCurrentSearch field to true.
     * @param n The starting node for the recursion.
     */
    private void labelNodesOnShortestPaths(Node n) {
        if (n.isVisitedInCurrentSearch())
            return;
        n.setVisitedInCurrentSearch(true);
        for (Edge e : n.getOutgoingEdges()){
            if(e.getTargetNode().getDepthLevelInCurrentIteration()+1 == n.getDepthLevelInCurrentIteration()){
                labelNodesOnShortestPaths(e.getTargetNode());
                if(!isTestRun) currentAugmentingPath.add(e.getReverseEdge()); // Adds the edge solely for visualisation in the GUI
            }
        }
    }

    /**
     * Sets the depth level labels of all the nodes not belonging to the subgraph to -1 and makes sure that the nodes in the subgraph are set to not visited
     */
    private void cleanDepthLabels() {
        for (Node n : residualGraph.getNodes())
            if(!n.isVisitedInCurrentSearch())
                n.setDepthLevelInCurrentIteration(-1);
            else
                n.setVisitedInCurrentSearch(false);
    }


    /**
     * Implements the blocking flow by Dinic algorithm.
     */
    private void dinicBlockingFlow() {
        LinkedList<Edge> augmentingPath;
        int blockingFlow = 0;

        // calculate the blocking flow as sum of the bottlenecks over all augmenting paths (using DFS)
        while((augmentingPath = dfs()) != null){
            int bottleneck = bottleneckOfPath(augmentingPath);
            blockingFlow += bottleneck;

            // add the bottleneck value to every edge of the path
            for(Edge e: augmentingPath){
                e.addFlow(bottleneck);
                e.getReverseEdge().addFlow(-bottleneck);
            }
        }

        // show the blocking flow in the GUI
        if (!isTestRun) graphGUI.setBlockingFlowLabelText(blockingFlow);
    }


    /**
     * Runs a dfs on the graph.
     * @return returns a path from source to target calculated with a dfs
     */
    private LinkedList<Edge> dfs() {
        LinkedList<Edge> edgePath = new LinkedList<>();
        LinkedList<Node> nodePath = new LinkedList<>();
        Node sourceNode = residualGraph.getSourceNode();
        Node targetNode = residualGraph.getTargetNode();

        nodePath.add(sourceNode);
        sourceNode.setVisitedInCurrentSearch(true);

        // runs till a path with remaining capacity to target is found
        while (nodePath.peekLast() != targetNode) {
            // if the list is empty there is no path to target with remaining capacity
            if(nodePath.isEmpty())
                return null;
            // test edges of the last node in the path for any nodes to add to the path
            Node currentNode = nodePath.getLast();
            boolean hasOnlyVisitedChildren = true;
            for (Edge edge : currentNode.getOutgoingEdges()) {
                // makes sure not to use seen nodes, saturated Edges, to traverses the graph like a directed graph and not to use destroyed nodes
                if(!edge.getTargetNode().isVisitedInCurrentSearch() && edge.getRemainingCapacity() > 0 && edge.getSourceNode().getDepthLevelInCurrentIteration() < edge.getTargetNode().getDepthLevelInCurrentIteration() && !edge.getTargetNode().isDestroyedForCurrentIteration()){
                    nodePath.add(edge.getTargetNode());
                    edge.getTargetNode().setVisitedInCurrentSearch(true);
                    edgePath.add(edge);
                    hasOnlyVisitedChildren = false;
                    break;
                }
            }
            // if the dfs runs into a dead end remove the last node and edge, aka backtracks one step
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


    /**
     * Resets the 'visited' attribute of all the nodes in the graph.
     */
    private void resetVisited() {
        for (Node node : residualGraph.getNodes()) {
            node.setVisitedInCurrentSearch(false);
        }
    }


    /**
     * Resets all variables in preparation for the next iteration.
     */
    private void resetIteration() {
        for (Node node : residualGraph.getNodes()) {
            node.setDestroyedForCurrentIteration(false);
            node.setDepthLevelInCurrentIteration(-1);
        }
        if(!isTestRun) currentAugmentingPath = new LinkedList<Edge>();
    }


    @Override
    public LinkedList<Edge> getPathToSink() {
        return null;
    }
}
