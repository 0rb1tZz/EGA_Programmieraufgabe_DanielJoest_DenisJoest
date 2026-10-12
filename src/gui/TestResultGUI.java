package gui;

import javax.swing.*;

public class TestResultGUI extends JFrame {

    private JTextArea testResultTextArea = new JTextArea("Test Results here...");

    public TestResultGUI() {
        super("TestResultGUI");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1600, 900);

        this.add(testResultTextArea);

        SwingUtilities.invokeLater(() -> {this.validate(); this.repaint(); this.setVisible(true);});
    }



    public void setTestResultText(String testResults) {
        testResultTextArea.setText(testResults);
    }

    public void addTestResult(String testResult) {
        testResultTextArea.append(testResult + "\n");
    }
}
