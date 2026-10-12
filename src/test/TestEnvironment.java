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
        for (int i = 0; i < instances; i++){
            Graph graph = new GraphBuilder(nodes, maxCapacity, i).buildGraph(10000, 10000);
            int[] flowResults = new int[4];
            for (int j = 0; j < 4; j++){
                graph.resetGraphBeforeAlgoRun();
                BaseAlgorithm algorithm = switch (j) {
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
            }
            //test equal flow:
            boolean equalFlow = true;
//            for (int l: flowResults){ // kaputt
//                equalFlow = equalFlow && flowResults[l] == flowResults[0];
//            }
            for (int l = 1; l < flowResults.length; l++){
                equalFlow = equalFlow && flowResults[l] == flowResults[0];
            }
            testResultGUI.addTestResult("" + equalFlow);
        }
    }

    public void setTestResultGUI(TestResultGUI testResultGUI) {
        this.testResultGUI = testResultGUI;
    }
}
