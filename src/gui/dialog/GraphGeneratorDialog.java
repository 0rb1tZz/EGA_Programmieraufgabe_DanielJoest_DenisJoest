package gui.dialog;

import graph.GraphBuilder;
import gui.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GraphGeneratorDialog extends JPanel {
    public GraphGeneratorDialog(GraphPanel graphPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Node count:"));
        JTextField nodeCountF = new JTextField("10", 8);
        row1.add(nodeCountF);
        add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Max Capacity:"));
        JTextField maxCapacity = new JTextField("5", 8);
        row2.add(maxCapacity);
        add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Seed (optional):"));
        JTextField seedF = new JTextField("42", 8);
        row3.add(seedF);
        add(row3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton ok = new JButton("Generate");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(e -> {
            try {
                int nodeCount = Integer.parseInt(nodeCountF.getText());
                int capacity = Integer.parseInt(maxCapacity.getText());
                int seed = seedF.getText().isEmpty() ? new Random().nextInt() : Integer.parseInt(seedF.getText());
                graphPanel.setGraph(new GraphBuilder(nodeCount, capacity, seed));
                SwingUtilities.getWindowAncestor(this).dispose(); // dispose raus wenn nicht mehr in PopUp
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttons.add(ok);
        buttons.add(cancel);
        add(buttons);
    }


}
