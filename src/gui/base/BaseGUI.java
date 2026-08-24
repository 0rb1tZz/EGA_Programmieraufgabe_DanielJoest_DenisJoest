package gui.base;

import algo.Algorithm;
import graph.Graph1;
import gui.GraphGUI;
import gui.event.impl.AlgorithmChangedEvent;
import gui.event.impl.AlgorithmDrawEvent;
import gui.event.EventBus;
import gui.event.Subscribe;

import javax.swing.*;
import java.awt.*;

public class BaseGUI<T extends Comparable<T>> extends JFrame {

    private Algorithm<T, ?> algorithm;
    private Graph1<T> graph;

    private JButton startButton;
    private JButton pauseButton;
    private JSlider delaySlider;

    public BaseGUI(GraphGUI1 contentPanel, Graph1<T> graph) {
        super("Graph1Lib");
        EventBus.instance.register(this);

        this.graph = graph;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1500, 1000);
        this.setLocationRelativeTo(null);

        var controlPanel = createControlPanel();
        controlPanel.add(contentPanel.getCustomControls());
        add(controlPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            this.validate();
            this.repaint();
            this.setVisible(true);
        });
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            if (algorithm == null) return;
            algorithm.start();
            startButton.setEnabled(false);
        });

        delaySlider = new JSlider(0, 100, 0);
        delaySlider.createStandardLabels(10);
        delaySlider.addChangeListener(e -> {
                    if (algorithm == null) return;
                    algorithm.setDelay(delaySlider.getValue());
                }
        );

        var stepButton = new JButton("Step");
        stepButton.setEnabled(false);
        stepButton.addActionListener(e -> algorithm.step());

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> {
            if (algorithm == null) return;
            if (pauseButton.getText().equals("Pause")) {
                pauseButton.setText("Resume");
                algorithm.pause();
                stepButton.setEnabled(true);
            } else if (pauseButton.getText().equals("Resume")) {
                pauseButton.setText("Pause");
                algorithm.resume();
                stepButton.setEnabled(false);
            }
        });

        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(stepButton);
        controlPanel.add(new JLabel("Delay: "));
        controlPanel.add(delaySlider);

        controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        return controlPanel;
    }

    @Subscribe
    private void onAlgorithmDrawEvent(AlgorithmDrawEvent event) {
        SwingUtilities.invokeLater(this::repaint);
    }

    public void setAlgorithm(Algorithm<T, ?> algorithm) {
        this.algorithm = algorithm;
        startButton.setEnabled(true);
        algorithm.setDelay(delaySlider.getValue());
        if (pauseButton.getText().equals("Resume")) algorithm.pause();

        EventBus.instance.post(new AlgorithmChangedEvent(algorithm));
    }
}
