package pl.proenix.android.us2pum.lab6lang;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Fragment for getting user preferences for Test action.
 * // TODO: 06/05/2020 Add Option to check until written OK. 
 * // TODO: 06/05/2020 Add Option to go to wait after correct answer. 
 */
public class TestOptionsFragment extends Fragment implements View.OnClickListener {

    private static final String DEBUG_TAG = "AndroidLang";

    private View view;
    private Button buttonTestToEnglish;
    private Button buttonTestToPolish;
    private Button buttonTestToBoth;
    private SeekBar seekBarTestElements;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_test_options, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Learned elements Count
        final int max = MainActivity.db.getWordsCountByLanguageAndLearnState(Word.WORD_LANGUAGE_ENGLISH, Word.WORD_TO_LEARN, ">");
        Log.d(DEBUG_TAG, "Total Testable Count: "+ String.valueOf(max));

        // Map controls
        final TextView textViewNumberOfElementsSummary = view.findViewById(R.id.textViewNumberOfElementsSummary);
        seekBarTestElements = view.findViewById(R.id.seekBarNumberOfElements);
        buttonTestToEnglish = view.findViewById(R.id.buttonTestToEnglish);
        buttonTestToPolish = view.findViewById(R.id.buttonTestToPolish);
        buttonTestToBoth = view.findViewById(R.id.buttonTestBoth);

        if (max == 0) {
            seekBarTestElements.setMax(0);
            seekBarTestElements.setEnabled(false);
            buttonTestToPolish.setEnabled(false);
            buttonTestToEnglish.setEnabled(false);
            buttonTestToBoth.setEnabled(false);

            Toast.makeText(view.getContext(), "Please learn some words first!", Toast.LENGTH_LONG).show();
            return;
        } else {
            seekBarTestElements.setMax(max-1); // set max to max elements counted from 0
        }

        if (max < 10) {
            textViewNumberOfElementsSummary.setText(String.format("%s/%s", String.valueOf(max), String.valueOf(max)));
            seekBarTestElements.setProgress(max-1); // set to max elements counted from 0
        } else {
            textViewNumberOfElementsSummary.setText(String.format("%s/%s", String.valueOf(10), String.valueOf(max)));
            seekBarTestElements.setMax(9); // set to 10 elements counted from 0
        }
        seekBarTestElements.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewNumberOfElementsSummary.setText(String.format("%s/%s", String.valueOf(progress + 1), String.valueOf(max)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Add Navigation
        buttonTestToEnglish.setOnClickListener(this);
        buttonTestToPolish.setOnClickListener(this);
        buttonTestToBoth.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putInt("numberOfTests", seekBarTestElements.getProgress() + 1);
        if (v.getId() == buttonTestToEnglish.getId()) {
            Log.d(DEBUG_TAG, "To English");
            bundle.putInt("mode", Test.TEST_MODE_TO_ENGLISH);
            NavHostFragment.findNavController(TestOptionsFragment.this)
                    .navigate(R.id.action_TestOptions_to_Test, bundle);
        }
        if (v.getId() == buttonTestToPolish.getId()) {
            Log.d(DEBUG_TAG, "To Polish");
            bundle.putInt("mode", Test.TEST_MODE_TO_POLISH);
            NavHostFragment.findNavController(TestOptionsFragment.this)
                    .navigate(R.id.action_TestOptions_to_Test, bundle);
        }
        if (v.getId() == buttonTestToBoth.getId()) {
            Log.d(DEBUG_TAG, "To Both");
            bundle.putInt("mode", Test.TEST_MODE_TO_BOTH);
            NavHostFragment.findNavController(TestOptionsFragment.this)
                    .navigate(R.id.action_TestOptions_to_Test, bundle);
        }
    }
}
