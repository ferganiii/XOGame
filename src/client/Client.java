import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static JButton[] buttons = new JButton[9];
    private static String mySymbol;
    private static boolean myTurn = false;
    private static JFrame frame;
    private static JPanel gamePanel;
    private static JPanel buttonPanel;

    public static void main(String[] args) {
        initializeGUI();
        connectToServer();
    }

    private static void initializeGUI() {
        frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 450);

        gamePanel = new JPanel(new GridLayout(3, 3));
        buttonPanel = new JPanel(new FlowLayout());

        // Initialize game buttons
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 60));
            final int index = i;
            buttons[i].addActionListener(e -> makeMove(index));
            gamePanel.add(buttons[i]);
        }

        // Initialize control buttons
        JButton playAgainButton = new JButton("Play Again");
        JButton exitButton = new JButton("Exit");
        
        playAgainButton.addActionListener(e -> {
            out.println("PLAY_AGAIN");
            buttonPanel.setVisible(false);
        });
        
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(playAgainButton);
        buttonPanel.add(exitButton);
        buttonPanel.setVisible(false);

        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Handle server messages
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Server: " + message);
                
                if (message.startsWith("WELCOME:")) {
                    mySymbol = message.substring(8);
                    frame.setTitle("Tic-Tac-Toe - Player " + mySymbol);
                } 
                else if (message.equals("YOUR_TURN")) {
                    myTurn = true;
                    enableButtons();
                } 
                else if (message.startsWith("BOARD:")) {
                    updateBoard(message.substring(6));
                } 
                else if (message.startsWith("GAME_OVER:")) {
                    String result = message.substring(10);
                    String msg = result.equals(mySymbol) ? "You win!" : 
                               result.equals("DRAW") ? "It's a draw!" : "You lose!";
                    JOptionPane.showMessageDialog(frame, msg);
                    disableButtons();
                    buttonPanel.setVisible(true);
                }
                else if (message.equals("NEW_GAME")) {
                    resetBoard();
                }
                else if (message.equals("OPPONENT_DISCONNECTED")) {
                    JOptionPane.showMessageDialog(frame, "Opponent disconnected!");
                    disableButtons();
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void makeMove(int index) {
        if (myTurn && buttons[index].getText().isEmpty()) {
            out.println("MOVE:" + (index + 1));
            buttons[index].setText(mySymbol);
            myTurn = false;
            disableButtons();
        }
    }

    private static void updateBoard(String boardState) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 9; i++) {
                char c = boardState.charAt(i);
                buttons[i].setText(c == ' ' ? "" : String.valueOf(c));
            }
        });
    }

    private static void enableButtons() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 9; i++) {
                buttons[i].setEnabled(buttons[i].getText().isEmpty());
            }
        });
    }

    private static void disableButtons() {
        SwingUtilities.invokeLater(() -> {
            for (JButton button : buttons) {
                button.setEnabled(false);
            }
        });
    }

    private static void resetBoard() {
        SwingUtilities.invokeLater(() -> {
            for (JButton button : buttons) {
                button.setText("");
            }
        });
    }
}