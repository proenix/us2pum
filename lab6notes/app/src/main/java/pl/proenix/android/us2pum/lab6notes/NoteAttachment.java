package pl.proenix.android.us2pum.lab6notes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    NoteAttachment() {
    }

    NoteAttachment(Long noteId) {
        this._note_id = noteId;
    }

    /**
     * Create NoteAttachment object. (Use only from DatabaseHandler)
     *
     * @param id            ID of NoteAttachment
     * @param noteId        Related Note ID
     * @param pathImgNormal Path to Image in full size.
     * @param pathImgThumb  Path to Image as thumbnail.
     */
    NoteAttachment(Long id, Long noteId, String pathImgNormal, String pathImgThumb) {
        this._id = id;
        this._note_id = noteId;
        this._path_image_normal = pathImgNormal;
        this._path_image_thumb = pathImgThumb;
    }

    /**
     * Create NoteAttachment object.
     *
     * @param noteId        Related Note ID
     * @param pathImgNormal Path to Image in full size.
     * @param pathImgThumb  Path to Image as thumbnail.
     */
    NoteAttachment(Long noteId, String pathImgNormal, String pathImgThumb) {
        this._note_id = noteId;
        this._path_image_normal = pathImgNormal;
        this._path_image_thumb = pathImgThumb;
    }

    @NonNull
    @Override
    public String toString() {
        return "NoteAttachment: {id: " + _id
                + "; note_id: " + _note_id
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
        galleryAddPic();
        // Log.d("AndroidNotes Saved. ", this.toString());
    }

    /**
     * Current time representation as Long.
     *
     * @return Long current time representation in second since epoch.
     */
    public Long getCurrentDateTime() {
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

    /**
     * Generate thumbnail.
     */
    public void generateThumbnail() {
        File photoThumbFile = null;
        try {
            photoThumbFile = createImageFile(false);
            File photoFile = new File(this._path_image_normal);
            InputStream input = new FileInputStream(photoFile);
            BitmapFactory.Options oBO = new BitmapFactory.Options();
            oBO.inJustDecodeBounds = true;
            oBO.inDither = true;
            oBO.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeStream(input, null, oBO);
            input.close();

            if ((oBO.outWidth == -1) || oBO.outHeight == -1) {
                return;
            }

            double ratio = (oBO.outHeight > 400.0) ? (oBO.outHeight / 400.0) : 1.0;

            BitmapFactory.Options bO = new BitmapFactory.Options();
            bO.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
            bO.inDither = true;
            bO.inPreferredConfig = Bitmap.Config.ARGB_8888;
            input = new FileInputStream(photoFile);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bO);
            input.close();

            try (FileOutputStream out = new FileOutputStream(photoThumbFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
            //Log.d("AndroidNotes", "Bitmape shrinked: " + bitmap.getHeight() + " " + bitmap.getWidth());
            this._path_image_thumb = photoThumbFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Get max round integer of 2^.
     *
     * @param ratio Ratio to check.
     * @return Integer ratio.
     * @link https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri/4717740#4717740
     */
    private int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    /**
     * Create file name for saving data to it.
     *
     * @param fullSize If name generated is for full size pic
     * @return File object
     * @throws IOException
     */
    private File createImageFile(boolean fullSize) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = MainActivity.getAppContext().getExternalFilesDir((fullSize) ? Environment.DIRECTORY_PICTURES : "Thumbnails");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        if (fullSize) {
            this._path_image_normal = image.getAbsolutePath();
        }
        return image;
    }

    public File prepareFullSizeImageFile() throws IOException {
        return createImageFile(true);
    }

    /**
     * Broadcast media to gallery.
     * // TODO: 16/05/2020 Check why that is not working as expected. ? Change location of photos taken to public dir.
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(this._path_image_normal);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        MainActivity.getAppContext().sendBroadcast(mediaScanIntent);
    }

    public Long getID() {
        return this._id;
    }

    public void delete() {
        File file = new File(this._path_image_normal);
        if (file.exists()) {
            file.delete();
        }
        file = new File(this._path_image_thumb);
        if (file.exists()) {
            file.delete();
        }
        MainActivity.db.removeNoteAttachment(this);

    }
}
