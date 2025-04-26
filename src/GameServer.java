import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.util.HashMap;
import java.util.Map;

public class GameServer extends WebSocketServer {
    private Map<WebSocket, String> players = new HashMap<>();
    private char[] board = new char[9];
    private int currentPlayer = 0;
    private int playerCount = 0;

    public GameServer(int port) {
        super(port);
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
            
            broadcast("MOVE:" + playerName + " placed " + symbol + " at " + position);
            
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
            conn.send("ERROR:Please enter a valid number");
        }
    }

    // ... باقي الدوال كما هي (checkWin, isBoardFull, resetGame)
}