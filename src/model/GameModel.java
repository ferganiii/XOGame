package model;

public class GameModel {
    private String currentPlayer = "X";
    private String[][] board = new String[3][3];

    public GameModel() {
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void togglePlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
    }

    public String getBoardCell(int row, int col) {
        return board[row][col];
    }

    public void setBoardCell(int row, int col, String value) {
        board[row][col] = value;
    }

    public boolean checkWin(String player) {
        // Rows, columns, and diagonals check
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(player) && board[i][1].equals(player) && board[i][2].equals(player))
                return true;
        }

        for (int j = 0; j < 3; j++) {
            if (board[0][j].equals(player) && board[1][j].equals(player) && board[2][j].equals(player))
                return true;
        }

        if (board[0][0].equals(player) && board[1][1].equals(player) && board[2][2].equals(player))
            return true;

        if (board[0][2].equals(player) && board[1][1].equals(player) && board[2][0].equals(player))
            return true;

        return false;
    }

    public boolean isDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals(""))
                    return false;
            }
        }
        return true;
    }
}
