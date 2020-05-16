package pl.proenix.android.us2pum.lab6notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Fragment for displaying list of all notes.
 *
 * // TODO: 10/05/2020 Add sorting options. By title ASC DESC, due date ASC DESC, priority ASC DESC, status ASC DESC
 * // TODO: 14/05/2020 Filter by status (done/not done), due date (from <-> to), having in content/title (string)
 * // TODO: 14/05/2020 Export to text file.
 * // TODO: 14/05/2020 Show attached elements. (as thumb previews)
 * // TODO: 13/05/2020 Add burger context menu for each row.
 */
public class NotesListFragment extends Fragment implements NoteSelectedInteface {

    private List<Long> selectedNotes = new ArrayList<Long>();
    private List<Note> notes;
    private View view;
    private MenuItem menuItemShare;
    private MenuItem menuItemDelete;
    private NotesListAdapter notesListAdapter;
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
            bundle.putSerializable("mode", NoteCreateReadUpdateFragment.NoteEditMode.NOTE_NEW);
            NavHostFragment.findNavController(NotesListFragment.this).navigate(R.id.action_notesListFragment_to_noteCreateReadUpdateFragment, bundle);
        });

        notes = MainActivity.db.findAllNotes();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewNoteList);
        notesListAdapter = new NotesListAdapter(notes, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.getAppContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notesListAdapter);

        // Delete notes dialog.
        onDeleteDialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    // Delete selected notes after confirmation.
                    for (long noteId : selectedNotes) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Stream<Note> noteStream = notes.stream().filter(n -> n.getID().equals(noteId));
                            noteStream.forEach(Note::delete);
                            notes.removeIf(n -> n.getID().equals(noteId));
                        } else {
                            // For old API.
                            Iterator<Note> noteIterator = notes.iterator();
                            while (noteIterator.hasNext()) {
                                Note n = noteIterator.next();
                                if (n.getID().equals(noteId)) {
                                    n.delete();
                                    noteIterator.remove();
                                }
                            }
                        }
                    }
                    selectedNotes.clear();
                    notesListAdapter.notifyDataSetChanged();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        // Do not display back button in toolbar.
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (NullPointerException ignored) {}

        // Clear list of selected notes
        selectedNotes.clear();
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
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shared_note));
                StringBuilder sb = new StringBuilder();
                for (long noteId : selectedNotes) {
                    Note note = Note.findById(noteId);
                    if (note != null) {
                        sb.append(note.getSharableContent()).append('\n');
                    }
                }
                shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                startActivity(Intent.createChooser(shareIntent, getString(R.string.shared_note)));
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
    }

    @Override
    public void removeFromSelectedNotes(Long id) {
        selectedNotes.remove(id);
        showHideMenuForSelection();
    }

    @Override
    public void addToSelectedNotes(Long id) {
        selectedNotes.add(id);
        showHideMenuForSelection();
    }

}
