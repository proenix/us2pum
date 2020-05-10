package pl.proenix.android.us2pum.lab6notes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Fragment for displaying single note for read only.
 * 
 * // TODO: 10/05/2020 Add button to go to edit. 
 */
public class NoteReadFragment extends Fragment {

    private long noteID = -1L;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            noteID = bundle.getLong("noteID");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_read, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add navigation
    }
}
