package pl.proenix.android.us2pum.lab2tictactoe;

import android.content.res.ColorStateList;
import android.widget.Button;

import androidx.core.content.ContextCompat;

/**
 * Class for Tic Tac Toe internal game logic.
 */
public class TicTacToe {
    private char[][] board; // internal for remembering board state
    private char currentPlayerMark = 'x'; // player mark
    public int playerScoreX, playerScoreO = 0; // players score
    private boolean gameEnd = false; // no available move indicator

    public TicTacToe() {
        board = new char[3][3];
    }

    /**
     * Initialize board with empty symbol.
     */
    public void initializeBoard() {
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                board[i][j] = '-';
            }
        }
        gameEnd = false;
    }

    /**
     * Sets buttons style and content.
     * @param buttonBoard Array of buttons displayed to player.
     */
    public void printBoard(Button[][] buttonBoard) {
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (board[i][j] == '-') {
                    buttonBoard[i][j].setText(" ");
                    buttonBoard[i][j].setBackgroundTintList(null);
                } else {
                    buttonBoard[i][j].setText(String.valueOf(board[i][j]));
                }
            }
        }
    }

    /**
     * Check if board is fully played.
     * @return boolean
     */
    public boolean isBoardFull() {
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (board[i][j] == '-')
                    return false;
            }
        }
        return true;
    }

    /**
     * Check if current state is winning state.
     * @param buttonBoard Array of buttons displayed to player.
     * @return boolean
     */
    public boolean checkWin(Button[][] buttonBoard) {

        // check rows
        for (int i=0; i<3; i++) {
            if (checkLine(board[i][0],board[i][1],board[i][2]) == true) {
                colorWinLine(buttonBoard,i,0,i,1,i,2);
                return true;
            }
        }
        // check cols
        for (int i=0; i<3; i++) {
            if (checkLine(board[0][i],board[1][i],board[2][i]) == true) {
                colorWinLine(buttonBoard,0,i,1,i,2,i);
                return true;
            }
        }
        // check diagonals
        if (checkLine(board[0][0], board[1][1], board[2][2]) == true) {
            colorWinLine(buttonBoard,0,0,1,1,2,2);
            return true;
        }
        if (checkLine(board[0][2], board[1][1], board[2][0]) == true) {
            colorWinLine(buttonBoard,0,2,1,1,2,0);
            return true;
        }
        return false;
    }

    /**
     * Check is three values are the same.
     * @param a Field mark.
     * @param b Field mark.
     * @param c Field mark.
     * @return boolean If all marks are the same and placed by player.
     */
    private boolean checkLine(char a, char b, char c) {
        return ((a != '-') && (a == b) && (b == c));
    }

    /**
     * Set button tint color for winning row/col/diagonal.
     * @param buttonBoard Array of buttons displayed to player.
     * @param x0 Column number of first button.
     * @param x1 Column number of second button.
     * @param x2 Column number of third button.
     * @param y0 Row number of first button.
     * @param y1 Row number of second button.
     * @param y2 Row number of third button.
     */
    private void colorWinLine(Button[][] buttonBoard, int x0, int y0, int x1, int y1, int x2, int y2) {
        buttonBoard[x0][y0].setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(buttonBoard[x0][y0].getContext(), R.color.colorWin)));
        buttonBoard[x1][y1].setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(buttonBoard[x1][y2].getContext(), R.color.colorWin)));
        buttonBoard[x2][y2].setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(buttonBoard[x2][y2].getContext(), R.color.colorWin)));
    }

    /**
     * Rotate through player marks.
     */
    public void changePlayer() {
        if (currentPlayerMark == 'x') {
            currentPlayerMark = 'o';
        } else {
            currentPlayerMark = 'x';
        }
    }

    /**
     * Get current player mark.
     * @return char Current player mark.
     */
    public char getCurrentPlayerMark() {
        return currentPlayerMark;
    }

    /**
     * Get current player mark.
     * @return String Current player mark.
     */
    public String getCurrentPlayerMarkAsString() {
        return String.valueOf(currentPlayerMark);
    }

    /**
     * Place current player mark on selected field.
     * @param buttonBoard Array of buttons displayed to player.
     * @param col Column number of button.
     * @param row Row number of button.
     * @return boolean Returns false if field is already used.
     */
    public boolean placeMark(Button[][] buttonBoard, int row, int col) {
        if (gameEnd == true) {
            return false;
        }
        if (board[row][col] == '-') {
            board[row][col] = currentPlayerMark;
            buttonBoard[row][col].setText(this.getCurrentPlayerMarkAsString());
            return true;
        }
        return false;
    }

    /**
     * Helper function to get column number of button.
     * @param button Button from which column number will be extracted.
     * @return int Column number
     */
    public int getButtonX(Button button)
    {
        String[] s = button.getTag().toString().split(";",2);
        int x = Integer.parseInt(s[0]);
        return x;
    }

    /**
     * Helper function to get row number of button.
     * @param button Button from which row number will be extracted.
     * @return int Row number
     */
    public int getButtonY(Button button)
    {
        String[] s = button.getTag().toString().split(";",2);
        int y = Integer.parseInt(s[1]);
        return y;
    }

    /**
     * Add one point to current player score.
     */
    public void addPointsToCurrentPlayer()
    {
        if (currentPlayerMark == 'x') {
            playerScoreX += 1;
        } else {
            playerScoreO += 1;
        }
    }

    /**
     * Toggle gameEnd variable used to check if next move is allowed.
     */
    public void toggleGameEnd()
    {
        if (gameEnd == true) {
            gameEnd = false;
        } else {
            gameEnd = true;
        }
    }
}
