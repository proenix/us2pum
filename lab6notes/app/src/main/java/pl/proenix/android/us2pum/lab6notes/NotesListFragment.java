package pl.proenix.android.us2pum.lab6notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

/**
 * Fragment for displaying list of all notes.
 * 
 * // TODO: 10/05/2020 Read notes from database.
 * // TODO: 10/05/2020 Attach notes to scrollview. 
 * // TODO: 10/05/2020 Create additional fragment for displaying note row. 
 * // TODO: 10/05/2020 Color notes based on category.
 * // TODO: 10/05/2020 Add float button to add new note. 
 * // TODO: 10/05/2020 Add navigation on click to each note to go to fragment_note_read.
 * 
 * // TODO: 10/05/2020 Add option to set note as done. 
 * // TODO: 10/05/2020 Add sorting options.
 */
public class NotesListFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(NotesListFragment.this)
                        .navigate(R.id.action_notesListFragment_to_noteCreateUpdateFragment);
            }
        });
    }
}
