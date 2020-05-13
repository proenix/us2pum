package pl.proenix.android.us2pum.lab6notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying list of all notes.
 *
 * // TODO: 10/05/2020 Add sorting options. v2
 * // TODO: 13/05/2020 Add burger context menu for each row.
 */
public class NotesListFragment extends Fragment {

    private List<Long> selectedNotes = new ArrayList<Long>();
    private View view;
    private MenuItem menuItemShare;
    private MenuItem menuItemDelete;

    private DialogInterface.OnClickListener onDeleteDialogClickListener;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_notes_list, container, false);
        setHasOptionsMenu(true);
        return view;
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

        // Delete notes dialog.
        onDeleteDialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    // Delete selected notes after confirmation.
                    for (long noteId : selectedNotes) {
                        Note note = Note.findById(noteId);
                        if (note != null) {
                            note.delete();
                        }
                    }
                    selectedNotes.clear();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

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
            textViewNoteTitle.setTextColor(note.getTextColor());
            textViewNoteTitle.setText(note.getTitleShort());

            // Populate note with content limited to
            TextView textViewNoteContent = singleNoteRow.findViewById(R.id.textViewNoteContent);
            textViewNoteContent.setTextColor(note.getTextColor());
            textViewNoteContent.setText(note.getContentShort());

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
                singleNoteRow.findViewById(R.id.noteElement).setBackground(getBackground(note.getBackgroundColor(), note.getBackgroundColor()));
            } catch (NullPointerException ignored) { }

            singleNoteRow.setTag(R.id.TAG_NOTE_ID, note.getID());
            singleNoteRow.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("mode", NoteCreateUpdateFragment.NoteEditMode.NOTE_UPDATE);
                bundle.putLong("noteID", (Long) v.getTag(R.id.TAG_NOTE_ID));
                NavHostFragment.findNavController(NotesListFragment.this).navigate(R.id.action_notesListFragment_to_noteCreateUpdateFragment, bundle);
            });
            singleNoteRow.setOnLongClickListener((View.OnLongClickListener) v -> {
                if (v.isSelected()) {
                    selectedNotes.remove((Long) v.getTag(R.id.TAG_NOTE_ID));
                    v.setSelected(false);
                    v.findViewById(R.id.noteElement).setBackground(getBackground(note.getBackgroundColor(), note.getBackgroundColor()));
                } else {
                    selectedNotes.add( (Long) v.getTag(R.id.TAG_NOTE_ID));
                    v.setSelected(true);
                    v.findViewById(R.id.noteElement).setBackground(getBackground(note.getBackgroundColor(), note.getTextColor()));
                }
                showHideMenuForSelection();
                return true;
            });
            linearLayoutNotesList.addView(singleNoteRow);
        }

        // Do not display back button in toolbar.
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (NullPointerException ignored) {}

        // Clear list of selected notes
        selectedNotes.clear();
    }

    /**
     * Get background for note row based on provided colors.
     * @param colorBackground Background color.
     * @param colorStroke Stroke color - accent for selected items.
     * @return Drawable for use as background.
     */
    private Drawable getBackground(int colorBackground, int colorStroke) {
        LayerDrawable bg = (LayerDrawable) MainActivity.getAppContext().getDrawable(R.drawable.layout_note_row_bg_full);
        Drawable background = bg.getDrawable(0);
        Drawable stroke = bg.getDrawable(1);
        GradientDrawable backgroundGD = (GradientDrawable) background;
        GradientDrawable strokeGD = (GradientDrawable) stroke;
        backgroundGD.setColor(colorBackground);
        strokeGD.setStroke(4, colorStroke);
        bg.setDrawableByLayerId(0, backgroundGD);
        bg.setDrawableByLayerId(1, strokeGD);
        return bg;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_update_note, menu);
        menuItemShare = menu.findItem(R.id.menu_item_share);
        menuItemDelete = menu.findItem(R.id.menu_item_delete);
        showHideMenuForSelection();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setTypeAndNormalize("text/*");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared notes");
                StringBuilder sb = new StringBuilder();
                for (long noteId : selectedNotes) {
                    Note note = Note.findById(noteId);
                    if (note != null) {
                        sb.append(note.getSharableContent()).append('\n');
                    }
                }
                shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                startActivity(Intent.createChooser(shareIntent, "Share notes"));
                return true;
            case R.id.menu_item_delete:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
                dialogBuilder.setMessage(R.string.do_you_really_want_to_delete_notes).
                        setNegativeButton(R.string.no, onDeleteDialogClickListener).
                        setPositiveButton(R.string.yes, onDeleteDialogClickListener).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show/Hide menu if some items are selected.
     */
    private void showHideMenuForSelection() {
        menuItemDelete.setVisible(selectedNotes.size() > 0);
        menuItemShare.setVisible(selectedNotes.size() > 0);
    };
}
