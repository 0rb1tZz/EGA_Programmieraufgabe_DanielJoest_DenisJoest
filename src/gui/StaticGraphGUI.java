package gui;

import graph.Graph1;
import graph.Node1;
import gui.base.BaseGUI;
import gui.base.GraphGUI1;
import gui.event.EventBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;


public class StaticGraphGUI<T extends Comparable<T>> extends GraphGUI1 {

    private final Graph1<T> graph;

    private final Map<Node1<T>, double[]> positions = new HashMap<>();

    // Pan and zoom
    private double translateX = 0;
    private double translateY = 0;
    private double scale = 1.0;

    private int lastMouseX;
    private int lastMouseY;
    // Hover state
    private Node1<T> hoveredNode = null;
    private Node1<T>[] hoveredEdge = null;

    private final List<ContextAction> contextActions = new ArrayList<>();

    private static final int NODE_RADIUS = 10;

    public StaticGraphGUI(Graph1<T> graph) {
        this.graph = graph;
        new BaseGUI<>(this, graph);
        EventBus.instance.register(this);

        setupMouse();
    }

    private void setupMouse() {
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    showContextMenu(e);
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // pan only
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                translateX += dx;
                translateY += dy;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // update hover state
                double gx = (e.getX() - translateX) / scale;
                double gy = (e.getY() - translateY) / scale;

                Node1<T> newHoverNode = getNodeAtGraphPosition(gx, gy);
                Node1<T>[] newHoverEdge = null;
                if (newHoverNode == null) {
                    newHoverEdge = getEdgeAtGraphPosition(gx, gy);
                }

                boolean changed = false;
                if (newHoverNode != hoveredNode) {
                    hoveredNode = newHoverNode;
                    changed = true;
                }
                if (newHoverEdge != null) {
                    if (hoveredEdge == null || hoveredEdge[0] != newHoverEdge[0] || hoveredEdge[1] != newHoverEdge[1]) {
                        hoveredEdge = newHoverEdge;
                        changed = true;
                    }
                } else if (hoveredEdge != null) {
                    hoveredEdge = null;
                    changed = true;
                }

                if (changed) {
                    repaint();
                }
            }
        };

        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);

        this.addMouseWheelListener(e -> {
            double oldScale = scale;
            int wheel = e.getWheelRotation();
            double factor = Math.pow(1.1, -wheel);
            double newScale = scale * factor;
            newScale = Math.max(0.1, Math.min(5.0, newScale));
            factor = newScale / oldScale;

            double sx = e.getX();
            double sy = e.getY();
            translateX = factor * translateX + (1 - factor) * sx;
            translateY = factor * translateY + (1 - factor) * sy;

            scale = newScale;
            repaint();
        });
    }

    private void showContextMenu(MouseEvent e) {
        // Use a plain popup menu (no title) and force heavyweight popup to avoid
        // it being added to the same lightweight component hierarchy which
        // can cause painting/translation issues.
        JPopupMenu menu = new JPopupMenu();
        try {
            // prefer heavyweight popup to avoid layout/painting glitches
            menu.setLightWeightPopupEnabled(false);
        } catch (Throwable ignored) {
        }
        // populate dynamic actions
        double gx = (e.getX() - translateX) / scale;
        double gy = (e.getY() - translateY) / scale;
        Node1<T> node = getNodeAtGraphPosition(gx, gy);
        for (ContextAction ca : contextActions) {
            JMenuItem it = new JMenuItem(ca.name);
            it.addActionListener(a -> ca.handler.accept(e, node));
            menu.add(it);
        }
        menu.show(this, e.getX(), e.getY());
    }

    public static  class ContextAction<T extends Comparable<T>> {
        final String name;
        final BiConsumer<MouseEvent, Node1<T>> handler;

        public ContextAction(String name, BiConsumer<MouseEvent, Node1<T>> handler) {
            this.name = name;
            this.handler = handler;
        }
    }

    private void showGenerateDialog() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generate Random Graph1", true);
        d.setSize(360, 200);
        d.setLocationRelativeTo(this);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Node1 count:"));
        JTextField nodeCountF = new JTextField("100", 8);
        row1.add(nodeCountF);
        p.add(row1);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Edge probability (0..1):"));
        JTextField probF = new JTextField("0.05", 8);
        row2.add(probF);
        p.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row3.add(new JLabel("Seed (optional):"));
        JTextField seedF = new JTextField("42", 8);
        row3.add(seedF);
        p.add(row3);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton ok = new JButton("Generate");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(e -> {
            try {
                int cnt = Integer.parseInt(nodeCountF.getText());
                double prob = Double.parseDouble(probF.getText());
                long seed = Long.parseLong(seedF.getText());
                generateRandomGraph(cnt, prob, seed);
                d.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(d, "Invalid input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancel.addActionListener(e -> d.dispose());
        buttons.add(ok);
        buttons.add(cancel);
        p.add(buttons);

        d.add(p);
        d.setVisible(true);
    }

    private void generateRandomGraph(int cnt, double prob, long seed) {
        // clear existing graph
        graph.nodes.clear();
        graph.outgoing.clear();
        positions.clear();

        Random rand = new Random(seed);

        int width = this.getWidth() > 0 ? this.getWidth() : 800;
        int height = this.getHeight() > 0 ? this.getHeight() : 600;

        Node1<T>[] created = new Node1[cnt];
        for (int i = 0; i < cnt; i++) {
            Node1<T> n = graph.newNode();
            n.setLabel("N" + i);
            created[i] = n;
            double x = rand.nextDouble() * width - width / 2.0;
            double y = rand.nextDouble() * height - height / 2.0;
            positions.put(n, new double[]{x, y});
        }

        // Add edges with probability
        for (int i = 0; i < cnt; i++) {
            for (int j = i + 1; j < cnt; j++) {
                if (rand.nextDouble() < prob) {
                    graph.newBiEdge(created[i], created[j]);
                }
            }
        }

        repaint();
    }

    /**
     * Find a node at graph coordinates (gx,gy) within NODE_RADIUS
     */
    private Node1<T> getNodeAtGraphPosition(double gx, double gy) {
        for (Node1<T> n : graph.getAllNodes()) {
            double[] p = positions.get(n);
            if (p == null) continue;
            double dx = p[0] - gx;
            double dy = p[1] - gy;
            if (Math.hypot(dx, dy) <= NODE_RADIUS) return n;
        }
        return null;
    }

    /**
     * Find an edge near the graph coordinates (gx,gy). Returns node pair [a,b] if found, otherwise null.
     */
    private Node1<T>[] getEdgeAtGraphPosition(double gx, double gy) {
        double threshold = Math.max(6.0, NODE_RADIUS);
        Node1<T>[] best = null;
        double bestDist = Double.MAX_VALUE;
        for (Node1<T> a : graph.getAllNodes()) {
            double[] p1 = positions.get(a);
            if (p1 == null) continue;
            for (Node1<T> b : a.getNeighbors()) {
                if (a.getId() >= b.getId()) continue;
                double[] p2 = positions.get(b);
                if (p2 == null) continue;
                double d = pointToSegmentDistance(gx, gy, p1[0], p1[1], p2[0], p2[1]);
                if (d < threshold && d < bestDist) {
                    bestDist = d;
                    @SuppressWarnings("unchecked")
                    Node1<T>[] pair = (Node1<T>[]) new Node1[]{a, b};
                    best = pair;
                }
            }
        }
        return best;
    }

    public void addContextMenu(ContextAction contextAction) {
        this.contextActions.add(contextAction);
    }

    private double pointToSegmentDistance(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            dx = px - x1;
            dy = py - y1;
            return Math.hypot(dx, dy);
        }
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        double projx = x1 + t * dx;
        double projy = y1 + t * dy;
        return Math.hypot(px - projx, py - projy);
    }

    // direct-highlight logic: no component BFS

    @Override
    public JPanel getCustomControls() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        JButton gen = new JButton("Generate...");
        gen.addActionListener(e -> showGenerateDialog());
        p.add(gen);
        p.add(Box.createHorizontalStrut(10));
        return p;
    }

    @Override
    public KeyAdapter getKeyAdapter() {
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform at = new AffineTransform(scale, 0, 0, scale, translateX, translateY);
        g2d.setTransform(at);

        // edges
        g2d.setColor(new Color(200,200,200));
        g2d.setStroke(new BasicStroke(1.0f/(float)scale));
        for (Node1<T> n : graph.getAllNodes()) {
            double[] p1 = positions.get(n);
            if (p1 == null) continue;
            for (Node1<T> m : n.getNeighbors()) {
                // draw each undirected edge only once
                if (n.getId() >= m.getId()) continue;
                double[] p2 = positions.get(m);
                if (p2 == null) continue;
                // choose color based on hover: if hovering a node, highlight incident edges; if hovering an edge, highlight that edge
                if (hoveredNode != null && (hoveredNode == n || hoveredNode == m)) {
                    g2d.setColor(Color.ORANGE);
                } else if (hoveredEdge != null && ((hoveredEdge[0] == n && hoveredEdge[1] == m) || (hoveredEdge[0] == m && hoveredEdge[1] == n))) {
                    g2d.setColor(Color.YELLOW);
                } else {
                    g2d.setColor(new Color(200,200,200));
                }
                g2d.drawLine((int)p1[0], (int)p1[1], (int)p2[0], (int)p2[1]);
            }
        }
        // nodes
        for (Node1<T> n : graph.getAllNodes()) {
            double[] p = positions.get(n);
            if (p == null) continue;
            int x = (int)p[0];
            int y = (int)p[1];
            // node fill color: if hovering an edge, highlight its endpoints; hovered node is yellow
            if (hoveredEdge != null && (n == hoveredEdge[0] || n == hoveredEdge[1])) {
                g2d.setColor(Color.ORANGE);
            } else if (n == hoveredNode) {
                g2d.setColor(Color.YELLOW);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
            }
            int r = NODE_RADIUS;
            if (n == hoveredNode) r = (int)(NODE_RADIUS * 1.4);
            g2d.fillOval(x-r, y-r, r*2, r*2);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x-r, y-r, r*2, r*2);
            String label = n.getLabel()!=null?n.getLabel():String.valueOf(n.getId());
            g2d.drawString(label, x - g2d.getFontMetrics().stringWidth(label)/2, y + 4);
        }

        g2d.dispose();
    }
}


