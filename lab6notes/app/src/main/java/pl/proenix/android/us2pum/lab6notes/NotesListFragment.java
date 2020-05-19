package pl.proenix.android.us2pum.lab6notes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Fragment for displaying list of all notes.
 *
 */
public class NotesListFragment extends Fragment implements NoteSelectedInteface {

    private static final int PERMISSION_REQUEST_DOWNLOAD_FILE = 23;

    private List<Long> selectedNotes = new ArrayList<Long>();
    private List<Note> notes;
    private View view;
    private MenuItem menuItemShare;
    private MenuItem menuItemDelete;
    private MenuItem menuItemDownload;
    private RecyclerView recyclerView;
    private NotesListAdapter notesListAdapter;
    private DialogInterface.OnClickListener onDeleteDialogClickListener;

    private View popupView;

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

        // Floating action button.
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("mode", NoteCreateReadUpdateFragment.NoteEditMode.NOTE_NEW);
            NavHostFragment.findNavController(NotesListFragment.this).navigate(R.id.action_notesListFragment_to_noteCreateReadUpdateFragment, bundle);
        });

        // Inflate popup at start so it won't block threat while trying to display it.
        popupInflate();

        TextView textViewSortFilter = view.findViewById(R.id.textViewSortFilter);
        ImageView imageViewSortFilter = view.findViewById(R.id.imageViewSortFilter);

        View.OnClickListener sortClicked = v -> {
            if (v.getId() == textViewSortFilter.getId() || v.getId() == imageViewSortFilter.getId()) {
                openPopupSort();
            }
        };
        textViewSortFilter.setOnClickListener(sortClicked);
        imageViewSortFilter.setOnClickListener(sortClicked);

        // Create recycler.
        recyclerView = view.findViewById(R.id.recyclerViewNoteList);
        recreateRecyclerView();

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
        menuItemDownload = menu.findItem(R.id.menu_item_download);
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
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                ArrayList<Uri> uriList = new ArrayList<Uri>();
                for (long noteId : selectedNotes) {
                    Note note = Note.findById(noteId);
                    if (note != null) {
                        sb.append(note.getSharableContent()).append('\n');
                        // TODO: 16/05/2020 Convert to ACTION_SEND_MULTIPLE to attach files.
//                        for (NoteAttachment noteAtt : note.getNoteAttachments()) {
//                            File file = new File(noteAtt.getPathImageNormal());
//                            Uri uri = FileProvider.getUriForFile(view.getContext(), "pl.proenix.android.us2pum.lab6notes.fileprovider", file);
//                            uriList.add(uri);
//                        }
                    }
                }
//                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
                shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                startActivity(Intent.createChooser(shareIntent, getString(R.string.shared_note)));
                return true;
            case R.id.menu_item_delete:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
                dialogBuilder.setMessage(R.string.do_you_really_want_to_delete_notes).
                        setNegativeButton(R.string.no, onDeleteDialogClickListener).
                        setPositiveButton(R.string.yes, onDeleteDialogClickListener).show();
                return true;
            case R.id.menu_item_download:
                // Save notes to download
                downloadNotes();
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
        menuItemDownload.setVisible(selectedNotes.size() > 0);
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

    /**
     * Open Popup Sort/Filter.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void openPopupSort() {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        // Initialize popup content only once.
        if (popupView == null) {
            popupInflate();
        }
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // Close button for popup.
        ImageView imageViewClosePopup = popupView.findViewById(R.id.imageButtonClose);
        imageViewClosePopup.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0,0 );

        if (popupView != null) {
            popupView.setOnTouchListener((v, event) -> {
                recreateRecyclerView();
                popupWindow.dismiss();
                return true;
            });
        }
        // Fire refreshing recycler data and notify recyclerAdapter about changes.
        popupWindow.setOnDismissListener(this::recreateRecyclerView);
    }

    /**
     * Recreate recycler view in main thread.
     * Only notifying about data set changed did not refresh adapter.
     */
    private void recreateRecyclerView() {
        new Handler(Looper.getMainLooper()).post(() -> {
            notes = MainActivity.db.findAllNotes();
            notesListAdapter = new NotesListAdapter(notes, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.getAppContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(notesListAdapter);
            notesListAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Inflate popup.
     */
    private void popupInflate() {
        LayoutInflater inflater = getLayoutInflater();
        popupView = inflater.inflate(R.layout.popup_sort, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;

        ChipGroup chipGroupSortBy = popupView.findViewById(R.id.chipGroupSortBy);
        chipGroupSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.chipSortPriorityAsc:
                    MainActivity.db.setSortByPriority("ASC");
                    break;
                case R.id.chipSortPriorityDesc:
                    MainActivity.db.setSortByPriority("DESC");
                    break;
                case R.id.chipSortTitleAsc:
                    MainActivity.db.setSortByTitle("ASC");
                    break;
                case R.id.chipSortTitleDesc:
                    MainActivity.db.setSortByTitle("DESC");
                    break;
                case R.id.chipSortDueDateAsc:
                    MainActivity.db.setSortByDueDate("ASC");
                    break;
                case R.id.chipSortDueDateDesc:
                    MainActivity.db.setSortByDueDate("DESC");
                    break;
                default:
                    MainActivity.db.setSortByDueDate("DESC");
            }
        });
        // Set first default sort.
        ((Chip)popupView.findViewById(R.id.chipSortDueDateDesc)).setChecked(true);

        // Chips for filtering by categories
        ChipGroup chipGroupCategories = popupView.findViewById(R.id.chipGroupCategories);
        List<Map.Entry<Integer, Integer>> categoriesList = Note.getCategoriesColors();
        for (Map.Entry<Integer, Integer> cat : categoriesList) {
            Chip chip = new Chip(view.getContext());
            chip.setText(Note.getCategoryNameByInt(cat.getKey()));
            // Set color for chip
            chip.setChipBackgroundColor(ColorStateList.valueOf(cat.getValue()));
            chip.setTextColor(MainActivity.getAppContext().getColor(R.color.colorCategoryDefaultText));
            chip.setTag(R.id.TAG_CATEGORY_ID, cat.getKey());
            chip.setWidth(width);
            // Initial check state
            chip.setCheckable(true);
            chip.setChecked((MainActivity.db.getCategoryFilter().indexOf(cat.getKey()) != -1));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    MainActivity.db.addCategoryFilter((Integer) chip.getTag(R.id.TAG_CATEGORY_ID));
                } else {
                    MainActivity.db.removeCategoryFilter((Integer) chip.getTag(R.id.TAG_CATEGORY_ID));
                }
            });
            chipGroupCategories.addView(chip);
        }

        // Chips for filtering by priorities
        ChipGroup chipGroupPriorities = popupView.findViewById(R.id.chipGroupPriorities);
        List<Map.Entry<Integer, String>> priorities = Note.getPriorities();
        for (Map.Entry<Integer, String> priority : priorities) {
            Chip chip = new Chip(view.getContext());
            chip.setText(priority.getValue());
            chip.setTag(R.id.TAG_PRIORITY_ID, priority.getKey());
            // Initial check state
            chip.setCheckable(true);
            chip.setChecked((MainActivity.db.getPriorityFilter().indexOf(priority.getKey()) != -1));
            chip.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                if (isChecked) {
                    MainActivity.db.addPriorityFilter((Integer) chip.getTag(R.id.TAG_PRIORITY_ID));
                } else {
                    MainActivity.db.removePriorityFilter((Integer) chip.getTag(R.id.TAG_PRIORITY_ID));
                }
            }));
            chipGroupPriorities.addView(chip);
        }

        // Chips for filtering by status
        ChipGroup chipGroupStatus = popupView.findViewById(R.id.chipGroupStatus);
        // Status Done button
        ((Chip) chipGroupStatus.getChildAt(0)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                MainActivity.db.addStatusFilter(Note.STATUS_DONE);
            } else {
                MainActivity.db.removeStatusFilter(Note.STATUS_DONE);
            }
        });
        // Status In progress button
        ((Chip) chipGroupStatus.getChildAt(1)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                MainActivity.db.addStatusFilter(Note.STATUS_IN_PROGRESS);
            } else {
                MainActivity.db.removeStatusFilter(Note.STATUS_IN_PROGRESS);
            }
        });

    }

    /**
     * Process note download. Ask for permissions if not granted.
     */
    private void downloadNotes() {
        if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(view.getContext(), R.string.ask_manual_file_write_permission, Toast.LENGTH_LONG).show();
            } else {
                requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_DOWNLOAD_FILE);
                // The callback method gets the result of the request.
            }
        } else {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date()) + ".txt");
            FileOutputStream stream = null;
            StringBuilder sb2 = new StringBuilder();
            for (long noteId : selectedNotes) {
                Note note = Note.findById(noteId);
                if (note != null) {
                    sb2.append(note.getSharableContent()).append('\n');
                }
            }
            try {
                stream = new FileOutputStream(file);
                try {
                    stream.write(sb2.toString().getBytes());
                } finally {
                    stream.close();
                    Snackbar.make(view, R.string.note_saved_to_downloads, Snackbar.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_DOWNLOAD_FILE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadNotes();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
