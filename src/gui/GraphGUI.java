package gui;

import algo.*;
import graph.Edge;
import graph.Graph;
import graph.GraphBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class GraphGUI extends JFrame {

    private enum AlgoType {
        FORD_FULKERSON("Ford-Fulkerson"),
        EDMONDS_KARP("Edmonds-Karp"),
        DINIC("Dinic"),
        GOLDBERG_TARJAN("Goldberg-Tarjan");

        private final String algoName;
        AlgoType(String algoName) { this.algoName = algoName; }
        @Override
        public String toString() { return algoName; }
    }


    private final GraphPanel graphPanel = new GraphPanel();
    private final JComboBox<AlgoType> algoSelectionBox = new JComboBox<>(AlgoType.values());


    private final JButton newGraphButton = new JButton("New Graph");
    private final JButton startButton = new JButton("Start");
    private final JButton stopButton = new JButton("Stop");
    private final JButton stepButton = new JButton("Step");
    private final JToggleButton autoButton = new JToggleButton("Auto Run");
    private final JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
    private final JCheckBox edgeLabelToggle = new JCheckBox("Show Edge Labels", false);
    private final JCheckBox nodeLabelToggle = new JCheckBox("Show Node Labels", false);

    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel flowLabel = new JLabel(" ");

    private Graph currentGraph;
    private BaseAlgorithm algorithm;



    public GraphGUI() {
        super("Graph");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1920, 1080);
        this.setLayout(new BorderLayout());

        add(buildMenuBar(), BorderLayout.NORTH);
        add(buildStatusBar(), BorderLayout.SOUTH);

        graphPanel.setGraphGUI(this);
        var scrollPane = new JScrollPane(graphPanel);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(new GraphGenerationPanel(graphPanel), BorderLayout.EAST);

        addListeners();

        SwingUtilities.invokeLater(() -> {this.validate(); this.repaint(); this.setVisible(true);});
    }

    /**
     * Builds and returns a menu bar for choosing settings.
     *
     * @return The menu bar as a JComponent.
     */
    private JComponent buildMenuBar() {
        JPanel menuBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        menuBar.setBackground(Color.LIGHT_GRAY);
        menuBar.add(newGraphButton);
        menuBar.add(new JLabel("Choose Algo Type:"));
        menuBar.add(algoSelectionBox);
        menuBar.add(startButton);
        menuBar.add(stopButton);
        menuBar.add(new JSeparator(SwingConstants.VERTICAL));
        menuBar.add(stepButton);
        menuBar.add(autoButton);
        menuBar.add(new JLabel("Speed:"));
        speedSlider.setPreferredSize(new Dimension(150, 20));
        menuBar.add(speedSlider);
        menuBar.add(edgeLabelToggle);
        menuBar.add(nodeLabelToggle);
        return menuBar;
    }

    /**
     * Builds and returns a status bar.
     *
     * @return The status bar as a JComponent.
     */
    private JComponent buildStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Color.LIGHT_GRAY);
        statusBar.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 13f));
        flowLabel.setFont(flowLabel.getFont().deriveFont(Font.BOLD, 13f));
        statusBar.add(statusLabel, BorderLayout.CENTER);
        statusBar.add(flowLabel, BorderLayout.EAST);
        return statusBar;
    }


    private void generateNewGraph() {
        stopAlgorithm();
        Graph graph = new Graph(); // properties

        if (graph == null) {
            JOptionPane.showMessageDialog(this, "Please select a Graph", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

//        try {
//            int nodeCount = Integer.parseInt(nodeCountField.getText());
//            int capacity = Integer.parseInt(maxCapacityField.getText());
//            int seed = seedField.getText().isEmpty() ? new Random().nextInt() : Integer.parseInt(seedField.getText());
//            graphPanel.setGraph(new GraphBuilder(nodeCount, capacity, seed));
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
//        }

        currentGraph = graph;
        //graphPanel.setGraph(graph);
        statusLabel.setText("Graph Created with " + graph.getNodes().size() + " Nodes.");
        flowLabel.setText(" ");
        toggleMenuControls(false);
    }

    private void startAlgorithm() {
        algorithm = createAlgorithm((AlgoType) algoSelectionBox.getSelectedItem());
        algorithm.setGraphPanel(graphPanel);
        algorithm.startAlgorithm();
//        try {
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    private void stopAlgorithm() {

    }

    private BaseAlgorithm createAlgorithm(AlgoType algoType) {
        return switch (algoType) {
            case FORD_FULKERSON -> new FordFulkerson(currentGraph);
            case EDMONDS_KARP -> new EdmondsKarp(currentGraph);
            case DINIC -> new Dinic(currentGraph);
            case GOLDBERG_TARJAN -> new GoldbergTarjan(currentGraph);
        };
    }

    private void toggleMenuControls(boolean running) {
        algoSelectionBox.setEnabled(!running);

        stepButton.setEnabled(running);
        autoButton.setEnabled(running);
        speedSlider.setEnabled(running);
        stopButton.setEnabled(running);
    }

    private void addListeners() {
        newGraphButton.addActionListener(e -> {generateNewGraph();});
        startButton.addActionListener(e -> {startAlgorithm();});
        stopButton.addActionListener(e -> {stopAlgorithm();});
        stepButton.addActionListener(e -> {algorithm.setTakeStep(stepButton.isSelected()); algorithm.setAutoRun(false);});
        autoButton.addActionListener(e -> {algorithm.setAutoRun(autoButton.isSelected());});

        edgeLabelToggle.addActionListener(e -> {graphPanel.setShowEdgeLabels(edgeLabelToggle.isSelected());});
        nodeLabelToggle.addActionListener(e -> {graphPanel.setNodeEdgeLabels(nodeLabelToggle.isSelected());});
    }

    public void setGraph(Graph graph){
        this.currentGraph = graph;
    }

    public List<Edge> getCurrentAugmentingPath() {
        if (algorithm == null) {
            return null;
        }
        return algorithm.getCurrentAugmentingPath();
    }

}
