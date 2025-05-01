import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static String[] board = new String[9];
    private static boolean turnX = true;
    private static List<PlayerHandler> players = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is running and waiting for connections...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                
                if (players.size() < 2) {
                    PlayerHandler player = new PlayerHandler(socket, players.size() + 1);
                    players.add(player);
                    new Thread(player).start();
                    
                    if (players.size() == 2) {
                        broadcast("Both players connected! Game starts now.");
                        updateAllBoards();
                        players.get(0).setTurn(true); // Player X starts first
                    }
                } else {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("SERVER_FULL");
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcast(String message) {
        for (PlayerHandler player : players) {
            player.sendMessage(message);
        }
    }

    private static void updateAllBoards() {
        String boardState = getBoardState();
        for (PlayerHandler player : players) {
            player.sendMessage("BOARD:" + boardState);
        }
    }

    private static String getBoardState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(board[i] != null ? board[i] : " ");
        }
        return sb.toString();
    }

    private static synchronized boolean makeMove(int position, String symbol) {
        if (position < 0 || position >= 9 || board[position] != null) {
            return false;
        }
        
        board[position] = symbol;
        return true;
    }

    private static synchronized boolean checkWinner() {
        int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };

        for (int[] condition : winConditions) {
            if (board[condition[0]] != null && 
                board[condition[0]].equals(board[condition[1]]) && 
                board[condition[0]].equals(board[condition[2]])) {
                return true;
            }
        }
        return false;
    }

    private static synchronized boolean isBoardFull() {
        for (String cell : board) {
            if (cell == null) return false;
        }
        return true;
    }

    private static synchronized void resetGame() {
        Arrays.fill(board, null);
        turnX = true;
    }

    private static class PlayerHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int playerNumber;
        private boolean myTurn;
        private String symbol;

        public PlayerHandler(Socket socket, int playerNumber) {
            this.socket = socket;
            this.playerNumber = playerNumber;
            this.symbol = playerNumber == 1 ? "X" : "O";
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                sendMessage("WELCOME:" + symbol);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void setTurn(boolean turn) {
            this.myTurn = turn;
            if (turn) {
                sendMessage("YOUR_TURN");
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        @Override
        public void run() {
            try {
                String input;
                while ((input = in.readLine()) != null) {
                    if (input.equals("PLAY_AGAIN")) {
                        // لا شيء مطلوب هنا، الخادم سيرسل NEW_GAME تلقائياً
                    } 
                    else if (myTurn && input.startsWith("MOVE:")) {
                        int position = Integer.parseInt(input.substring(5)) - 1;
                        
                        if (makeMove(position, symbol)) {
                            updateAllBoards();
                            
                            if (checkWinner()) {
                                broadcast("GAME_OVER:" + symbol);
                                resetGame();
                                broadcast("NEW_GAME");
                                updateAllBoards();
                                players.get(0).setTurn(true);
                            } else if (isBoardFull()) {
                                broadcast("GAME_OVER:DRAW");
                                resetGame();
                                broadcast("NEW_GAME");
                                updateAllBoards();
                                players.get(0).setTurn(true);
                            } else {
                                // Switch turns
                                myTurn = false;
                                players.get((playerNumber) % 2).setTurn(true);
                            }
                        } else {
                            sendMessage("INVALID_MOVE");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    players.remove(this);
                    if (players.size() > 0) {
                        players.get(0).sendMessage("OPPONENT_DISCONNECTED");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}