package pl.proenix.android.us2pum.lab6notes;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Map;


public class NoteCreateUpdateFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private long noteID = -1L;
    private NoteEditMode mode;
    private EditText editTextNoteTitle;
    private EditText editTextNoteContent;

    enum NoteEditMode {
        NOTE_NEW,
        NOTE_UPDATE,
    }

    Note note;

    Spinner spinnerCategory;
    List<Map.Entry<Integer, Integer>> categoryItems;
    ScrollView scrollViewNote;

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
        View view = inflater.inflate(R.layout.fragment_note_create_update, container, false);
        return view;
    }


    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        note = new Note(4L, "Testowa notatka 5.", "Tresc testowej notatki ktora powinna miec jakas tam dlugosc zeby bylo widac co sie dzieje.", Note.CATEGORY_SCHOOL, Note.STATUS_IN_PROGRESS, Note.PRIORITY_DEFAULT, null);

        scrollViewNote = view.findViewById(R.id.scrollViewNote);
        scrollViewNote.setBackgroundColor(note.getBackgroundColor());

        // Color spinner for categories
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        categoryItems = Note.getCategoriesColors();
        ArrayAdapter<Map.Entry<Integer, Integer>> adapter = new ArrayAdapter<Map.Entry<Integer, Integer>>(view.getContext(), R.layout.spinner_item, categoryItems) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                ((TextView) view).setText(Note.getCategoryNameByInt(categoryItems.get(position).getKey()));
                ((TextView) view).setTextColor(ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryDefaultText));
                view.setBackgroundColor(categoryItems.get(position).getValue());
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        // Set Spinner BG as set for current note
        spinnerCategory.setBackgroundColor(note.getBackgroundColor());
        spinnerCategory.setOnItemSelectedListener(this);

        // Populate text fields with note data.
        editTextNoteTitle = view.findViewById(R.id.editTextNoteTitle);
        editTextNoteTitle.setText(note.getTitle());
        editTextNoteTitle.setTextColor(note.getTextColor());

        editTextNoteContent = view.findViewById(R.id.editTextNoteContent);
        editTextNoteContent.setText(note.getContent());
        editTextNoteContent.setTextColor(note.getTextColor());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        note.setCategory(categoryItems.get(position).getKey());

        ((TextView) view).setText(Note.getCategoryNameByInt(note.getCategoryAsInt()));
        ((TextView) view).setTextColor(note.getTextColor()); // Text color of spinner visible part
        view.setBackgroundColor(note.getBackgroundColor()); // Visible part of Spinner
        try {
            Drawable bg = getContext().getDrawable(R.drawable.layout_note_row_bg);
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
}
