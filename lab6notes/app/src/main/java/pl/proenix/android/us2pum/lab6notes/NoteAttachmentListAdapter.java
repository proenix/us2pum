package pl.proenix.android.us2pum.lab6notes;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class NoteAttachmentListAdapter extends RecyclerView.Adapter<NoteAttachmentListAdapter.NoteAttachmentViewHolder> {

    private List<NoteAttachment> noteAttachmentList;
     private NoteAttachmentInterface noteAttachmentSelectedInterface;

    @NonNull
    @Override
    public NoteAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note_attachment_single_image, parent, false);
        return new NoteAttachmentViewHolder(itemView);
    }

    NoteAttachmentListAdapter(List<NoteAttachment> noteAttachments, NoteAttachmentInterface noteCreateReadUpdateFragment) {
        this.noteAttachmentList = noteAttachments;
        this.noteAttachmentSelectedInterface = noteCreateReadUpdateFragment;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAttachmentViewHolder holder, int position) {
        if (position == 0) {
            Log.d("AndroidNotes", "Displaying Button");
            holder.imageView.setVisibility(View.GONE);
            holder.imageButtonAddPhoto.setVisibility(View.VISIBLE);
            holder.imageButtonAddPhoto.setOnClickListener(v -> noteAttachmentSelectedInterface.dispatchPhoto());
            return;
        }
        holder.imageView.setVisibility(View.VISIBLE);
        holder.imageButtonAddPhoto.setVisibility(View.GONE);
        NoteAttachment noteAttachment = noteAttachmentList.get(position - 1);
        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(noteAttachment.getPathImageThumbnail()));
        holder.imageView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("noteID", noteAttachment.getNoteId());
            bundle.putLong("attachmentID", noteAttachment.getID());
            Log.d("AndroidNotes", "Flying to view!");
            Navigation.findNavController(v).saveState();
            Navigation.findNavController(v).navigate(R.id.action_noteCreateReadUpdateFragment_to_noteAttachmentFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return noteAttachmentList.size() + 1;
    }

    class NoteAttachmentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton imageButtonAddPhoto;
        NoteAttachmentViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            imageButtonAddPhoto = view.findViewById(R.id.imageButtonAddPhoto);
        }

    }
}
