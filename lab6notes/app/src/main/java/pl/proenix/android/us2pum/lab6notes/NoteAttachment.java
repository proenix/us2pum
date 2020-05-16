package pl.proenix.android.us2pum.lab6notes;

import androidx.annotation.NonNull;

import java.util.Calendar;

/**
 * NoteAttachment object representation.
 */
public class NoteAttachment {
    private Long _id;
    private Long _note_id;
    private String _path_image_normal;
    private String _path_image_thumb;

    public void setPathImageNormal(String path) {
        this._path_image_normal = path;
    }

    public void setPathImageThumbnail(String path) {
        this._path_image_thumb = path;
    }

    public void setNoteId(Long id) {
        this._note_id = id;
    }

    public void setID(Long id) {
        this._id = id;
    }

    public long getNoteId() {
        return this._note_id;
    }

    public String getPathImageNormal() {
        return this._path_image_normal;
    }

    public String getPathImageThumbnail() {
        return this._path_image_thumb;
    }

    NoteAttachment() { }

    /**
     * Create NoteAttachment object. (Use only from DatabaseHandler)
     * @param id ID of NoteAttachment
     * @param noteId Related Note ID
     * @param pathImgNormal Path to Image in full size.
     * @param pathImgThumb Path to Image as thumbnail.
     */
    NoteAttachment(Long id, Long noteId, String pathImgNormal, String pathImgThumb) {
        this._id = id;
        this._note_id = noteId;
        this._path_image_normal = pathImgNormal;
        this._path_image_thumb = pathImgThumb;
    }

    /**
     * Create NoteAttachment object.
     * @param noteId Related Note ID
     * @param pathImgNormal Path to Image in full size.
     * @param pathImgThumb Path to Image as thumbnail.
     */
    NoteAttachment(Long noteId, String pathImgNormal, String pathImgThumb) {
        this._note_id = noteId;
        this._path_image_normal = pathImgNormal;
        this._path_image_thumb = pathImgThumb;
    }

    @NonNull
    @Override
    public String toString() {
        return "NoteAttachment: {id: " + String.valueOf(_id)
                + "; note_id: " + String.valueOf(_note_id)
                + "; pathImgNorm: " + _path_image_normal
                + "; pathImgThumb: " + _path_image_thumb + "}";
    }

    /**
     * Save Attachment to database.
     */
    void save() {
        if (this._id == null) {
            MainActivity.db.addAttachment(this);
        }
        // Log.d("AndroidNotes Saved. ", this.toString());
    }

    /**
     * Current time representation as Long.
     * @return Long current time representation in second since epoch.
     */
    public Long getCurrentDateTime() {
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

}
