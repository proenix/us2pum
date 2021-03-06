package pl.proenix.android.us2pum.lab6lang;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class TestSummaryFragment extends Fragment {

    private List<Test> testsDone;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            testsDone = bundle.getParcelableArrayList("TestSummary");
        }
        return inflater.inflate(R.layout.fragment_test_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add callback for return button pressed.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                NavHostFragment.findNavController(TestSummaryFragment.this).navigate(R.id.action_TestSummary_to_MainActivity);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        LinearLayout linearLayoutTestSummary = view.findViewById(R.id.linearLayoutTestSummary);
        linearLayoutTestSummary.removeAllViews();

        int totalCorrect = 0;
        int totalTests = testsDone.size();
        for (Test test : testsDone) {
            View row = View.inflate(view.getContext(), R.layout.fragment_summary_row, null);
            TextView testedWord = row.findViewById(R.id.textViewSummaryRowTest);

            TextView testProgress = row.findViewById(R.id.textViewSummaryRowProgress);
            testProgress.setText(getString(R.string.summary_row_progress, test.getTries(), test.getWord().getLearnState()));

            ImageView mode = row.findViewById(R.id.imageViewSummaryRowMode);
            ImageView result = row.findViewById(R.id.imageViewSummaryRowResult);

            if (test.isAdvanced()) {
                result.setImageResource(R.drawable.up);
                totalCorrect += 1;
            } else {
                result.setImageResource(R.drawable.down);
            }

            if (test.getMode() == Test.TEST_MODE_TO_ENGLISH) {
                testedWord.setText(test.getWord().getName());
                mode.setImageResource(R.drawable.pl_to_en);
            } else {
                testedWord.setText(test.getWordToShow());
                mode.setImageResource(R.drawable.en_to_pl);
            }
            linearLayoutTestSummary.addView(row);
        }

        TextView textViewTestSummaryTotal = view.findViewById(R.id.textViewTestSummaryTotal);
        textViewTestSummaryTotal.setText(getString(R.string.correct_answers, totalCorrect, totalTests));
    }
}
