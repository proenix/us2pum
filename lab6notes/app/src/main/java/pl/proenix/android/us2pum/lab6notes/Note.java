package pl.proenix.android.us2pum.lab6notes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Note representation.
 */
class Note {


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
     * Categories types.
     * // TODO: 10/05/2020 Create dedicated table to store categories and allow user to create custom categories.
     */
    public static final int CATEGORY_HOME = 2001;
    public static final int CATEGORY_FINANCE = 2002;
    public static final int CATEGORY_WORK = 2003;
    public static final int CATEGORY_VACATION = 2004;
    public static final int CATEGORY_SCHOOL = 2005;
    /**
     * Status representation as integer.
     */
    private Integer _status;
    public static final int STATUS_IN_PROGRESS = 3001;
    public static final int STATUS_DONE = 3002;

    /**
     * Priority of notes.
     */
    private Integer _priority;
    /**
     * Default Priority set for Notes without explicitly declared priority.
     */
    public static final int PRIORITY_DEFAULT = 1000;

    /**
     * Due Date representation as seconds from epoch.
     */
    private Long _dueDate;

    public Note() {
        this._name = "";
        this._content = "";
        this._category = CATEGORY_HOME;
        this._status = STATUS_IN_PROGRESS;
        this._priority = PRIORITY_DEFAULT;
        this._dueDate = (long) -1;

    }

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

    /**
     * Find note by ID.
     * @param noteID
     * @return
     */
    public static Note findById(long noteID) {
        return MainActivity.db.findNoteById(noteID);
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

    public void setCategory(Integer categoryID) {
        this._category = categoryID;
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

    /**
     * Set Due Date from provided Calendar object.
     * @param dueDate Calendar object with date time set.
     */
    public void setDueDate(Calendar dueDate) {
        this._dueDate = dueDate.getTimeInMillis() / 1000;
    }

    public void setID(long id) {
        this._id = id;
    }

    public Long getID() {
        return this._id;
    }

    public boolean isDone() {
        return this._status == STATUS_DONE;
    }

    public String getTitle() {
        return this._name;
    }
    public void setTitle(String title) {
        this._name = title;
    }

    public boolean hasDueDate() {
        return (this._dueDate != -1);
    }

    public boolean isAfterDue() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int currentTimeInSeconds = LocalDateTime.now().getSecond();
            Log.d("AndroidNotes", "Checking time in Oreo++");
            return (this._dueDate <= (long) currentTimeInSeconds);
        } else {
            Log.d("AndroidNotes", "Checking time in Oreo--");
            long timestamp = System.currentTimeMillis() / 1000;
            return (this._dueDate <= timestamp);
        }
    }

    public int getTextColor() {
        switch (this._category) {
            case CATEGORY_HOME:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryHomeText);
            case CATEGORY_FINANCE:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryFinanceText);
            case CATEGORY_WORK:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryWorkText);
            case CATEGORY_VACATION:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryVacationText);
            case CATEGORY_SCHOOL:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategorySchoolText);
            default:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryDefaultText);
        }
    }

    public int getAfterDueColor() {
        switch (this._category) {
            case CATEGORY_HOME:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryHomeAfterDue);
            case CATEGORY_FINANCE:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryFinanceAfterDue);
            case CATEGORY_WORK:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryWorkAfterDue);
            case CATEGORY_VACATION:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryVacationAfterDue);
            case CATEGORY_SCHOOL:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategorySchoolAfterDue);
            default:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryDefaultText);
        }
    }

    public int getBackgroundColor() {
        switch (this._category) {
            case CATEGORY_HOME:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryHomeBackground);
            case CATEGORY_FINANCE:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryFinanceBackground);
            case CATEGORY_WORK:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryWorkBackground);
            case CATEGORY_VACATION:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryVacationBackground);
            case CATEGORY_SCHOOL:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategorySchoolBackground);
            default:
                return ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryDefaultBackground);
        }
    }

    public static List<Map.Entry<Integer, Integer>> getCategoriesColors() {
        List<Map.Entry<Integer, Integer>> categoriesColors = new ArrayList<>();
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_HOME, ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryHomeBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_FINANCE, ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryFinanceBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_WORK, ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryWorkBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_VACATION, ContextCompat.getColor(MainActivity.appContext, R.color.colorCategoryVacationBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_SCHOOL, ContextCompat.getColor(MainActivity.appContext, R.color.colorCategorySchoolBackground)));
        return categoriesColors;
    }

    public static String getCategoryNameByInt(int categoryId) {
        switch (categoryId) {
            case CATEGORY_HOME:
                return "Home";
            case CATEGORY_FINANCE:
                return "Finance";
            case CATEGORY_WORK:
                return "Work";
            case CATEGORY_VACATION:
                return "Vacation";
            case CATEGORY_SCHOOL:
                return "School";
            default:
                return "Other";
        }

    }

    /**
     * Save Note to database. If note has no ID save it in db as new note.
     */
    public void save() {
        if (this._id == null) {
            MainActivity.db.addNote(this);
        } else {
            MainActivity.db.updateNote(this);
        }
    }

    public void setStatusInProgress() {
        this._status = STATUS_IN_PROGRESS;
    }
    public void setStatusDone() {
        this._status = STATUS_DONE;
    }
}
