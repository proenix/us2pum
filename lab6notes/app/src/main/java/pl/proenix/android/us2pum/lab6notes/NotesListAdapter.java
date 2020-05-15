package pl.proenix.android.us2pum.lab6notes;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying notes in list.
 */
public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteViewHolder> {
    private List<Note> noteList;
    private NoteSelectedInteface noteSelectedInteface;

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note_row, parent, false);
        return new NoteViewHolder(itemView);
    }

    /**
     * Populate holder with data.
     * @param holder Holder
     * @param position Adapter position.
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.done.setChecked(note.isDone());
        holder.noteTitle.setText(note.getTitleShort());
        holder.noteTitle.setTextColor(note.getTextColor());
        holder.noteContent.setText(note.getContentShort());
        holder.noteContent.setTextColor(note.getTextColor());
        holder.notePriority.setImageDrawable(note.getPriorityDrawable());
        // Set due field visibility, value and color.
        if (note.hasDueDate()) {
            holder.noteDueDate.setText(String.format("%s %s", note.getFormattedDate(), note.getFormattedTime()));
            if (note.isAfterDue()) {
                holder.noteDueDate.setTextColor(note.getAfterDueColor());
            } else {
                holder.noteDueDate.setTextColor(note.getTextColor());
            }
        } else {
            holder.noteDueDate.setVisibility(View.GONE);
        }
        // Color background for note.
        try {
            holder.noteBackground.setBackground(getBackground(note.getBackgroundColor(), note.getBackgroundColor()));
        } catch (NullPointerException ignored) { }
        // Open note on click
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("mode", NoteCreateReadUpdateFragment.NoteEditMode.NOTE_UPDATE);
            bundle.putLong("noteID", noteList.get(holder.getAdapterPosition()).getID());
            Navigation.findNavController(v).navigate(R.id.action_notesListFragment_to_noteCreateReadUpdateFragment, bundle);
        });
        // On long click select note.
        holder.itemView.setOnLongClickListener(v -> {
            if (v.isSelected()) {
                noteSelectedInteface.removeFromSelectedNotes(noteList.get(holder.getAdapterPosition()).getID());
                v.setSelected(false);
                v.findViewById(R.id.noteElement).setBackground(getBackground(note.getBackgroundColor(), note.getBackgroundColor()));
            } else {
                noteSelectedInteface.addToSelectedNotes(noteList.get(holder.getAdapterPosition()).getID());
                v.setSelected(true);
                v.findViewById(R.id.noteElement).setBackground(getBackground(note.getBackgroundColor(), note.getTextColor()));
            }
            return true;
        });
        // On checkbox change edit state and save note.
        holder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Note note1 = noteList.get(holder.getAdapterPosition());
            if (isChecked) {
                note1.setStatusDone();
            } else {
                note1.setStatusInProgress();
            }
            note1.save();
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent, noteDueDate;
        ImageView notePriority;
        View noteBackground;
        CheckBox done;
        NoteViewHolder(View view) {
            super(view);
            done = view.findViewById(R.id.checkBoxListNoteDone);
            done.setChecked(true);
            // Set Unique Id for checkbox without this setting checked state pointed to all previous buttons with that id.
            done.setId(View.generateViewId());
            noteTitle = view.findViewById(R.id.textViewNoteTitle);
            noteContent = view.findViewById(R.id.textViewNoteContent);
            noteDueDate = view.findViewById(R.id.textViewNoteDueDate);
            noteBackground = view.findViewById(R.id.noteElement);
            notePriority = view.findViewById(R.id.imageViewNotePriority);
        }
    }

    public NotesListAdapter(List<Note> noteList, NoteSelectedInteface noteSelectedInteface) {
        this.noteList = noteList;
        this.noteSelectedInteface = noteSelectedInteface;
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
}
