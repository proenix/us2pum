package pl.proenix.android.us2pum.lab6lang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class ChooseModeFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_mode, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add navigation to Learning Mode
        view.findViewById(R.id.buttonToLearningMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ChooseModeFragment.this)
                        .navigate(R.id.action_MainActivity_to_LearningMode);
            }
        });

        // Add navigation to Test Mode
        view.findViewById(R.id.buttonToTestMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ChooseModeFragment.this)
                        .navigate(R.id.action_MainActivity_to_TestOptions);
            }
        });


    }
}
