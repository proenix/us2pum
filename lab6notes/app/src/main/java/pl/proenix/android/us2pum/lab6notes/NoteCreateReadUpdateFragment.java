package pl.proenix.android.us2pum.lab6notes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Fragment for displaying single note read and edit mode.
 * // TODO: 12/05/2020 Add option to attach taken photo or video.
 * // TODO: 14/05/2020 Export to text file.
 * // TODO: 14/05/2020 Manage attachments CRUD operation.
 */
public class NoteCreateReadUpdateFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CAMERA = 349;
    private View view;

    private long noteID = -1L;
    private NoteEditMode mode;
    private EditText editTextNoteTitle;
    private EditText editTextNoteContent;
    private TextView textViewDueDate;
    private TextView textViewDueTime;
    private ScrollView scrollViewNote;
    private ImageButton imageButtonDueDelete;
    private Calendar dueDate = null;
    private Note note;
    private List<Map.Entry<Integer, Integer>> categoryItems;
    private List<Map.Entry<Integer, String>> priorityItems;
    private DialogInterface.OnClickListener onDeleteDialogClickListener;
    private Spinner spinnerPriority;
    private Spinner spinnerCategory;
    private NoteAttachment noteAttachment;

    enum NoteEditMode {
        NOTE_NEW,
        NOTE_UPDATE
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(view.getContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(view.getContext(), "Please grant Camera permission manually.", Toast.LENGTH_LONG).show();
            } else {
                requestPermissions( new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                // PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Already granted.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(MainActivity.getAppContext().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    noteAttachment = new NoteAttachment(noteID);
                    photoFile = noteAttachment.prepareFullSizeImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(view.getContext(),
                            "pl.proenix.android.us2pum.lab6notes.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            noteAttachment.generateThumbnail();
            noteAttachment.save();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public NoteCreateReadUpdateFragment() {}

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
        view = inflater.inflate(R.layout.fragment_note_create_read_update, container, false);
        setHasOptionsMenu(true);
        return view;
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // NOTE_NEW
        if (mode == NoteEditMode.NOTE_UPDATE) {
            note = Note.findById(noteID);
        } else {
            note = new Note();
        }
        scrollViewNote = view.findViewById(R.id.scrollViewNote);

        dispatchTakePictureIntent();


        // Note categories handling. Color spinner for categories
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        categoryItems = Note.getCategoriesColors();
        ArrayAdapter<Map.Entry<Integer, Integer>> adapter = new ArrayAdapter<Map.Entry<Integer, Integer>>(view.getContext(), R.layout.spinner_item, categoryItems) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.spinner_item, parent, false);
                TextView tv = (TextView) view;
                tv.setText(Note.getCategoryNameByInt(categoryItems.get(position).getKey()));
                tv.setTextColor(ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryDefaultText));
                view.setBackgroundColor(categoryItems.get(position).getValue());
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(this);
        // Set current category as selected.
        for (Map.Entry<Integer, Integer> item : categoryItems) {
            if (item.getKey().equals(note.getCategoryAsInt())) {
                spinnerCategory.setSelection(categoryItems.indexOf(item));
            }
        }

        // Note priority handling.
        spinnerPriority = view.findViewById(R.id.spinnerPriority);
        priorityItems = Note.getPriorities();
        ArrayAdapter<Map.Entry<Integer, String>> adapterPriority = new ArrayAdapter<Map.Entry<Integer, String>>(view.getContext(), R.layout.spinner_item, priorityItems) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.spinner_item, parent, false);
                TextView tv = (TextView) view;
                tv.setText(priorityItems.get(position).getValue());
                tv.setTextColor(note.getTextColor());
                tv.setBackgroundColor(note.getBackgroundColor());
                tv.setCompoundDrawablesWithIntrinsicBounds(note.getPriorityDrawableByPriorityInt(priorityItems.get(position).getKey(), true), null, null, null);
                return view;
            }
        };
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapterPriority);
        spinnerPriority.setOnItemSelectedListener(this);
        for (Map.Entry<Integer, String> item : priorityItems) {
            if (item.getKey().equals(note.getPriority())) {
                spinnerPriority.setSelection(priorityItems.indexOf(item));
            }
        }


        // Note title handling
        editTextNoteTitle = view.findViewById(R.id.editTextNoteTitle);
        editTextNoteTitle.setText(note.getTitle());
        editTextNoteTitle.setTextColor(note.getTextColor());
        final Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
        final Runnable[] workRunnable = {null, null};
        editTextNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(workRunnable[0]);
                workRunnable[0] = () -> setNoteTitle(s.toString());
                handler.postDelayed(workRunnable[0], 4000);
            }
        });

        // Note content handling
        editTextNoteContent = view.findViewById(R.id.editTextNoteContent);
        editTextNoteContent.setText(note.getContent());
        editTextNoteContent.setTextColor(note.getTextColor());
        editTextNoteContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(workRunnable[1]);
                workRunnable[1] = () -> setNoteContent(s.toString());
                handler.postDelayed(workRunnable[1], 4000);

            }
        });

        // Note due date handling
        textViewDueDate = view.findViewById(R.id.textViewDueDate);
        textViewDueTime = view.findViewById(R.id.textViewDueTime);
        textViewDueDate.setOnClickListener(this);
        textViewDueTime.setOnClickListener(this);
        // Initialize Calendar object for date time storing in Fragment.
        dueDate = note.getDueDateAsCalendar();
        imageButtonDueDelete = view.findViewById(R.id.imageButtonDueDelete);
        imageButtonDueDelete.setOnClickListener(this);
        loadDateTime();

        // Note done handling
        CheckBox checkBoxNoteDone = view.findViewById(R.id.checkBoxNoteDone);
        checkBoxNoteDone.setChecked(note.isDone());
        checkBoxNoteDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    note.setStatusDone();
                } else {
                    note.setStatusInProgress();
                }
                note.save();
            }
        });

        // Add on back pressed callback.
        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handler.removeCallbacks(workRunnable[0]);
                setNoteTitle(editTextNoteTitle.getText().toString());
                handler.removeCallbacks(workRunnable[1]);
                setNoteContent(editTextNoteContent.getText().toString());
                NavHostFragment.findNavController(NoteCreateReadUpdateFragment.this).popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);


        onDeleteDialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    note.delete();
                    NavHostFragment.findNavController(NoteCreateReadUpdateFragment.this).popBackStack();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        // Display back button in toolbar.
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {}

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_create_update_note, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setTypeAndNormalize("text/*");
                shareIntent.putExtra(Intent.EXTRA_TEXT, note.getSharableContent());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, note.getTitle());
                startActivity(Intent.createChooser(shareIntent, null));
                return true;
            case R.id.menu_item_delete:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
                dialogBuilder.setMessage(R.string.do_you_really_want_to_delete_note).
                        setNegativeButton(R.string.no, onDeleteDialogClickListener).
                        setPositiveButton(R.string.yes, onDeleteDialogClickListener).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setNoteContent(String content) {
        note.setContent(content);
        note.save();
        Toast.makeText(view.getContext(), R.string.note_saved, Toast.LENGTH_SHORT).show();
    }

    private void setNoteTitle(String title) {
        note.setTitle(title);
        note.save();
        Toast.makeText(view.getContext(), R.string.note_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerCategory:
                note.setCategory(categoryItems.get(position).getKey());
                ((TextView) view).setText(Note.getCategoryNameByInt(note.getCategoryAsInt()));
                ((TextView) view).setTextColor(note.getTextColor()); // Text color of spinner visible part
                try {
                    Drawable bg = view.getContext().getDrawable(R.drawable.layout_note_row_bg);
                    bg.setColorFilter(
                            new PorterDuffColorFilter(note.getBackgroundColor(), PorterDuff.Mode.SRC)
                    );
                    // Set color for note shape.
                    scrollViewNote.setBackground(bg);
                    editTextNoteContent.setTextColor(note.getTextColor());
                    editTextNoteTitle.setTextColor(note.getTextColor());
                } catch (NullPointerException ignored) { }
                break;
            case R.id.spinnerPriority:
                note.setPriority(priorityItems.get(position).getKey());
                ((TextView) view).setText(note.getPriorityName());
                ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(note.getPriorityDrawable(true), null, null, null);
                ((TextView) view).setTextColor(note.getTextColor()); // Text color of spinner visible part
        }
        note.save();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == textViewDueDate.getId()) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                    (view, year, month, dayOfMonth) -> {
                        dueDate.set(year, month, dayOfMonth, dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE));
                        note.setDueDate(dueDate);
                        note.save();
                        loadDateTime();
                    }, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
        if (v.getId() == textViewDueTime.getId()) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                    (view, hourOfDay, minute) -> {
                        dueDate.set(dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        note.setDueDate(dueDate);
                        note.save();
                        loadDateTime();
                    }, dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }
        if (v.getId() == imageButtonDueDelete.getId()) {
            note.removeDueDate();
            note.save();
            loadDateTime();
        }
    }

    /**
     * Load date and time after change.
     */
    private void loadDateTime() {
        if (note.hasDueDate()) {
            if (note.isAfterDue()) {
                textViewDueDate.setTextColor(note.getAfterDueColor());
                textViewDueTime.setTextColor(note.getAfterDueColor());
            } else {
                textViewDueDate.setTextColor(note.getTextColor());
                textViewDueTime.setTextColor(note.getTextColor());
            }
            textViewDueDate.setText(note.getFormattedDate());
            textViewDueTime.setText(note.getFormattedTime());
            imageButtonDueDelete.setVisibility(View.VISIBLE);
            imageButtonDueDelete.setEnabled(true);
        } else {
            textViewDueDate.setText("");
            textViewDueTime.setText("");
            textViewDueTime.setTextColor(note.getTextColor());
            imageButtonDueDelete.setVisibility(View.INVISIBLE);
            imageButtonDueDelete.setEnabled(false);
        }
    }


}
