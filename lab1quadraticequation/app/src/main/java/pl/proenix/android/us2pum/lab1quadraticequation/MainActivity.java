package pl.proenix.android.us2pum.lab1quadraticequation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    double A,B,C;
    double delta;

    TextView textViewMsg, textViewRoot1, textViewRoot2, textViewFunction;

    EditText inputA, inputB, inputC;
    TextView inputAError, inputBError, inputCError;

    Button buttonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonCount = (Button)findViewById(R.id.buttonCount);
        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberFormat nf = NumberFormat.getInstance();

                // Check if A, B, C is initialized.
                if ((inputA.getText() != null) &&
                    ((inputAError.getText() == getResources().getString(R.string.NotQuadraticEquation)) || (inputAError.getText() == "")) &&
                    (inputB.getText() != null) && (inputBError.getText() == "") &&
                    (inputC.getText() != null) && (inputCError.getText() == ""))
                {
                    // Check if Quadratic Equation
                    if (A != 0) {
                        delta = B*B-4*A*C;
                        if (delta < 0) {
                            textViewMsg.setText("Funkcja jest funkcją kwadratową. Istnieją dwa rozwiązania nierzeczywiste.");
                            textViewRoot1.setText("Rozwiązanie 1: " + nf.format(-B/(2*A)) + " + i" + nf.format(Math.sqrt(-delta)/(2*A)));
                            textViewRoot2.setText("Rozwiązanie 2: " + nf.format(-B/(2*A)) + " - i" + nf.format(Math.sqrt(-delta)/(2*A)));
                        } else if (delta > 0) {
                            textViewMsg.setText("Funkcja jest funkcją kwadratową. Istnieją dwa rozwiązania rozwiązania rzeczywiste.");
                            textViewRoot1.setText("Rozwiązanie 1: " + nf.format((-B + Math.sqrt(delta))/(2*A)));
                            textViewRoot2.setText("Rozwiązanie 2: " + nf.format((-B - Math.sqrt(delta))/(2*A)));
                        } else {
                            textViewMsg.setText("Funkcja jest funkcją kwadratową. Istnieje jedno rozwiązanie rzeczywiste.");
                            textViewRoot1.setText("Rozwiązanie 1: " + nf.format(-B/(2*A)));
                            textViewRoot2.setText("");
                        }
                        textViewFunction.setText("y = " + nf.format(A) + "x^2 + " + nf.format(B) + "x + " + nf.format(C));
                    } else if (B != 0) {
                        textViewMsg.setText("Funkcja jest funkcją liniową.");
                        textViewRoot1.setText("Rozwiązanie: " + nf.format(-C/B));
                        textViewRoot2.setText("");
                        textViewFunction.setText("y = " + nf.format(B) + "x + " + nf.format(C));
                    } else {
                        textViewMsg.setText("Funcja jest funkcją stałą.");
                        textViewRoot1.setText("");
                        textViewRoot2.setText("");
                        textViewFunction.setText("y = " + nf.format(C));
                    }
                } else {
                    textViewMsg.setText("Wprowadź prawidłowe dane!");
                    textViewRoot1.setText("");
                    textViewRoot2.setText("");
                }
            }
        });

        // Set and check param A
        inputA = (EditText)findViewById(R.id.editTextA);
        inputA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputAError.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (TextUtils.isEmpty(s)) {
                        inputAError.setText("Pole nie może być puste.");
                        throw new Exception("Podaj liczbę.");
                    }
                    try {
                        A = Double.parseDouble(inputA.getText().toString());
                    } catch (Exception e) {
                        inputAError.setText("Błąd. Podaj prawidłową liczbę.");
                        throw new Exception("Nie liczba.");
                    }
                    if (A == 0) {
                        inputAError.setText(R.string.NotQuadraticEquation);
                    }
                } catch (Exception e) {
                    Log.v("ConversionError", e.getMessage().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });

        // Set and check param B
        inputB = (EditText)findViewById(R.id.editTextB);
        inputB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputBError.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (TextUtils.isEmpty(s)) {
                        inputBError.setText("Pole nie może być puste.");
                        throw new Exception("Podaj liczbę.");
                    }
                    try {
                        B = Double.parseDouble(inputB.getText().toString());
                    } catch (Exception e) {
                        inputBError.setText("Błąd. Podaj prawidłową liczbę.");
                        throw new Exception("Nie liczba.");
                    }
                } catch (Exception e) {
                    Log.v("ConversionError", e.getMessage().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });

        // Set and check param C
        inputC = (EditText)findViewById(R.id.editTextC);
        inputC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputCError.setText("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (TextUtils.isEmpty(s)) {
                        inputCError.setText("Pole nie może być puste.");
                        throw new Exception("Podaj liczbę.");
                    }
                    try {
                        C = Double.parseDouble(inputC.getText().toString());
                    } catch (Exception e) {
                        inputCError.setText("Błąd. Podaj prawidłową liczbę.");
                        throw new Exception("Nie liczba.");
                    }
                } catch (Exception e) {
                    Log.v("ConversionError", e.getMessage().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });

        inputAError = (TextView)findViewById(R.id.textViewAError);
        inputBError = (TextView)findViewById(R.id.textViewBError);
        inputCError = (TextView)findViewById(R.id.textViewCError);

        textViewMsg = (TextView)findViewById(R.id.textViewMsg);
        textViewRoot1 = (TextView)findViewById(R.id.textViewRoot1);
        textViewRoot2 = (TextView)findViewById(R.id.textViewRoot2);
        textViewFunction = (TextView)findViewById(R.id.textViewFunct);
    }
}
