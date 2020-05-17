package pl.proenix.android.us2pum.lab6notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "notes.db";

    private static final String TABLE_NOTES = "notes";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PRIORITY = "priority";
    private static final String KEY_DUE_DATE = "due_date";
    private static final String KEY_CREATED_AT = "created_at";

    private static final String TABLE_ATTACHMENTS = "attachments";
    private static final String KEY_NOTE_ID = "id_note";
    private static final String KEY_PATH_IMG_NORMAL = "path_img_normal";
    private static final String KEY_PATH_IMG_THUMB = "path_img_thumb";

    DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_CONTENT + " TEXT,"
                + KEY_CATEGORY + " INTEGER, "
                + KEY_STATUS + " INTEGER, "
                + KEY_PRIORITY + " INTEGER, "
                + KEY_DUE_DATE + " INTEGER "
                + ")";
        db.execSQL(CREATE_NOTES_TABLE);

        upgradeToVersion2(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            upgradeToVersion2(db);
        }
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        String UPDATE_NOTES_TABLE = "ALTER TABLE " + TABLE_NOTES
                + " ADD COLUMN " + KEY_CREATED_AT + " INTEGER;";
        db.execSQL(UPDATE_NOTES_TABLE);

        String CREATE_ATTACHMENTS_TABLE = "CREATE TABLE " + TABLE_ATTACHMENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NOTE_ID + " INTEGER,"
                + KEY_PATH_IMG_NORMAL + " TEXT,"
                + KEY_PATH_IMG_THUMB + " TEXT,"
                + KEY_CREATED_AT + " INTEGER"
                + ")";
        db.execSQL(CREATE_ATTACHMENTS_TABLE);
    }

    /**
     * Reset DB to original state for development purposes.
     */
    void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTACHMENTS);

        onCreate(db);
    }

    /**
     * Initialize Sort and Filter with data.
     */
    void initializeSorterFilter() {
        _sortBy = KEY_DUE_DATE + " DESC";
        for (Map.Entry<Integer, Integer> cat : Note.getCategoriesColors()) {
            _categoryFilter.add(cat.getKey());
        }
        for (Map.Entry<Integer, String> priority : Note.getPriorities()) {
            _priorityFilter.add(priority.getKey());
        }
        _statusFilter.add(Note.STATUS_DONE);
        _statusFilter.add(Note.STATUS_IN_PROGRESS);
    }

    /**
     * Add new note to database and return its id.
     * @param note Note object populated with data.
     * @return long ID of added note.
     */
    long addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, note.getName());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_CATEGORY, note.getCategoryAsInt());
        values.put(KEY_STATUS, note.getStatus());
        values.put(KEY_PRIORITY, note.getPriority());
        values.put(KEY_DUE_DATE, note.getDueDateAsLong());
        values.put(KEY_CREATED_AT, note.getCreatedAt());

        // Save row to database, and update Note object with id.
        long id = db.insert(TABLE_NOTES, null, values);
        note.setID(id);

        return id;
    }

    /**
     * Update provided Note object representation in database.
     * @param note Note object populated with data and already existing in DB.
     * @return Boolean depending on success of operation.
     */
    boolean updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, note.getName());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_CATEGORY, note.getCategoryAsInt());
        values.put(KEY_STATUS, note.getStatus());
        values.put(KEY_PRIORITY, note.getPriority());
        values.put(KEY_DUE_DATE, note.getDueDateAsLong());
        values.put(KEY_CREATED_AT, note.getCreatedAt());

        int rowsUpdated = db.update(TABLE_NOTES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getID()) });
        // True if at least one row updated.
        return rowsUpdated > 0;
    }

    /**
     * Finds Note in database by provided id.
     * @param id Long ID of note
     * @return Note object or null
     */
    @Nullable
    Note findNoteById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_NOTES,
                new String[]{ KEY_ID, KEY_NAME, KEY_CONTENT, KEY_CATEGORY, KEY_STATUS, KEY_PRIORITY, KEY_DUE_DATE, KEY_CREATED_AT },
                KEY_ID + "=?",
                new String[]{ String.valueOf(id) },
                null, null, null, null)) {
            if (cursor != null) {
                cursor.moveToFirst();
                return new Note(
                        Long.parseLong(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getLong(6),
                        cursor.getLong(7)
                );
            }
        }
        return null;
    }

    /**
     * Add attachment to database.
     * @param attachment Attachment object - already populated with data.
     * @return id of added attachment.
     */
    long addAttachment(NoteAttachment attachment) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_ID, attachment.getNoteId());
        values.put(KEY_PATH_IMG_NORMAL, attachment.getPathImageNormal());
        values.put(KEY_PATH_IMG_THUMB, attachment.getPathImageThumbnail());
        values.put(KEY_CREATED_AT, attachment.getCurrentDateTime());

        // Save row to database, and update Note object with id.
        long id = db.insert(TABLE_ATTACHMENTS, null, values);
        attachment.setID(id);

        return id;
    }

    /**
     * Find single attachment by id.
     * @param id Id of attachment.
     * @return Attachment Object.
     */
    NoteAttachment findAttachmentById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_ATTACHMENTS,
                new String[]{ KEY_ID, KEY_NOTE_ID, KEY_PATH_IMG_NORMAL, KEY_PATH_IMG_THUMB},
                KEY_ID + "=?",
                new String[]{ String.valueOf(id) },
                null, null, null, null)) {
            if (cursor != null) {
                cursor.moveToFirst();
                return new NoteAttachment(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
            }
        }
        return null;
    }

    /**
     * Find all attachments in database that are connected to chosen note id.
     * @param noteId Note id.
     * @return List of attachment objects.
     */
    List<NoteAttachment> findAllAttachmentsByNoteId(long noteId) {
        List<NoteAttachment> attachments = new ArrayList<NoteAttachment>();
        String attachmentsQuery = "SELECT * FROM " + TABLE_ATTACHMENTS
                + " WHERE " + KEY_NOTE_ID + " = " + noteId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(attachmentsQuery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                NoteAttachment noteAttachment = new NoteAttachment(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                attachments.add(noteAttachment);
            }
            cursor.close();
        }
        return attachments;
    }

    /**
     * Find all notes.
     * @return List of Note objects.
     */
    List<Note> findAllNotes() {
        List<Note> notes = new ArrayList<Note>();
        String notesQuery = "SELECT * FROM " + TABLE_NOTES
                + " WHERE " + KEY_CATEGORY + " IN " + getCategoryFilterString()
                + " AND " + KEY_PRIORITY + " IN " + getPriorityFilterString()
                + " AND " + KEY_STATUS + " IN " + getStatusFilterString()
                + " ORDER BY " + _sortBy;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(notesQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Note note = new Note(
                        Long.parseLong(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getLong(6),
                        cursor.getLong(7)
                );
                notes.add(note);
            }
            cursor.close();
        }
        return notes;
    }

    private String _sortBy = KEY_DUE_DATE + " DESC";
    void setSortBy(String column, String order) {
        this._sortBy = column + " " + order;
    }
    public void setSortByPriority(String order) {
        if (order == "ASC") {
            setSortBy(KEY_PRIORITY, "ASC");
        } else {
            setSortBy(KEY_PRIORITY, "DESC");
        }
    }
    public void setSortByTitle(String order) {
        if (order == "ASC") {
            setSortBy(KEY_NAME, "ASC");
        } else {
            setSortBy(KEY_NAME, "DESC");
        }
    }
    public void setSortByDueDate(String order) {
        if (order == "ASC") {
            setSortBy(KEY_DUE_DATE, "ASC");
        } else {
            setSortBy(KEY_DUE_DATE, "DESC");
        }
    }

    private List<Integer> _priorityFilter = new ArrayList<Integer>();
    public List<Integer> getPriorityFilter() {
        return this._priorityFilter;
    }
    void addPriorityFilter(Integer id) {
        if (_priorityFilter.indexOf(id) == -1) {
            _priorityFilter.add(id);
        }
    }
    void removePriorityFilter(Integer id) {
        _priorityFilter.remove(id);
    }
    String getPriorityFilterString() {
        if (_priorityFilter.size() != 0) {
            StringBuilder s = new StringBuilder();
            for (Integer cat : _priorityFilter) {
                s.append(cat).append(", ");
            }
            return "(" + s.toString().substring(0, s.toString().length() - 2) + ")";
        }
        return  "()";
    }

    private List<Integer> _categoryFilter = new ArrayList<Integer>();
    public List<Integer> getCategoryFilter() {
        return this._categoryFilter;
    }
    void addCategoryFilter(Integer id) {
        if (_categoryFilter.indexOf(id) == -1) {
            _categoryFilter.add(id);
        }
    }
    void removeCategoryFilter(Integer id) {
        _categoryFilter.remove(id);
    }
    String getCategoryFilterString() {
        if (_categoryFilter.size() != 0) {
            StringBuilder s = new StringBuilder();
            for (Integer cat : _categoryFilter) {
                s.append(cat).append(", ");
            }
            return "(" + s.toString().substring(0, s.toString().length() - 2) + ")";
        }
        return "()";
    }

    private List<Integer> _statusFilter = new ArrayList<Integer>();
    public List<Integer> getStatusFilter() {
        return this._statusFilter;
    }
    void addStatusFilter(Integer id) {
        if (_statusFilter.indexOf(id) == -1) {
            _statusFilter.add(id);
        }
    }
    void removeStatusFilter(Integer id) {
        _statusFilter.remove(id);
    }
    String getStatusFilterString() {
        if (_statusFilter.size() != 0) {
            StringBuilder s = new StringBuilder();
            for (Integer cat : _statusFilter) {
                s.append(cat).append(", ");
            }
            return "(" + s.toString().substring(0, s.toString().length() - 2) + ")";
        }
        return "()";
    }


    /**
     * Remove note from DB.
     * @param note Note object.
     * @return True if note found and deleted.
     */
    boolean removeNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTES, KEY_ID + "=?", new String[]{String.valueOf(note.getID())}) > 0;
    }

    /**
     * Remove attachment from DB.
     * @param noteAttachment NoteAttachment object.
     * @return True if note found and deleted.
     */
    boolean removeNoteAttachment(NoteAttachment noteAttachment) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ATTACHMENTS, KEY_ID + "=?", new String[]{String.valueOf(noteAttachment.getID())}) > 0;
    }
}
