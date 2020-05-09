package pl.proenix.android.us2pum.lab6lang;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TestSummaryFragment extends Fragment {

    Bundle bundle;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        bundle = getArguments();

        List<Test> testsDone = bundle.getParcelableArrayList("Z");

        for (Test test : testsDone) {
            Log.d("AndroidLang", String.valueOf(test.getResult()));
        }

        return inflater.inflate(R.layout.fragment_test_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add Navigation
    }
}
