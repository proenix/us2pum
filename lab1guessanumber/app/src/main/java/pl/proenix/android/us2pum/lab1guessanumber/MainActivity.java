package pl.proenix.android.us2pum.lab1guessanumber;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int numberToGuess;
    int numberOfTries = 0;
    int numberToCheck;

    Button btnCheck, btnReset;
    TextView textViewError, textViewResult;
    EditText inputNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheck = (Button)findViewById(R.id.buttonCheck);
        btnReset = (Button)findViewById(R.id.buttonReset);
        btnCheck.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        textViewError = (TextView)findViewById(R.id.textViewGuessNumberError);

        inputNumber = (EditText)findViewById(R.id.editTextGuessNumber);

        Random random = new Random();
        numberToGuess = random.nextInt(4001) - 2000;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnCheck.getId()) {
            // Update tries counter
            numberOfTries++;
            // Reset error field;
            textViewError.setText("");

            // Check for win condition
            try {
                if (TextUtils.isEmpty(inputNumber.getText())) {
                    textViewError.setText("Pole nie może być puste.");
                    throw new Exception("Brak liczby.");
                }
                try {
                    numberToCheck = Integer.parseInt(inputNumber.getText().toString());
                } catch (Exception e) {
                    textViewError.setText("Podaj liczbę całkowitą z przedziału -2000 do 2000.");
                    throw new Exception("Nie liczba.");
                }
                if (numberToCheck > 2000) {
                    textViewError.setText("Podana liczba jest za duża. Podaj liczbę całkowitą z przedziału -2000 do 2000.");
                    throw new Exception("Liczba za duża.");
                }
                if (numberToCheck < -2000) {
                    textViewError.setText("Podana liczba jest za mała. Podaj liczbę całkowitą z przedziału -2000 do 2000.");
                    throw new Exception("Liczba za mała.");
                }
            } catch (Exception e) {
                Log.v("E", e.getMessage().toString());
            }

            if (numberToCheck == numberToGuess) {
                textViewResult.setTextColor(Color.GREEN);
                textViewResult.setText("Gratulacje! Poprawnie wytypowałeś liczbę za " + numberOfTries +" próbą.");
            } else if (numberToCheck > numberToGuess) {
                textViewResult.setTextColor(Color.BLACK);
                textViewResult.setText("Poszukiwana liczba jest mniejsza. To próba numer " + numberOfTries + ".");
            } else {
                textViewResult.setTextColor(Color.BLACK);
                textViewResult.setText("Poszukiwana liczba jest większa. To próba numer " + numberOfTries + ".");
            }
        }
        if (v.getId() == btnReset.getId()) {
            // Reset application to starting state.
            numberOfTries = 0;
            textViewResult.setText("");
            textViewError.setText("");

            Random random = new Random();
            numberToGuess = random.nextInt(4001) - 2000;
        }

    }
}
