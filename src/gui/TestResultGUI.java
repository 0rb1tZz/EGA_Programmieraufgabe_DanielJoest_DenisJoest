package gui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class TestResultGUI extends JFrame {

    private JPanel contentPane = new JPanel();
    private JScrollPane scrollPane;
    private JTextArea testResultTextArea = new JTextArea("");

    public TestResultGUI() {
        super("TestResultGUI");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1600, 900);

        contentPane.add(testResultTextArea);
        contentPane.setBackground(testResultTextArea.getBackground());
        scrollPane = new JScrollPane(contentPane);
        contentPane.setAutoscrolls(true);
        scrollPane.setPreferredSize(new Dimension(1600, 900));
        this.add(scrollPane, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {this.validate(); this.repaint(); this.setVisible(true);});
    }



    public void setTestResultText(String testResults) {
        testResultTextArea.setText(testResults);
    }

    public void addTitle(String title) {
        testResultTextArea.setFont(testResultTextArea.getFont().deriveFont(Font.BOLD, 16f));
        testResultTextArea.append(title + "\n\n\n");
        testResultTextArea.setFont(testResultTextArea.getFont().deriveFont(Font.PLAIN, 12f));
    }

    public void addHeadline(String headline) {
        testResultTextArea.setFont(testResultTextArea.getFont().deriveFont(Font.BOLD, 14f));
        testResultTextArea.append(headline + "\n\n");
        testResultTextArea.setFont(testResultTextArea.getFont().deriveFont(Font.PLAIN, 12f));
    }

    public void addTestResult(LinkedList<String> testResults) {
        // testResultTextArea.append("------------------------------------------\n");
        for (String s: testResults) {
            testResultTextArea.append(s + "\n");
        }
        testResultTextArea.append("\n---------------------------------------------------------------------------\n\n");
    }
}
