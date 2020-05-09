package pl.proenix.android.us2pum.lab6lang;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TestSummaryFragment extends Fragment {

    Bundle bundle;
    private AlertDialog.Builder dialogBuilder;
    private DialogInterface.OnClickListener onExitDialogClickListener;
    private View view;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        bundle = getArguments();

        List<Test> testsDone = bundle.getParcelableArrayList("TestSummary");

        for (Test test : testsDone) {
            Log.d("AndroidLang", String.valueOf(test.getResult()));
        }
        Log.d("AndroidLang", "Test Results Size: " + String.valueOf(testsDone.size()));

        view = inflater.inflate(R.layout.fragment_test_summary, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add callback for return button pressed.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                NavHostFragment.findNavController(TestSummaryFragment.this).popBackStack(R.id.MainActivity, true);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


    }
}
