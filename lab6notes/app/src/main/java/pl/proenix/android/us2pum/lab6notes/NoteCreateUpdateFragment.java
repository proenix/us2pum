package pl.proenix.android.us2pum.lab6notes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


// TODO: 11/05/2020 Append on exit behaviour to save note.
public class NoteCreateUpdateFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private View view;

    private long noteID = -1L;
    private NoteEditMode mode;
    private EditText editTextNoteTitle;
    private EditText editTextNoteContent;
    private TextView textViewDueDate;
    private TextView textViewDueTime;
    private ScrollView scrollViewNote;

    private Calendar dueDate = null;

    enum NoteEditMode {
        NOTE_NEW,
        NOTE_UPDATE,

        }
    Note note;

    List<Map.Entry<Integer, Integer>> categoryItems;

    public NoteCreateUpdateFragment() {}

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
        view = inflater.inflate(R.layout.fragment_note_create_update, container, false);
        return view;
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // NOTE_NEW
        if (mode == NoteEditMode.NOTE_UPDATE) {
            note = Note.findById(noteID);
        } else {
            note = new Note();
        }
        scrollViewNote = view.findViewById(R.id.scrollViewNote);
        scrollViewNote.setBackgroundColor(note.getBackgroundColor());

        // Note categories handling. Color spinner for categories
        Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        categoryItems = Note.getCategoriesColors();
        ArrayAdapter<Map.Entry<Integer, Integer>> adapter = new ArrayAdapter<Map.Entry<Integer, Integer>>(view.getContext(), R.layout.spinner_item, categoryItems) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setText(Note.getCategoryNameByInt(categoryItems.get(position).getKey()));
                tv.setTextColor(ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryDefaultText));
                view.setBackgroundColor(categoryItems.get(position).getValue());
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setBackgroundColor(note.getBackgroundColor());
        spinnerCategory.setOnItemSelectedListener(this);

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
        dueDate = Calendar.getInstance();
        if (note.hasDueDate()) {
            dueDate.setTimeInMillis(note.getDueDateAsLong()*1000);
        }
        loadDateTime(dueDate);
        // TODO: 11/05/2020 Add way to clear due date.

        // Note done handling
        CheckBox checkBoxNoteDone = view.findViewById(R.id.checkBoxNoteDone);
        checkBoxNoteDone.setChecked(note.isDone());
        checkBoxNoteDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    note.setStatusDone();
                    note.save();
                } else {
                    note.setStatusInProgress();
                    note.save();
                }
            }
        });
    }

    private void setNoteContent(String content) {
        note.setContent(content);
        note.save();
        Toast.makeText(view.getContext(), "Note saved.", Toast.LENGTH_SHORT).show();
    }

    private void setNoteTitle(String title) {
        note.setTitle(title);
        note.save();
        Toast.makeText(view.getContext(), "Note saved.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        note.setCategory(categoryItems.get(position).getKey());
        note.save();

        ((TextView) view).setText(Note.getCategoryNameByInt(note.getCategoryAsInt()));
        ((TextView) view).setTextColor(note.getTextColor()); // Text color of spinner visible part
        view.setBackgroundColor(note.getBackgroundColor()); // Visible part of Spinner
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
                        loadDateTime(dueDate);
                    }, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
        if (v.getId() == textViewDueTime.getId()) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                    (view, hourOfDay, minute) -> {
                        dueDate.set(dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                        note.setDueDate(dueDate);
                        note.save();
                        loadDateTime(dueDate);
                    }, dueDate.get(Calendar.HOUR_OF_DAY), dueDate.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }
    }

    /**
     * Load date and time after change.
     * @param cal Calendar note due date time.
     */
    private void loadDateTime(Calendar cal) {
        if (note.hasDueDate()) {
            textViewDueDate.setTextColor(note.getTextColor());
            textViewDueTime.setTextColor(note.getTextColor());
            textViewDueDate.setText(formatDate(cal));
            textViewDueTime.setText(formatTime(cal));

        } else {
            textViewDueDate.setText("set date");
            textViewDueTime.setText("set time");
        }
    }

    /**
     * Format date in standardized way.
     * @param cal Calendar object with datetime set.
     * @return String Formatted date string.
     */
    private String formatDate(Calendar cal) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("E, dd-MMM-YYYY");
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(cal.getTime());
        } else {
            return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * Format time in standardized way.
     * @param cal Calendar object with datetime set.
     * @return String Formatted time string.
     */
    private String formatTime(Calendar cal) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("HH:mm");
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(cal.getTime());
        } else {
            return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
        }
    }
}
