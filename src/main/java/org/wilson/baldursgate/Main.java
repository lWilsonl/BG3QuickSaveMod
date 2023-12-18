package org.wilson.baldursgate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.text.NumberFormatter;

public class Main extends JFrame implements ActionListener {
    private final JFormattedTextField minutesField;
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton exitButton;
    private final JLabel countdownLabel;
    private final JScrollPane consolePane;
    private final JButton toggleConsoleButton;
    private final JTextArea consoleArea;
    private Robot robot;
    private Thread saveThread;

    public Main() {
        // Initialize JPanel
        super("Quick Save tool for Baldurs Gate 3");
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize and add all needed elements for interaction
        /*----------------------------------------------------------------*/
        JLabel label = new JLabel("Enter save interval (Minutes):");
        add(label);

        NumberFormatter formatter = new NumberFormatter();
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.1);
        formatter.setMaximum(Double.MAX_VALUE);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        minutesField = new JFormattedTextField(formatter);
        minutesField.setColumns(10);
        add(minutesField);

        startButton = new JButton("Start Auto Save");
        startButton.addActionListener(this);
        add(startButton);

        stopButton = new JButton("Stop Auto Save");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        add(stopButton);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        add(exitButton);

        countdownLabel = new JLabel();
        consoleArea = new JTextArea(10, 30);
        consoleArea.setEditable(false);
        JScrollPane countDownPane = new JScrollPane(countdownLabel);
        countDownPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        countDownPane.setPreferredSize(new Dimension(200, 100));
        add(countDownPane);

        consolePane = new JScrollPane(consoleArea);
        consolePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        consolePane.setPreferredSize(new Dimension(500, 100));
        consolePane.setVisible(true);
        add(consolePane);

        toggleConsoleButton = new JButton("Toggle Console");
        toggleConsoleButton.addActionListener(this);
        add(toggleConsoleButton);
        /*----------------------------------------------------------------*/

        // Default size of the window to fit everything
        Dimension preferredSize = new Dimension(900, 260);

        // Obtain current screen width and height
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        // Calculate position to center the JPanel
        int x = (screenWidth - preferredSize.width) / 2;
        int y = (screenHeight - preferredSize.height) / 2;

        // Set preferred size and position of window and pack JPanel
        this.setPreferredSize(preferredSize);
        this.setBounds(x, y, preferredSize.width, preferredSize.height);

        pack();
        setVisible(true);

        // Create robot instance
        try {
            robot = new Robot();
        } catch (AWTException e) {
            consoleArea.append("\n Problem with robot initialization " + e);
        }
    }

    public void actionPerformed(ActionEvent event) {
        try {
            // Start button tasks (Start countdown between saves)
            if (event.getSource() == startButton) {
                double minutes = ((Number) minutesField.getValue()).doubleValue();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);

                saveThread = new Thread(() -> {
                    try {
                        while (true) {
                            int seconds = (int) (minutes * 60);
                            for (int i = seconds; i > 0; i--) {
                                countdownLabel.setText("Time left: " + i + " seconds");
                                Thread.sleep(1000);
                            }

                            robot.keyPress(KeyEvent.VK_F5);
                            robot.keyRelease(KeyEvent.VK_F5);
                        }
                    } catch (InterruptedException ex) {
                        consoleArea.append("\n Main thread of Saving tool stopped: " + ex.getMessage());
                    }
                });

                saveThread.start();
            // Stop button tasks (Killing the active thread and restarting objects)
            } else if (event.getSource() == stopButton) {
                if (saveThread != null && saveThread.isAlive()) {
                    saveThread.interrupt();
                }
                countdownLabel.setText("");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            // Exit button task to close app
            } else if (event.getSource() == exitButton) {
                dispose();
                System.exit(0);
            // Console button that hides or shows the console (Optional to see if something breaks)
            } else if (event.getSource() == toggleConsoleButton) {
                if (consolePane.isVisible()) {
                    consolePane.setVisible(false);
                } else {
                    consolePane.setVisible(true);
                }
            }
        } catch(Exception e){
            consoleArea.append("\n Error while executing the task on one of the buttons: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}