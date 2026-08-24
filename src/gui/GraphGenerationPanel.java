package gui;

import graph.GraphBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GraphGenerationPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
    public static final int PANEL_WIDTH = 170;
    public static final int PANEL_HEIGHT = 30;

    public GraphGenerationPanel(GraphPanel graphPanel) {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel nodeCountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nodeCountPanel.setBackground(BACKGROUND_COLOR);
        JLabel nodeCountLabel = new JLabel("Node Count:");
        nodeCountLabel.setForeground(Color.WHITE);
        nodeCountPanel.add(nodeCountLabel);
        JTextField nodeCountField = new JTextField("67", 5);
        nodeCountPanel.add(nodeCountField);
        nodeCountPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        nodeCountPanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        nodeCountPanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.add(nodeCountPanel);

        JPanel maxCapacityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        maxCapacityPanel.setBackground(Color.BLUE);
        JLabel maxCapacityLabel = new JLabel("Max Capacity:");
        maxCapacityLabel.setForeground(Color.WHITE);
        maxCapacityPanel.add(maxCapacityLabel);
        JTextField maxCapacityField = new JTextField("5", 5);
        maxCapacityPanel.add(maxCapacityField);
        maxCapacityPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        maxCapacityPanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        maxCapacityPanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.add(maxCapacityPanel);

        JPanel seedPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        seedPanel.setBackground(BACKGROUND_COLOR);
        JLabel seedLabel = new JLabel("Seed (optional):");
        seedLabel.setForeground(Color.WHITE);
        seedPanel.add(seedLabel);
        JTextField seedField = new JTextField("42", 5);
        seedPanel.add(seedField);
        seedPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        seedPanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        seedPanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.add(seedPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton okButton = new JButton("Generate Graph");
        okButton.addActionListener(e -> {
            try {
                int nodeCount = Integer.parseInt(nodeCountField.getText());
                int capacity = Integer.parseInt(maxCapacityField.getText());
                int seed = seedField.getText().isEmpty() ? new Random().nextInt() : Integer.parseInt(seedField.getText());
                graphPanel.setGraph(new GraphBuilder(nodeCount, capacity, seed));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(okButton);
        buttonPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        buttonPanel.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        buttonPanel.setMinimumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        this.add(buttonPanel);
    }


}
