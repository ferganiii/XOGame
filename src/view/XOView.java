package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class XOView extends JFrame {
    private JLabel statusLabel = new JLabel("Player X's Turn");
    private JPanel boardPanel = new JPanel();
    private JButton[][] buttons = new JButton[3][3];
    private JButton restartButton = new JButton("Restart");

    public XOView() {
        setTitle("XO Game");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        Font font = new Font("Arial", Font.BOLD, 40);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(font);
                boardPanel.add(buttons[i][j]);
            }
        }
        

        add(boardPanel, BorderLayout.CENTER);

        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setEnabled(false);
        add(restartButton, BorderLayout.SOUTH);
    }

    public void showUI() {
        setVisible(true);
    }

    public void addButtonListener(int row, int col, ActionListener listener) {
        buttons[row][col].addActionListener(listener);
    }

    public void setButtonText(int row, int col, String text) {
        buttons[row][col].setText(text);
    }

    public JButton getButton(int row, int col) {
        return buttons[row][col];
    }

    public void setStatusText(String text) {
        statusLabel.setText(text);
    }

    public void addRestartListener(ActionListener listener) {
        restartButton.addActionListener(listener);
    }

    public void enableRestartButton() {
        restartButton.setEnabled(true);
    }

    public void disableRestartButton() {
        restartButton.setEnabled(false);
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
    }
}
