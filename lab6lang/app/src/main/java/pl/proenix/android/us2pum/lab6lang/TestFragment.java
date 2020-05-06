package pl.proenix.android.us2pum.lab6lang;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Fragment for Test action.
 */
public class TestFragment extends Fragment {

    public static final int TEST_MODE_TO_ENGLISH = 10;
    public static final int TEST_MODE_TO_POLISH = 11;
    public static final int TEST_MODE_TO_BOTH = 12;

    Bundle bundle;
    private View view;
    private int mode;
    private int numberOfTests;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        bundle = getArguments();
        try {
            mode = bundle != null ? bundle.getInt("mode") : TEST_MODE_TO_BOTH;
            numberOfTests = bundle != null ? bundle.getInt("numberOfTests") : 0;
            Log.d("AndroidLang", "mode: "+String.valueOf(mode));
            Log.d("AndroidLang", "numberOfTests: "+String.valueOf(numberOfTests));
        } catch (Exception e) {
            e.printStackTrace();
        }

        view = inflater.inflate(R.layout.fragment_test, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add Navigation
    }
}
