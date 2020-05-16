package pl.proenix.android.us2pum.lab6notes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.PagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Fragment for displaying Notes attachment.
 */
public class NoteAttachmentFragment extends Fragment {

    private View view;
    private ViewGroup viewGroup;
    private Long noteID;
    private Long attachmentID;
    private int currentPosition;
    float x1,x2,y1,y2;

    List<NoteAttachment> noteAttachments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            noteID = bundle.getLong("noteID");
            attachmentID = bundle.getLong("attachmentID");
        }

        view = inflater.inflate(R.layout.fragment_note_attachment, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteAttachments = Note.findById(noteID).getNoteAttachments();
        for (NoteAttachment att : noteAttachments) {
            if (att.getID().equals(attachmentID)) {
                currentPosition = noteAttachments.indexOf(att);
            }
        }
        if (currentPosition == -1) {
            Log.d("AndroidNotes", "Olaboga.");

        }
        Log.d("AndroidNotes", "c" + currentPosition);

        ImageView imageViewSingleAttachment = view.findViewById(R.id.imageViewSingleAttachment);
        imageViewSingleAttachment.setImageBitmap(BitmapFactory.decodeFile(noteAttachments.get(currentPosition).getPathImageNormal()));
        imageViewSingleAttachment.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();
                    y2 = event.getY();
                    if(x1< x2) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("noteID", noteID);
                        bundle.putLong("attachmentID", getPreviousAttachmentId());
                        NavHostFragment.findNavController(this).navigate(R.id.action_noteAttachmentFragment_self, bundle);
                       Log.d("AndroidNotes", "To left");
                    } else if (x1 > x2) {
                        Bundle bundle = new Bundle();
                        bundle.putLong("noteID", noteID);
                        bundle.putLong("attachmentID", getNextAttachmentId());
                        NavHostFragment.findNavController(this).navigate(R.id.action_noteAttachmentFragment_self, bundle);
                        Log.d("AndroidNotes", "To right");
                    }
                    break;
            }
            return true;
        });
    }

    Long getNextAttachmentId() {
        if ((noteAttachments.size() - 1) == currentPosition) {
            return noteAttachments.get(0).getID();
        }
        return noteAttachments.get(currentPosition + 1).getID();
    }

    Long getPreviousAttachmentId() {
        if (currentPosition == 0) {
            return noteAttachments.get(noteAttachments.size()-1).getID();
        }
        return noteAttachments.get(currentPosition - 1).getID();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add_delete_attachment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.menu_take_photo:
                bundle.putLong("noteID", noteID);
                bundle.putSerializable("mode", NoteCreateReadUpdateFragment.NoteEditMode.TAKE_PHOTO);
                NavHostFragment.findNavController(this).saveState();
                NavHostFragment.findNavController(this).navigate(R.id.action_noteAttachmentFragment_to_noteCreateReadUpdateFragment, bundle);
                return true;
            case R.id.menu_delete_attachment:
                noteAttachments.get(currentPosition).delete();
                noteAttachments.remove(currentPosition);
                if (noteAttachments.size() != 0) {
                    Log.d("AndroidNotes", "Moar exist");
                    bundle.putLong("noteID", noteID);
                    bundle.putLong("attachmentID", getPreviousAttachmentId());
                    NavHostFragment.findNavController(this).navigate(R.id.action_noteAttachmentFragment_self, bundle);
                    return true;
                } else {
                    Log.d("AndroidNotes", "Sayonara");
                    NavHostFragment.findNavController(this).popBackStack();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
