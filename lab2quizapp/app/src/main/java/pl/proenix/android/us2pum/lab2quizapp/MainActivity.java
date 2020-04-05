package pl.proenix.android.us2pum.lab2quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements FragmentQuestionMultipleChoice.OnAnswerListener, FragmentQuestionInput.OnAnswerListener {

    public Question questions[];
    public Question currentQuestion;
    public Random random;

    TextView textViewScore;
    ImageView imageViewQuestion;
    private int score;

    private static Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        textViewScore = findViewById(R.id.textViewScore);
        imageViewQuestion = findViewById(R.id.imageViewQuestion);

        questions = new Question[]{
                new Question(Question.QUESTION_INPUT,R.drawable.zebra,"Jak nazywa się zwierzę na obrazku?","Zebra"),
                new Question(Question.QUESTION_INPUT,R.drawable.pies,"Jak nóg ma pies?","4"),
                new Question(Question.QUESTION_INPUT,R.drawable.pies2,"Jak nazywa się zwierzę na obrazku?","Pies"),
                new QuestionMultipleChoice(Question.QUESTION_MULTI, R.drawable.lama, "Więcej niż jedno zwierzę to?", "Stado", "Lama", "Owca", "Nie wiem", "Stado"),
                new QuestionMultipleChoice(Question.QUESTION_MULTI, R.drawable.kon, "Ile nóg ma koń?", "4", "2", "4", "8", "100"),
                new QuestionMultipleChoice(Question.QUESTION_MULTI, R.drawable.tygrys, "Jak nazywa się zwierzę na obrazku?", "Tygrys", "Jeleń", "Zebra", "Kot", "Tygrys")
        };

        random = new Random();
        // load first question
        loadNextQuestion();
    }

    public void loadNextQuestion() {
        currentQuestion = questions[random.nextInt(questions.length)];
        imageViewQuestion.setImageResource(currentQuestion.imageResource);

        if (currentQuestion.isMultipleChoice()) {
            loadFragment(new FragmentQuestionMultipleChoice());
        } else {
            loadFragment(new FragmentQuestionInput());
        }
    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        fragment.setArguments(getIntent().getExtras());
        transaction.replace(R.id.frameLayout, fragment);
        //transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    public void addPoint() {
        score += 1;
        textViewScore.setText(String.valueOf(score));
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof FragmentQuestionMultipleChoice) {
            FragmentQuestionMultipleChoice fragmentQuestionMultipleChoice = (FragmentQuestionMultipleChoice) fragment;
            fragmentQuestionMultipleChoice.setOnAnsweredListener(this);
            fragmentQuestionMultipleChoice.setCurrentQuestion((QuestionMultipleChoice) currentQuestion);
        }
        if (fragment instanceof FragmentQuestionInput) {
            FragmentQuestionInput fragmentQuestionInput = (FragmentQuestionInput) fragment;
            fragmentQuestionInput.setOnAnsweredListener(this);
            fragmentQuestionInput.setCurrentQuestion(currentQuestion);
        }
    }

    @Override
    public void onAnswerSelected(String answer) {
        if (answer.equals(currentQuestion.correctAnswer)) {
            addPoint();
            Toast.makeText(getBaseContext(), R.string.correctAnswer, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), R.string.incorrectAnswer, Toast.LENGTH_LONG).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNextQuestion();
            }
        }, 1000);
    }

    @Override
    public void onAnswerInput(String answer) {
        if (answer.equals(currentQuestion.correctAnswer.toUpperCase())) {
            addPoint();
            Toast.makeText(getBaseContext(), R.string.correctAnswer, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.incorrectAnswerCorrectIs, currentQuestion.correctAnswer), Toast.LENGTH_LONG).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNextQuestion();
            }
        }, 1000);
    }
}
