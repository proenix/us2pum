package pl.proenix.android.us2pum.lab6notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class NoteCreateUpdateFragment extends Fragment {

    private long noteID = -1L;
    private NoteEditMode mode;

    enum NoteEditMode {
        NOTE_NEW,
        NOTE_UPDATE,
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            noteID = bundle.getLong("noteID");
            mode = (NoteEditMode) bundle.getSerializable("mode");
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_create_update, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
