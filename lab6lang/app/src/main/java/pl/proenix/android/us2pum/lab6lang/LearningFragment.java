package pl.proenix.android.us2pum.lab6lang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.List;


// TODO: 03/05/2020 Remove button on bottom to exit?
public class LearningFragment extends Fragment implements View.OnClickListener {

    private int currentWordIndex = 0;
    private View view;
    private List<Word> words;
    private Word word;
    private Button buttonSetAsLearned;
    private AlertDialog.Builder dialogBuilder;
    private DialogInterface.OnClickListener unlearnDialogClickListener;
    private View buttonNextWord;
    private View buttonPreviousWord;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_learning, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get English words with learnable status and not learned.
        words = MainActivity.db.getWordsByLanguageAndLearnableAndLearnState(Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE, Word.WORD_TO_LEARN, "=", null);
        Collections.shuffle(words);
        if (words.size() == 0) {
            Toast.makeText(view.getContext(), R.string.all_words_learned, Toast.LENGTH_LONG).show();
        }

        // Get English words with learnable status and already learned.
        words.addAll(MainActivity.db.getWordsByLanguageAndLearnableAndLearnState(Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE, Word.WORD_TO_LEARN, ">", null));

        // Load references to elements.
        buttonSetAsLearned = view.findViewById(R.id.buttonSetAsLearned);
        buttonSetAsLearned.setOnClickListener(this);

        buttonNextWord = view.findViewById(R.id.buttonLearningNext);
        buttonNextWord.setOnClickListener(this);
        buttonPreviousWord = view.findViewById(R.id.buttonLearningPrevious);
        buttonPreviousWord.setOnClickListener(this);

        dialogBuilder = new AlertDialog.Builder(view.getContext());
        unlearnDialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        word.setAsNotLearned();
                        Toast.makeText(getContext(), R.string.word_unlearned, Toast.LENGTH_SHORT).show();
                        displayWord();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        displayWord();
        // Add Navigation to Main Activity
//        view.findViewById(R.id.button_end_learning).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
    }

    /**
     * Update current word selector and display new word.
     */
    private void nextWord() {
        currentWordIndex += 1;
        if (currentWordIndex == words.size()) {
            currentWordIndex = 0;
        }
        displayWord();
    }

    private void previousWord() {
        currentWordIndex -= 1;
        if (currentWordIndex == -1) {
            currentWordIndex = words.size() -1;
        }
        displayWord();
    }

    /**
     * Render displaying word.
     */
    private void displayWord() {
        word = words.get(currentWordIndex);
        // Set current progress.
        TextView learningProgress = view.findViewById(R.id.textViewLearningLeft);
        learningProgress.setText(String.format("%s/%s", String.valueOf(currentWordIndex + 1), String.valueOf(words.size())));

        // Set English Word to Field
        TextView englishWord = view.findViewById(R.id.textEnglishWord);
        englishWord.setText(word.getName());

        // Set Correct name to "Set As Learned" Button
        if (word.getLearnState() == Word.WORD_TO_LEARN) {
            buttonSetAsLearned.setText(R.string.learn_set_learned);
            buttonSetAsLearned.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorWhite));
        } else {
            buttonSetAsLearned.setText(R.string.learn_set_unlearn);
            buttonSetAsLearned.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorRed));
        }

        // Load possible word meanings.
        LinearLayout linearLayoutWordMeaning = view.findViewById(R.id.linearLayoutWordMeaning);
        linearLayoutWordMeaning.removeAllViews();
        for (Word wordMeaning : word.getRelatedWordsOtherLanguage()) {
            TextView textViewWordMeaning = new TextView(getContext());
            textViewWordMeaning.setText(wordMeaning.getName());
            textViewWordMeaning.setPadding(5,5,5,5);
            textViewWordMeaning.setGravity(Gravity.CENTER);

            linearLayoutWordMeaning.addView(textViewWordMeaning);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == buttonSetAsLearned.getId()) {
            if (word.getLearnState() == Word.WORD_TO_LEARN) {
                // Set as learned, automatically load next word and display.
                word.setAsLearned();
                nextWord();
                Toast.makeText(view.getContext(), R.string.word_learned, Toast.LENGTH_SHORT).show();
                displayWord();
            } else {
                // ASK is user is sure to reset word.
                dialogBuilder.setMessage(getString(R.string.do_you_really_want_to_unlearn_word, word.getName())).
                        setPositiveButton(R.string.yes, unlearnDialogClickListener).
                        setNegativeButton(R.string.no_go_back, unlearnDialogClickListener).show();
            }
        }
        if (v.getId() == buttonNextWord.getId()) {
            nextWord();
        }
        if (v.getId() == buttonPreviousWord.getId()) {
            previousWord();
        }
    }
}
