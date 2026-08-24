package gui;

import graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class GraphGUI extends JFrame {

    public GraphGUI() {
        super("Graph");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1920, 1080);
        this.setLayout(new BorderLayout());
        var graphPanel = new GraphPanel();
        var scrollPane = new JScrollPane(graphPanel);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(new GraphGenerationPanel(graphPanel), BorderLayout.EAST);

        SwingUtilities.invokeLater(() -> {this.validate(); this.repaint(); this.setVisible(true);});
    }



}
