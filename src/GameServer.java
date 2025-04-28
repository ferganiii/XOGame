import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class GameServer extends WebSocketServer {
    private Map<WebSocket, String> players = new HashMap<>();
    private char[] board = new char[9];
    private int currentPlayer = 0;
    private int playerCount = 0;

    public GameServer(int port) {
        super(new InetSocketAddress(port));
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = '-';
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        playerCount++;
        String playerName = "Player " + playerCount;
        players.put(conn, playerName);

        conn.send("ASSIGN_PLAYER:" + playerName);
        broadcast("CONNECTION:" + playerName + " has connected");

        if (playerCount == 2) {
            broadcast("GAME_START:Player 1 (X) starts first");
            broadcast("TURN:Player 1");
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String playerName = players.get(conn);

        if (message.startsWith("MOVE:")) {
            processMove(conn, playerName, message.substring(5));
        }
    }

    private void processMove(WebSocket conn, String playerName, String move) {
        try {
            int position = Integer.parseInt(move.trim());

            if (position < 0 || position > 8) {
                conn.send("ERROR:Invalid position (0-8 only)");
                return;
            }

            if (board[position] != '-') {
                conn.send("ERROR:Position already taken");
                return;
            }

            char symbol = playerName.equals("Player 1") ? 'X' : 'O';
            board[position] = symbol;

            broadcast("MOVE:" + playerName + " placed " + symbol + " at position " + position);

            if (checkWin(symbol)) {
                broadcast("GAME_OVER:" + playerName + " wins!");
                resetGame();
            } else if (isBoardFull()) {
                broadcast("GAME_OVER:It's a draw!");
                resetGame();
            } else {
                currentPlayer = (currentPlayer + 1) % 2;
                String nextPlayer = currentPlayer == 0 ? "Player 1" : "Player 2";
                broadcast("TURN:" + nextPlayer);
            }
        } catch (NumberFormatException e) {
            conn.send("ERROR:Please enter a valid number (0-8)");
        }
    }

    private boolean checkWin(char symbol) {
        int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
            {0, 4, 8}, {2, 4, 6}             // diagonals
        };

        for (int[] condition : winConditions) {
            if (board[condition[0]] == symbol && board[condition[1]] == symbol && board[condition[2]] == symbol) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (char c : board) {
            if (c == '-') {
                return false;
            }
        }
        return true;
    }

    private void resetGame() {
        initializeBoard();
        playerCount = 0;
        currentPlayer = 0;
        players.clear();
        broadcast("RESET:Game reset, waiting for new players...");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String playerName = players.get(conn);
        players.remove(conn);
        broadcast("DISCONNECTED:" + playerName + " has disconnected");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Game server started successfully!");
    }
}
