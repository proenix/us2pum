package pl.proenix.android.us2pum.lab6lang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Fragment for Test action.
 */
public class TestFragment extends Fragment implements View.OnClickListener {

    private static final String DEBUG_TAG = "AndroidLang";

    private static final int TEST_MODE_TO_ENGLISH = Test.TEST_MODE_TO_ENGLISH;
    private static final int TEST_MODE_TO_POLISH = Test.TEST_MODE_TO_POLISH;
    private static final int TEST_MODE_TO_BOTH = Test.TEST_MODE_TO_BOTH;

    private Bundle bundle;
    private View view;
    private int mode;

    private int currentWordsProgress = 0;
    private int numberOfTests;

    private Button buttonSubmitAnswer;
    private boolean stateSubmitted;

    private TextView textViewInputBarIndicator;
    private TextView textViewAnswerLeft;

    private TextView textViewWordToTest;
    private EditText editTextWordToTest;
    private TextView textViewNotWord;

    private LinearLayout linearLayoutAnswers;

    private AlertDialog.Builder dialogBuilder;
    private DialogInterface.OnClickListener onExitDialogClickListener;

    private List<Test> testsToDo;
    private List<Test> testsCompleted;
    private Test test;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        bundle = getArguments();
        try {
            mode = bundle != null ? bundle.getInt("mode") : TEST_MODE_TO_BOTH;
            numberOfTests = bundle != null ? bundle.getInt("numberOfTests") : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        view = inflater.inflate(R.layout.fragment_test, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Word> words = MainActivity.db.getWordsByLanguageAndLearnableAndLearnState(
                Word.WORD_LANGUAGE_ENGLISH,
                Word.WORD_LEARNABLE,
                Word.WORD_TO_LEARN,
                ">",
                numberOfTests);

        testsToDo = new ArrayList<Test>();
        testsCompleted = new ArrayList<Test>();
        if (mode == Test.TEST_MODE_TO_BOTH) {
            for (Word word : words) {
                testsToDo.add(new Test(word, Test.TEST_MODE_TO_ENGLISH));
                testsToDo.add(new Test(word, Test.TEST_MODE_TO_POLISH));
            }
            numberOfTests *= 2;
        }
        if (mode == Test.TEST_MODE_TO_ENGLISH) {
            for (Word word : words) {
                testsToDo.add(new Test(word, Test.TEST_MODE_TO_ENGLISH));
            }
        }
        if (mode == Test.TEST_MODE_TO_POLISH) {
            for (Word word : words) {
                testsToDo.add(new Test(word, Test.TEST_MODE_TO_POLISH));
            }
        }
        Log.d("AndroidLang", "Number of test in tests List: "+String.valueOf(testsToDo.size()));

        // Randomize order of tests
        Collections.shuffle(testsToDo);
        buttonSubmitAnswer = view.findViewById(R.id.buttonSubmitAnswer);
        buttonSubmitAnswer.setOnClickListener(this);

        textViewInputBarIndicator = view.findViewById(R.id.textViewInputBarIndicator);
        textViewWordToTest = view.findViewById(R.id.textViewWordToTest);
        editTextWordToTest = view.findViewById(R.id.editTextWordToTest);
        textViewNotWord = view.findViewById(R.id.textViewNotWord);
        textViewAnswerLeft = view.findViewById(R.id.textViewAsnswersLeft);
        linearLayoutAnswers = view.findViewById(R.id.linearLayoutAnswers);

        // Add callback for return button pressed.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                dialogBuilder.setMessage("Do you really want to finish test?").
                        setPositiveButton(R.string.yes, onExitDialogClickListener).
                        setNegativeButton("No, continue", onExitDialogClickListener).show();
            }
        };
        dialogBuilder = new AlertDialog.Builder(view.getContext());
        onExitDialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        goToSummary();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        // Proceed to displaying and asking answers until end moving already answered question out of loop.
        displayNextTest();
    }

    /**
     * Colors and set text of input bar indicator depending on current input type requested.
     */
    private void setInputBarIndicatorColors(int mode) {
        if (mode == TEST_MODE_TO_ENGLISH) {
            textViewInputBarIndicator.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorBackgroundToEnglish));
            textViewInputBarIndicator.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorTextToEnglish));
            textViewInputBarIndicator.setText(R.string.input_english_writing);
        } else {
            textViewInputBarIndicator.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorBackgroundToPolish));
            textViewInputBarIndicator.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorTextToPolish));
            textViewInputBarIndicator.setText(R.string.input_polish_meaning);
        }
    }

    /**
     * Check if answer is in answers pool.
     */
    private void checkAnswer() {
        String userInputtedAnswer = editTextWordToTest.getText().toString();

        // TODO: 08/05/2020 Move messages to top of screen so is more visible.
        if (test.checkAnswer(userInputtedAnswer)) {
            currentWordsProgress++;
            testsCompleted.add(testsToDo.get(0));
            testsToDo.remove(0);
            if (test.getMinDistance() > 0) {
                // Display that answer was a little bit off.
                Toast.makeText(view.getContext(), String.format("Your answer \"%s\" was a little bit off. Closes answer was %s", userInputtedAnswer, test.getProbableAnswer()), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(view.getContext(), "Answer OK!", Toast.LENGTH_LONG).show();
            }
        } else {
            if (test.isResultTryAgain()) {
                Toast.makeText(view.getContext(), "Your answer was not what what we wanted. Please try other word.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(view.getContext(), "Your answer was incorrect.", Toast.LENGTH_LONG).show();
                Collections.shuffle(testsToDo);
            }
        }
        Log.d("AndroidLang", "Loading next test.");
        displayNextTest();
    }

    /**
     * Populate view with next test data or go to summary if no tests remain.
     *
     */
    private void displayNextTest() {
        // Check if any test is left
        if (testsToDo.size() == 0) {
            Log.d("AndroidLang", "Tests done going to summary.");

            goToSummary();
            return;
        }
        // Get first word from testToDo list and display to user.
        test = testsToDo.get(0);

        // Set indicator bar color
        setInputBarIndicatorColors(test.getMode());
        if (test.isResultTryAgain()) {
            textViewNotWord.setText(getString(R.string.word_not, test.getRetryHint()));
        } else {
            textViewNotWord.setText("");
        }
        textViewWordToTest.setText(test.getWordToShow());
        editTextWordToTest.setText("");
        textViewAnswerLeft.setText(String.format("%s/%s", String.valueOf(currentWordsProgress), String.valueOf(numberOfTests)));

        // Clear answers.
        linearLayoutAnswers.removeAllViews();
    }

    /**
     * Prepare Bundle and navigate to Summary Fragment.
     */
    private void goToSummary() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("TestSummary", (ArrayList<? extends Parcelable>) testsCompleted);

        NavHostFragment.findNavController(TestFragment.this)
                .navigate(R.id.action_Test_to_TestSummary, bundle);
    }

    @Override
    public void onClick(View v) {
        // On answer submitted
        if (v.getId() == buttonSubmitAnswer.getId()) {
            int userInputtedAnswerLength = editTextWordToTest.getText().toString().length();
            if (userInputtedAnswerLength == 0) {
                return;
            }
            checkAnswer();
        }
    }
}
