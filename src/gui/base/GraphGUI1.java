package gui.base;

import javax.swing.*;
import java.awt.event.KeyListener;

public abstract class GraphGUI1 extends JPanel {
    public abstract JPanel getCustomControls();

    public abstract KeyListener getKeyAdapter();
}
