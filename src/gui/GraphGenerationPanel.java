package gui;

import graph.GraphBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GraphGenerationPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static final Color TEXT_COLOR = Color.BLACK;
    public static final int PANEL_WIDTH = 170;
    public static final int PANEL_HEIGHT = 30;


    // Components
    private final GraphGUI graphGUI;
    private final GraphPanel graphPanel;
    private JTextField nodeCountField;
    private JTextField maxCapacityField;
    private JTextField seedField;
    private final JButton generateButton = new JButton("Generate Graph");;
    private final JCheckBox edgeLabelToggle = new JCheckBox("Show Edge Labels", false);
    private final JCheckBox nodeLabelToggle = new JCheckBox("Show Node Labels", false);


    public GraphGenerationPanel(GraphGUI graphGUI, GraphPanel graphPanel) {
        this.graphGUI = graphGUI;
        this.graphPanel = graphPanel;

        setBackground(BACKGROUND_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(buildEdgeLabelTogglePanel());
        this.add(buildNodeLabelTogglePanel());
        this.add(buildNodeCountPanel());
        this.add(buildMaxCapacityPanel());
        this.add(buildSeedPanel());
        this.add(buildButtonPanel());
    }


    private JComponent buildNodeCountPanel() {
        JPanel nodeCountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nodeCountPanel.setBackground(BACKGROUND_COLOR);
        JLabel nodeCountLabel = new JLabel("Node Count:");
        nodeCountLabel.setForeground(TEXT_COLOR);
        nodeCountPanel.add(nodeCountLabel);
        nodeCountField = new JTextField("67", 5);
        nodeCountPanel.add(nodeCountField);
        nodeCountPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        nodeCountPanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        nodeCountPanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        return nodeCountPanel;
    }

    private JComponent buildMaxCapacityPanel() {
        JPanel maxCapacityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        maxCapacityPanel.setBackground(BACKGROUND_COLOR);
        JLabel maxCapacityLabel = new JLabel("Max Capacity:");
        maxCapacityLabel.setForeground(TEXT_COLOR);
        maxCapacityPanel.add(maxCapacityLabel);
        maxCapacityField = new JTextField("20", 5);
        maxCapacityPanel.add(maxCapacityField);
        maxCapacityPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        maxCapacityPanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        maxCapacityPanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        return maxCapacityPanel;
    }

    private JComponent buildSeedPanel() {
        JPanel seedPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        seedPanel.setBackground(BACKGROUND_COLOR);
        JLabel seedLabel = new JLabel("Seed (optional):");
        seedLabel.setForeground(TEXT_COLOR);
        seedPanel.add(seedLabel);
        seedField = new JTextField("42", 5);
        seedPanel.add(seedField);
        seedPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        seedPanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        seedPanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        return seedPanel;
    }

    private JComponent buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        generateButton.addActionListener(e -> {
            try {
                int nodeCount = Integer.parseInt(nodeCountField.getText());
                int capacity = Integer.parseInt(maxCapacityField.getText());
                int seed = seedField.getText().isEmpty() ? new Random().nextInt() : Integer.parseInt(seedField.getText());
                graphGUI.toggleMenuControls(false);
                graphPanel.setGraph(new GraphBuilder(nodeCount, capacity, seed));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(generateButton);
        buttonPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        buttonPanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        buttonPanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        return buttonPanel;
    }

    private JComponent buildEdgeLabelTogglePanel() {
        JPanel edgeLabelTogglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        edgeLabelTogglePanel.setBackground(BACKGROUND_COLOR);

        edgeLabelToggle.addActionListener(e -> {graphPanel.setShowEdgeLabels(edgeLabelToggle.isSelected());});
        edgeLabelTogglePanel.add(edgeLabelToggle);
        edgeLabelTogglePanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        edgeLabelTogglePanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        edgeLabelTogglePanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        return edgeLabelTogglePanel;
    }

    private JComponent buildNodeLabelTogglePanel() {
        JPanel nodeLabelTogglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nodeLabelTogglePanel.setBackground(BACKGROUND_COLOR);

        nodeLabelToggle.addActionListener(e -> {graphPanel.setNodeEdgeLabels(nodeLabelToggle.isSelected());});
        nodeLabelTogglePanel.add(nodeLabelToggle);
        nodeLabelTogglePanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        nodeLabelTogglePanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        nodeLabelTogglePanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        return nodeLabelTogglePanel;
    }


}
