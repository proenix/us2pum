package pl.proenix.android.us2pum.lab6notes;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Fragment for displaying list of all notes.
 *
 * // TODO: 10/05/2020 Add sorting options.
 * // TODO: 12/05/2020 Option to select and delete note. SnackBar maybe?
 * // TODO: 12/05/2020 Option to share selected note via SMS/EMAIL.
 * // TODO: 12/05/2020 Add content preview. Limited to ~100 chars.
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

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("mode", NoteCreateUpdateFragment.NoteEditMode.NOTE_NEW);
            NavHostFragment.findNavController(NotesListFragment.this).navigate(R.id.action_notesListFragment_to_noteCreateUpdateFragment, bundle);
        });

        LinearLayout linearLayoutNotesList = view.findViewById(R.id.linearLayoutNotesList);
        List<Note> notes = MainActivity.db.findAllNotes();

        for (Note note : notes) {
            View singleNoteRow = View.inflate(view.getContext(), R.layout.fragment_note_row, null);

            // Populate checkbox with state
            CheckBox checkBoxNoteDone = singleNoteRow.findViewById(R.id.checkBoxListNoteDone);
            // Set Unique Id for checkbox without this setting checked state pointed to all previous buttons with that id.
            checkBoxNoteDone.setId(View.generateViewId());
            checkBoxNoteDone.setTag(R.id.TAG_NOTE_ID, note.getID());
            checkBoxNoteDone.setChecked(note.isDone());
            checkBoxNoteDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Note note1 = Note.findById((long) buttonView.getTag(R.id.TAG_NOTE_ID));
                if (isChecked) {
                    note1.setStatusDone();
                } else {
                    note1.setStatusInProgress();
                }
                note1.save();
            });

            // Populate title with data
            TextView textViewNoteTitle = singleNoteRow.findViewById(R.id.textViewNoteTitle);
            textViewNoteTitle.setText(note.getTitle());
            // Populate due date.
            TextView textViewNoteDueDate = singleNoteRow.findViewById(R.id.textViewNoteDueDate);
            textViewNoteTitle.setTextColor(note.getTextColor());
            if (note.hasDueDate()) {
                textViewNoteDueDate.setText(String.format("%s %s", note.getFormattedDate(), note.getFormattedTime()));
                if (note.isAfterDue()) {
                    textViewNoteDueDate.setTextColor(note.getAfterDueColor());
                } else {
                    textViewNoteDueDate.setTextColor(note.getTextColor());
                }
            } else {
                textViewNoteDueDate.setVisibility(View.GONE);
            }

            // Get background shape and color it depending on category
            try {
                Drawable bg = view.getContext().getDrawable(R.drawable.layout_note_row_bg);
                bg.setColorFilter(
                        new PorterDuffColorFilter(note.getBackgroundColor(), PorterDuff.Mode.SRC)
                );
                singleNoteRow.findViewById(R.id.noteElement).setBackground(bg);
            } catch (NullPointerException ignored) { }

            singleNoteRow.setTag(R.id.TAG_NOTE_ID, note.getID());
            singleNoteRow.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("mode", NoteCreateUpdateFragment.NoteEditMode.NOTE_UPDATE);
                bundle.putLong("noteID", (Long) v.getTag(R.id.TAG_NOTE_ID));
                NavHostFragment.findNavController(NotesListFragment.this).navigate(R.id.action_notesListFragment_to_noteCreateUpdateFragment, bundle);
            });
            linearLayoutNotesList.addView(singleNoteRow);
        }
    }
}
