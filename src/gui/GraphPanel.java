package gui;

import graph.Edge;
import graph.Graph;
import graph.GraphBuilder;
import graph.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class GraphPanel extends JPanel implements MouseWheelListener, MouseListener, MouseMotionListener {
    private Graph graph;
    private static final double BASE_NODE_RADIUS = 20;
    private double nodeRadius = 20;
    private static final double BASE_EDGE_THICKNESS = 2;
    private double edgeThickness = BASE_EDGE_THICKNESS;
    private static final int BASE_FONT_SIZE = 12;
    private int fontSize = 12;
    private boolean showEdgeLabels = false;
    private boolean showNodeLabels = false;

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

    private GraphGUI graphGUI;

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
        drawEdges(g2d);

        // Re-Draw edges in orange that are part of the current augmenting path
        if (graphGUI.getCurrentAugmentingPath() != null){
            g2d.setColor(Color.ORANGE);

            for (Edge edge : graphGUI.getCurrentAugmentingPath()) {
                g2d.drawLine(edge.getSourceNode().getX(), edge.getSourceNode().getY(), edge.getTargetNode().getX(), edge.getTargetNode().getY());
            }
        }

        // Draw edge capacities
        if (showEdgeLabels) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            for (Edge edge : graph.getEdges()) {
                String edgeLabel = edge.getFlow() + " / " + edge.getCapacity();
                int textX = (int) edgeLabelPosition(edge)[0] - (fm.stringWidth(edgeLabel) / 2);
                int textY = (int) (edgeLabelPosition(edge)[1] + (fm.getAscent() / 2f));
                g2d.drawString(edgeLabel, textX, textY);
            }
        }

        // Draw Nodes


        for (Node node : graph.getNodes()) {
            // Draw node circle
            g2d.setColor(new Color(70, 130, 180)); // Steel Blue
            g2d.fillOval(node.getX() - (int) nodeRadius, node.getY() - (int) nodeRadius, 2 * (int) nodeRadius, 2 * (int) nodeRadius);

            // Draw node outline
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(node.getX() - (int) nodeRadius, node.getY() - (int) nodeRadius, 2 * (int) nodeRadius, 2 * (int) nodeRadius);

            if (showNodeLabels) {
                // Draw node id centered
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = node.getX() - (fm.stringWidth(String.valueOf(node.getId())) / 2);
                int textY = node.getY() + (fm.getAscent() / 2);
                g2d.drawString(String.valueOf(node.getId()), textX, textY);
            }
        }


    }

    private void drawEdges(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke((float) edgeThickness));
        g2d.setColor(Color.WHITE);
        for (Edge edge : graph.getEdges()) {
            double[] edgeNormal = calculateEdgeNormal(edge, nodeRadius/2);
            int x1 = edge.getSourceNode().getX() + (int) edgeNormal[0];
            int y1 = edge.getSourceNode().getY() + (int) edgeNormal[1];
            int x2 = edge.getTargetNode().getX() + (int) edgeNormal[0];
            int y2 = edge.getTargetNode().getY() + (int) edgeNormal[1];

            g2d.drawLine(x1, y1, x2, y2);
            drawArrow(g2d, x1, y1, x2, y2);
        }
    }


    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double len = Math.max(1e-6, Math.hypot(dx, dy));
        double ux = dx / len, uy = dy / len;
        // stop the arrow tip a little before the target node's circle
        double tipX = x2 - ux * (nodeRadius + 2);
        double tipY = y2 - uy * (nodeRadius + 2);
        double back1x = tipX - ux * 8 - (-uy) * 4; // Skalierung per Zoom fixen
        double back1y = tipY - uy * 8 - (ux) * 4;
        double back2x = tipX - ux * 8 + (-uy) * 4;
        double back2y = tipY - uy * 8 + (ux) * 4;

        Path2D.Double arrow = new Path2D.Double();
        arrow.moveTo(tipX, tipY);
        arrow.lineTo(back1x, back1y);
        arrow.lineTo(back2x, back2y);
        arrow.closePath();
        g2d.setColor(Color.WHITE);
        g2d.fill(arrow);
    }

    private double[] edgeLabelPosition(Edge edge){
        double[] edgeNormal = calculateEdgeNormal(edge, nodeRadius/2);
        edgeNormal[0] += edge.getSourceNode().getX() + (edge.getTargetNode().getX() - edge.getSourceNode().getX())/4;
        edgeNormal[1] += edge.getSourceNode().getY() + (edge.getTargetNode().getY() - edge.getSourceNode().getY())/4;

        return edgeNormal;
    }

    private double[] calculateEdgeNormal(Edge edge, double length){
        double[] edgeNormal = new double[2];
        edgeNormal[0] = edge.getTargetNode().getY() - edge.getSourceNode().getY();
        edgeNormal[1] = edge.getSourceNode().getX() - edge.getTargetNode().getX();
        double lengthOfNormal = Math.sqrt(edgeNormal[0]*edgeNormal[0] + edgeNormal[1]*edgeNormal[1]);
        edgeNormal[0] = (edgeNormal[0] / lengthOfNormal) * length;
        edgeNormal[1] = (edgeNormal[1] / lengthOfNormal) * length;

        return edgeNormal;
    }

    private void setupMouse() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    //showContextMenu(e);
                }
            }
        };

        addMouseListener(ma);
    }

//    private void showContextMenu(MouseEvent e) {
//        JPopupMenu menu = new JPopupMenu();
//        try {
//            menu.setLightWeightPopupEnabled(false);
//        } catch (Throwable ignored) {
//
//        }
//
//        var genGraph = new JMenuItem("Generate Graph...");
//        genGraph.addActionListener(e1 -> {
//            var dialog = new GraphGeneratorDialog(this);
//            var d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generate Graph", true);
//            d.setSize(360, 200);
//            d.setLocationRelativeTo(this);
//            d.add(dialog);
//            d.setVisible(true);
//        });
//        menu.add(genGraph);
//
//        menu.show(this, e.getX(), e.getY());
//    }

    public void setGraph(GraphBuilder graphBuilder) {
        this.graph = graphBuilder.buildGraph(this.getWidth(), this.getHeight());
        graphGUI.setGraph(this.graph);
        this.repaint();
    }

    public void setShowEdgeLabels(boolean showEdgeLabels) {
        this.showEdgeLabels = showEdgeLabels;
        System.out.println("showEdgeLabels: " + showEdgeLabels);
        this.repaint();
    }

    public void setNodeEdgeLabels(boolean showNodeLabels) {
        this.showNodeLabels = showNodeLabels;
        System.out.println("showNodeLabels: " + showNodeLabels);
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

//        EdmondsKarp falk = new EdmondsKarp(this.graph);
//        try {
//            System.out.println(falk.runAlgorithm());
//        } catch (InterruptedException ex) {
//            throw new RuntimeException(ex);
//        }
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
            edgeThickness = (BASE_EDGE_THICKNESS / zoomFactor);
            fontSize = (int) (BASE_FONT_SIZE / zoomFactor);
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            if (zoomFactor <= 0.2) return;
            zoomFactor /= 1.1;
            nodeRadius = (BASE_NODE_RADIUS / zoomFactor);
            edgeThickness = (BASE_EDGE_THICKNESS / zoomFactor);
            fontSize = (int) (BASE_FONT_SIZE / zoomFactor);
            repaint();
        }
    }

    public void setGraphGUI(GraphGUI graphGUI) {
        this.graphGUI = graphGUI;
    }

    public void repaintGraphPanel(){
        this.repaint();
    }
}