package pl.proenix.android.us2pum.lab2quizapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Collections;

public class FragmentQuestionInput extends Fragment {
    private OnAnswerListener callback;
    private View view;
    private Question currentQuestion;
    private boolean answered = false;

    private Button buttonSubmit;
    private TextView textViewQuestion;
    private EditText editTextInput;


    public void setOnAnsweredListener(OnAnswerListener callback) {
        this.callback = callback;
    }

    public interface OnAnswerListener {
        public void onAnswerInput(String answer);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_question_input, container, false);

        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        textViewQuestion = view.findViewById(R.id.textViewQuestion2);
        editTextInput = view.findViewById(R.id.editTextInput);

        textViewQuestion.setText(currentQuestion.question);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answered) {
                    return;
                }

                if (editTextInput.getText() != null) {
                    answered = true;
                    String answer = editTextInput.getText().toString().toUpperCase();
                    callback.onAnswerInput(answer);
                } else {
                    Toast.makeText(getContext(), R.string.answerFieldCannotBeEmpty, Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
