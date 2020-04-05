package pl.proenix.android.us2pum.lab2quizapp;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Collections;

public class FragmentQuestionMultipleChoice extends Fragment implements View.OnClickListener {
    private OnAnswerListener callback;
    private View view;
    private QuestionMultipleChoice currentQuestion;
    private boolean answered = false;

    private Button[] buttonAnswers;
    private TextView textViewQuestion;

    public void setOnAnsweredListener(OnAnswerListener callback) {
        this.callback = callback;
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    public interface OnAnswerListener {
        public void onAnswerSelected(String answer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_question_multiple_choice, container, false);

        buttonAnswers = new Button[4];
        buttonAnswers[0] = view.findViewById(R.id.buttonAnswerA);
        buttonAnswers[1] = view.findViewById(R.id.buttonAnswerB);
        buttonAnswers[2] = view.findViewById(R.id.buttonAnswerC);
        buttonAnswers[3] = view.findViewById(R.id.buttonAnswerD);

        textViewQuestion = view.findViewById(R.id.textViewQuestion);
        textViewQuestion.setText(currentQuestion.question);

        Collections.shuffle(currentQuestion.answers);
        for (int i=0; i<4; i++) {
            buttonAnswers[i].setOnClickListener(this);
            buttonAnswers[i].setText(currentQuestion.answers.get(i));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (answered) {
            return;
        }
        String answer = "";
        for (int i=0; i<4; i++) {
            if (currentQuestion.correctAnswer.equals(buttonAnswers[i].getText().toString())) {
                if (v.getId() == buttonAnswers[i].getId()) {
                    answer = buttonAnswers[i].getText().toString();
                }
                buttonAnswers[i].setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(buttonAnswers[i].getContext(), R.color.colorWin)));
            } else {
                if (v.getId() == buttonAnswers[i].getId()) {
                    buttonAnswers[i].setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(buttonAnswers[i].getContext(), R.color.colorFail)));
                }
            }
        }
        answered = true;
        callback.onAnswerSelected(answer);
    }

    public void setCurrentQuestion(QuestionMultipleChoice currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
