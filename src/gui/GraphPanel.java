package gui;

import algo.EdmondsKarp;
import algo.FordFulkerson;
import graph.Edge;
import graph.Graph;
import graph.GraphBuilder;
import graph.Node;
import gui.dialog.GraphGeneratorDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class GraphPanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
    private Graph graph;
    private static final double BASE_NODE_RADIUS = 20;
    private double nodeRadius = 20;
    private static final int BASE_FONT_SIZE = 12;
    private int fontSize = 12;

    //private final BufferedImage image;

    private double zoomFactor = 1;
    private double prevZoomFactor = 1;
    private boolean zoomer;
    private boolean dragger;
    private boolean released;
    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;
    private Point startPoint;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setBackground(Color.GRAY);
        initListeners();
        // setupMouse();
    }

    public GraphPanel() {
        setBackground(Color.GRAY);
        initListeners();
        // setupMouse();
    }

    private void initListeners() {
        addMouseWheelListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) return;
        Graphics2D g2d = (Graphics2D) g;

        if (zoomer) {
            AffineTransform at = new AffineTransform();

            double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();

            double zoomDiv = zoomFactor / prevZoomFactor;

            xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
            yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

            at.translate(xOffset, yOffset);
            at.scale(zoomFactor, zoomFactor);
            prevZoomFactor = zoomFactor;
            g2d.transform(at);
            zoomer = false;
        }

        if (dragger) {
            AffineTransform at = new AffineTransform();
            at.translate(xOffset + xDiff, yOffset + yDiff);
            at.scale(zoomFactor, zoomFactor);
            g2d.transform(at);

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
                dragger = false;
            }

        }

        // Enable antialiasing for smoother lines and text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw Edges first so they sit behind the nodes
        g2d.setStroke(new BasicStroke(2));
        for (Edge edge : graph.getEdges()) {
            g2d.setColor(Color.WHITE);
            g2d.drawLine(edge.getSourceNode().getX(), edge.getSourceNode().getY(), edge.getTargetNode().getX(), edge.getTargetNode().getY());
        }

        for (Edge edge : graph.getEdges()) {
            // Draw edge capacities
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (int) edgeLabelPosition(edge)[0]; // - (fm.stringWidth(String.valueOf(edge.getCapacity())) / 2);
            int textY = (int) (edgeLabelPosition(edge)[1]); // + (fm.getAscent() / 2f));
            g2d.drawString(String.valueOf(edge.getFlow() + " / " + edge.getCapacity()), textX, textY);
        }

        // Draw Nodes
        for (Node node : graph.getNodes()) {
            // Draw node circle
            g2d.setColor(new Color(70, 130, 180)); // Steel Blue
            g2d.fillOval(node.getX() - (int) nodeRadius, node.getY() - (int) nodeRadius, 2 * (int) nodeRadius, 2 * (int) nodeRadius);

            // Draw node outline
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(node.getX() - (int) nodeRadius, node.getY() - (int) nodeRadius, 2 * (int) nodeRadius, 2 * (int) nodeRadius);

            // Draw node id centered
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = node.getX() - (fm.stringWidth(String.valueOf(node.getId())) / 2);
            int textY = node.getY() + (fm.getAscent() / 2) - 2;
            g2d.drawString(String.valueOf(node.getId()), textX, textY);
        }
    }

    private double[] edgeLabelPosition(Edge edge){
        int x = edge.getSourceNode().getX() + (edge.getTargetNode().getX() - edge.getSourceNode().getX())/4;
        int y = edge.getSourceNode().getY() + (edge.getTargetNode().getY() - edge.getSourceNode().getY())/4;
        float[] reverseSlope = new float[2];
        reverseSlope[0] = edge.getTargetNode().getY() - edge.getSourceNode().getY();
        reverseSlope[1] = edge.getTargetNode().getX() - edge.getSourceNode().getX();
        double lengthRevSlope = Math.sqrt(reverseSlope[0]*reverseSlope[0] + reverseSlope[1]*reverseSlope[1]);
        reverseSlope[0] /= lengthRevSlope/10;
        reverseSlope[1] /= lengthRevSlope/10;

//
//        double reversSlope;
//        if (edge.getSourceNode().getY() != edge.getTargetNode().getY()) {
//            reversSlope = (edge.getTargetNode().getX() - edge.getSourceNode().getX())/(double)(edge.getTargetNode().getY() - edge.getSourceNode().getY()) * -1;
//        } else {
//            reversSlope = 100;
//        }
//        double reverseSlopeNormalized = reversSlope / Math.sqrt(1 + Math.pow(reversSlope, 2));
//        double sign = Math.signum(reverseSlopeNormalized);
        // return new double[]{x + reverseSlope[0], y + reverseSlope[1]};
        return new double[]{x, y};
    }

    private void setupMouse() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showContextMenu(e);
                }
            }
        };

        addMouseListener(ma);
    }

    private void showContextMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();
        try {
            menu.setLightWeightPopupEnabled(false);
        } catch (Throwable ignored) {

        }

        var genGraph = new JMenuItem("Generate Graph...");
        genGraph.addActionListener(e1 -> {
            var dialog = new GraphGeneratorDialog(this);
            var d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generate Graph", true);
            d.setSize(360, 200);
            d.setLocationRelativeTo(this);
            d.add(dialog);
            d.setVisible(true);
        });
        menu.add(genGraph);

        menu.show(this, e.getX(), e.getY());
    }

    public void setGraph(GraphBuilder graphBuilder) {
        this.graph = graphBuilder.buildGraph(this.getWidth(), this.getHeight());
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        EdmondsKarp falk = new EdmondsKarp(this.graph);
        System.out.println(falk.runAlgorithm());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        released = false;
        startPoint = MouseInfo.getPointerInfo().getLocation();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        released = true;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point curPoint = e.getLocationOnScreen();
        xDiff = curPoint.x - startPoint.x;
        yDiff = curPoint.y - startPoint.y;

        dragger = true;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        zoomer = true;

        //Zoom in
        if (e.getWheelRotation() < 0) {
            if (zoomFactor >= 5) return;
            zoomFactor *= 1.1;
            nodeRadius = (BASE_NODE_RADIUS / zoomFactor);
            fontSize = (int) (BASE_FONT_SIZE / zoomFactor);
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            if (zoomFactor <= 0.2) return;
            zoomFactor /= 1.1;
            nodeRadius = (BASE_NODE_RADIUS / zoomFactor);
            fontSize = (int) (BASE_FONT_SIZE / zoomFactor);
            repaint();
        }
    }
}