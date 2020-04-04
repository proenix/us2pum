package pl.proenix.android.us2pum.lab2tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Button
    TextView textViewCurrentPlayer, textViewPlayerScoreX, textViewPlayerScoreO;
    Button buttonNextGame;

    // Button board array
    private TicTacToe ttt;
    private Button[][] buttonBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ttt = new TicTacToe();
        ttt.initializeBoard();

        // Initialize text fields
        textViewCurrentPlayer = findViewById(R.id.textViewCurrentPlayer);
        textViewPlayerScoreO = findViewById(R.id.textViewPlayerScoreO);
        textViewPlayerScoreX = findViewById(R.id.textViewPlayerScoreX);

        textViewCurrentPlayer.setText(ttt.getCurrentPlayerMarkAsString());

        buttonNextGame = findViewById(R.id.buttonNextGame);
        buttonNextGame.setOnClickListener(this);

        // Initialize buttonBoard reference.
        buttonBoard = new Button[3][3];
        buttonBoard[0][0] = findViewById(R.id.b00);
        buttonBoard[0][1] = findViewById(R.id.b01);
        buttonBoard[0][2] = findViewById(R.id.b02);
        buttonBoard[1][0] = findViewById(R.id.b10);
        buttonBoard[1][1] = findViewById(R.id.b11);
        buttonBoard[1][2] = findViewById(R.id.b12);
        buttonBoard[2][0] = findViewById(R.id.b20);
        buttonBoard[2][1] = findViewById(R.id.b21);
        buttonBoard[2][2] = findViewById(R.id.b22);

        // Apply onclick listener.
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                buttonBoard[i][j].setOnClickListener(this);
                buttonBoard[i][j].setTag(String.valueOf(i) + ";" + String.valueOf(j));
            }
        }
        ttt.printBoard(buttonBoard);
    }

    @Override
    public void onClick(View v) {
        // Prepare new game.
        if (v.getId() == buttonNextGame.getId()) {
              ttt.toggleGameEnd();
              ttt.initializeBoard();
              ttt.printBoard(buttonBoard);
              Toast.makeText(getApplicationContext(), R.string.StartingNewGame, Toast.LENGTH_SHORT).show();

              buttonNextGame.setVisibility(View.INVISIBLE);
        } else {
            // Check if field has mark already
            if (!ttt.placeMark(buttonBoard, ttt.getButtonX((Button) v), ttt.getButtonY((Button) v))) {
                // Create Toast to inform user that action is not possible
                Toast.makeText(getApplicationContext(), R.string.BoardButtonCannotBePlaced, Toast.LENGTH_SHORT).show();
            } else {
                if (ttt.checkWin(buttonBoard)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.PlayerWins, ttt.getCurrentPlayerMarkAsString()), Toast.LENGTH_LONG).show();
                    ttt.addPointsToCurrentPlayer();

                    textViewPlayerScoreX.setText(String.valueOf(ttt.playerScoreX));
                    textViewPlayerScoreO.setText(String.valueOf(ttt.playerScoreO));

                    ttt.toggleGameEnd();
                    buttonNextGame.setVisibility(View.VISIBLE);
                } else if (ttt.isBoardFull()) {
                    Toast.makeText(getApplicationContext(), R.string.BoardButtonFull, Toast.LENGTH_LONG).show();

                    ttt.toggleGameEnd();
                    buttonNextGame.setVisibility(View.VISIBLE);
                }
                ttt.changePlayer();
                textViewCurrentPlayer.setText(ttt.getCurrentPlayerMarkAsString());
            }
        }
    }
}
