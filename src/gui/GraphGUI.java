package gui;

import algo.*;
import graph.Edge;
import graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.List;

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


    private final JButton confirmButton = new JButton("Confirm");
    // private final JButton stopButton = new JButton("Stop");
    private final JButton stepButton = new JButton("Step");
    private final JToggleButton autoButton = new JToggleButton("Auto Run");
    private final JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);

    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel flowLabel = new JLabel(" ");

    private Graph currentGraph;
    private BaseAlgorithm algorithm;



    public GraphGUI() {
        super("Graph");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1920, 1080);
        this.setLayout(new BorderLayout());

        this.add(new GraphGenerationPanel(this, graphPanel), BorderLayout.WEST);
        this.add(buildMenuBar(), BorderLayout.NORTH);
        this.add(buildStatusBar(), BorderLayout.SOUTH);

        graphPanel.setGraphGUI(this);
        var scrollPane = new JScrollPane(graphPanel);
        this.add(scrollPane, BorderLayout.CENTER);

        addListeners();
        toggleMenuControls(false);
        confirmButton.setEnabled(false);

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
        menuBar.add(new JLabel("Choose Algo Type:"));
        menuBar.add(algoSelectionBox);
        menuBar.add(confirmButton);
        menuBar.add(new JSeparator(SwingConstants.VERTICAL));
        menuBar.add(stepButton);
        menuBar.add(new JSeparator(SwingConstants.VERTICAL));
        menuBar.add(autoButton);
        menuBar.add(new JLabel("Speed:"));
        speedSlider.setPreferredSize(new Dimension(150, 20));
        menuBar.add(speedSlider);

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
        algorithm.setGraphGUI(this);
        algorithm.setGraphPanel(graphPanel);
        algorithm.startAlgorithm();
//        try {
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        statusLabel.setText("Algorithm running.");
        toggleMenuControls(true);
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

    public void toggleMenuControls(boolean running) {
        algoSelectionBox.setEnabled(!running);
        confirmButton.setEnabled(!running);

        stepButton.setEnabled(running);
        autoButton.setSelected(false);
        autoButton.setEnabled(running);
        speedSlider.setEnabled(running);
        // stopButton.setEnabled(running);
    }

    private void addListeners() {
        confirmButton.addActionListener(e -> {startAlgorithm();});
        // stopButton.addActionListener(e -> {stopAlgorithm();});
        stepButton.addActionListener(e -> {algorithm.setTakeStep(true); algorithm.setAutoRun(false);});
        autoButton.addActionListener(e -> {algorithm.setAutoRun(autoButton.isSelected());});
        speedSlider.addChangeListener(e -> {algorithm.setStepSizeInMillis(speedSlider.getValue() * 10L);});
    }

    public void onAlgoFinished(int maxFlow) {
        SwingUtilities.invokeLater(() -> {
            toggleMenuControls(false);
            statusLabel.setText("Algorithm finished.");
            flowLabel.setText("Max Flow: " + maxFlow);
        }); // kp warums nicht geht
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
