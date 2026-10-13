package test;

import algo.*;
import graph.Edge;
import graph.Graph;
import graph.GraphBuilder;
import graph.Node;
import gui.TestResultGUI;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TestEnvironment {

    int instances;
    int nodes;
    int maxCapacity;

    private TestResultGUI testResultGUI;

    public TestEnvironment(int instances, int nodes, int maxCapacity) {
        this.instances = instances;
        this.nodes = nodes;
        this.maxCapacity = maxCapacity;
    }

    public void runTestEnvironment() {
        testResultGUI.addTitle("Testing the triple (" + instances + ", " + nodes + ", " + maxCapacity + ")");
        for (int i = 0; i < instances; i++){
            testResultGUI.addHeadline("Testing instance " + i);
            Graph graph = new GraphBuilder(nodes, maxCapacity, i).buildGraph(10000, 10000);
            int[] flowResults = new int[8];
            long[] timeResults = new long[4];
            boolean[] flowPresResults = new boolean[4];
            boolean[] capCorrectnessResults = new boolean[4];
            boolean[] satCutResults = new boolean[4];
            int[] minCutResults = new int[4];
            for (int j = 0; j < 8; j++){
                if(j == 4){
                    graph.adaptForSecondTestRun();
                }
//                if(j == 3 || j == 7){
//                    continue;
//                }
                graph.resetGraphBeforeAlgoRun();
                BaseAlgorithm algorithm = switch (j%4) {
                    case 0 -> new EdmondsKarp(graph);
                    case 1 -> new FordFulkerson(graph);
                    case 2 -> new Dinic(graph);
                    default -> new GoldbergTarjan(graph);
                };
                algorithm.setStepSizeInMillis(0);
                algorithm.setIsTestRun(true);
                algorithm.setAutoRun(true);

                long runtime = 0;
                try {
                    long timePre = System.currentTimeMillis();
                    flowResults[j] = algorithm.runAlgorithm();
                    long timePost = System.currentTimeMillis();
                    runtime = timePost - timePre;
                } catch (Exception e) {
                    System.err.println("Error while running test environment");
                }

                //test Flusserhaltung:
                boolean flowPreservation = true;
                for (int k = 1; k < nodes - 1 && flowPreservation; k++){
                    List<Node> graphNodes = graph.getNodes();
                    List<Edge> nodeEdges = graphNodes.get(k).getOutgoingEdges();
                    int outgoing = 0;
                    for (Edge edge : nodeEdges){
                        int flow = edge.getFlow();
                        if (flow > 0)
                            outgoing += flow;
                    }
                    int incoming = 0;
                    for (Edge edge : nodeEdges){
                        int flow = edge.getReverseEdge().getFlow();
                        if (flow > 0)
                            incoming += flow;
                    }
                    flowPreservation = incoming == outgoing;
                }

                //test Kapazitäten:
                boolean capacityCondition = true;
                for (Edge edge : graph.getEdges()){
                    if(edge.getFlow() > maxCapacity){
                        capacityCondition = false;
                        break;
                    }
                }

                //test saturierter Schnitt:
                boolean saturatedCutCondition = true;
                LinkedList<Node> nodesConnectedToS = new LinkedList<>();
                nodesConnectedToS.add(graph.getSourceNode());
                for(Node n: graph.getNodes()){
                    n.setVisitedInCurrentSearch(false);
                }
                graph.getSourceNode().setVisitedInCurrentSearch(true);
                for (int k = 0; k < nodesConnectedToS.size(); k++){
                    Node current = nodesConnectedToS.get(k);
                    for(Edge e: current.getOutgoingEdges()){
                        if(e.getTargetNode().isVisitedInCurrentSearch() || e.getRemainingCapacity() == 0)
                            continue;
                        nodesConnectedToS.add(e.getTargetNode());
                        e.getTargetNode().setVisitedInCurrentSearch(true);
                    }
                }
                if(nodesConnectedToS.contains(graph.getTargetNode()))
                    saturatedCutCondition = false;
                int minCut = 0;
                for (Node n: nodesConnectedToS){
                    for(Edge e: n.getOutgoingEdges()){
                        if (e.getTargetNode().isVisitedInCurrentSearch())
                            continue;
                        minCut += e.getFlow();
                    }
                }

                if (j < 4){
                    timeResults[j] = runtime;
                    flowPresResults[j] = flowPreservation;
                    capCorrectnessResults[j] = capacityCondition;
                    satCutResults[j] = saturatedCutCondition;
                    minCutResults[j] = minCut;
                }
            }
            //test equal flow:
            boolean equalFlow = true;

            for (int l = 1; l < 4; l++){
                equalFlow = equalFlow && flowResults[l] == flowResults[0];
            }
            boolean correctlyChangedFlow = true;
            for (int l = 0; l < 4; l++){
                correctlyChangedFlow = correctlyChangedFlow && flowResults[l] + 1 == flowResults[l+4];
            }
            LinkedList<String> testResults = new LinkedList<>();
            String flowPres = "Flow preservation is " + Arrays.toString(flowPresResults);
            testResults.add(flowPres);
            String capCorrectness = "Capacity correctness is " + Arrays.toString(capCorrectnessResults);
            testResults.add(capCorrectness);
            String satCut = "Sat cut results are " + Arrays.toString(satCutResults);
            testResults.add(satCut);
            String minCut = "Minimum cut results are " + Arrays.toString(minCutResults);
            testResults.add(minCut);
            String runtimes = "Runtimes (ms): " + Arrays.toString(timeResults);
            testResults.add(runtimes);
            String flowEquality = "Flow equality is " + equalFlow + " (" + flowResults[0] + ")";
            testResults.add(flowEquality);
            String changedFlow = "Changed flow test is " + correctlyChangedFlow;
            testResults.add(changedFlow);

            testResultGUI.addTestResult(testResults);
        }
    }

    public void setTestResultGUI(TestResultGUI testResultGUI) {
        this.testResultGUI = testResultGUI;
    }
}
