package pl.proenix.android.us2pum.lab6notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Note representation.
 */
class Note {

    /**
     * Default Priority set for Notes without explicitly declared priority.
     */
    public static final int PRIORITY_DEFAULT = 1000;

    /**
     * Categories types.
     * // TODO: 10/05/2020 Create dedicated table to store categories and allow user to create custom categories.
     */
    public static final int CATEGORY_HOME = 2001;
    public static final int CATEGORY_FINANCE = 2002;
    public static final int CATEGORY_WORK = 2003;
    public static final int CATEGORY_VACATION = 2004;
    public static final int CATEGORY_SCHOOL = 2005;

    /**
     * ID in database.
     */
    private Long _id;
    private String _name;
    private String _content;
    /**
     * Category representation as integer. Categories const and names are declared in this class.
     */
    private Integer _category;
    /**
     * Status representation as integer.
     */
    private Integer _status;
    private Integer _priority;
    /**
     * Due Date representation as seconds from epoch.
     */
    private Long _dueDate;

    public Note() {}

    /**
     * Recreate Note object and populate with data. (from database)
     * @param id ID of object from database
     * @param name Name - subject of note
     * @param content Content of note
     * @param category Category of note as integer.
     * @param status Status of note as integer
     * @param priority Priority of note as integer.
     * @param dueDate Due Date time in seconds from epoch as integer.
     */
    public Note(long id, String name, String content, Integer category, Integer status, Integer priority, Long dueDate) {
        this._id = id;
        this._name = name;
        this._content = content;
        this._category = category;
        this._status = status;
        this._priority = priority;
        this._dueDate = dueDate;
    }

    /**
     * Create new Note object and populate with data.
     * @param name Name - subject of note
     * @param content Content of note
     * @param category Category of note as integer.
     * @param status Status of note as integer
     * @param priority Priority of note as integer.
     * @param dueDate Due Date time in seconds from epoch as integer.
     */
    public Note(String name, String content, Integer status, Integer category, @Nullable Integer priority, @Nullable Long dueDate) {
        this._name = name;
        this._content = content;
        this._status = status;
        this._category = category;
        if (priority == null) {
            this._priority = PRIORITY_DEFAULT;
        } else {
            this._priority = priority;
        }
        if (dueDate == null) {
            this._dueDate = (long) -1;
        } else {
            this._dueDate = dueDate;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Note: {" +
        "id: " + this._id +
        "name: " + this._name +
        "content: " + this._content +
        "category:" + this._category +
        "status: " + this._status +
        "priority: " + this._priority +
        "dueDate: " + this._dueDate + "}";
    }

    public String getName() {
        return this._name;
    }

    public String getContent() {
        return this._content;
    }

    public Integer getCategoryAsInt() {
        return this._category;
    }


    public Integer getStatus() {
        return this._status;
    }

    public Integer getPriority() {
        return this._priority;
    }

    public Long getDueDateAsLong() {
        return this._dueDate;
    }

    public void setID(long id) {
        this._id = id;
    }

    public Long getID() {
        return this._id;
    }
}
