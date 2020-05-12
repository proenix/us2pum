package pl.proenix.android.us2pum.lab6notes;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
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
    private Calendar _dueDateCalendar;

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
        if (dueDate == null) {
            this._dueDate = (long) -1;
        } else {
            this._dueDate = dueDate;
        }
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
        "; name: " + this._name +
        "; content: " + this._content +
        "; category:" + this._category +
        "; status: " + this._status +
        "; priority: " + this._priority +
        "; dueDate: " + this._dueDate + "}";
    }

    public String getName() {
        return this._name;
    }

    public String getContent() {
        return this._content;
    }

    public void setContent(String content) {
        this._content = content;
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
        this._dueDateCalendar = dueDate;
    }

    public void removeDueDate() {
        this._dueDate = (long) -1;
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

    /**
     * Check if due date is older than now.
     * @return boolean true if after due.
     */
    public boolean isAfterDue() {
        if (this.getDueDateAsCalendar().compareTo(Calendar.getInstance()) < 1) {
            return true;
        }
        return false;
    }

    public int getTextColor() {
        switch (this._category) {
            case CATEGORY_HOME:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryHomeText);
            case CATEGORY_FINANCE:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryFinanceText);
            case CATEGORY_WORK:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryWorkText);
            case CATEGORY_VACATION:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryVacationText);
            case CATEGORY_SCHOOL:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategorySchoolText);
            default:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryDefaultText);
        }
    }

    public int getAfterDueColor() {
        switch (this._category) {
            case CATEGORY_HOME:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryHomeAfterDue);
            case CATEGORY_FINANCE:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryFinanceAfterDue);
            case CATEGORY_WORK:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryWorkAfterDue);
            case CATEGORY_VACATION:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryVacationAfterDue);
            case CATEGORY_SCHOOL:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategorySchoolAfterDue);
            default:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryDefaultText);
        }
    }

    public int getBackgroundColor() {
        switch (this._category) {
            case CATEGORY_HOME:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryHomeBackground);
            case CATEGORY_FINANCE:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryFinanceBackground);
            case CATEGORY_WORK:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryWorkBackground);
            case CATEGORY_VACATION:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryVacationBackground);
            case CATEGORY_SCHOOL:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategorySchoolBackground);
            default:
                return ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryDefaultBackground);
        }
    }

    public static List<Map.Entry<Integer, Integer>> getCategoriesColors() {
        List<Map.Entry<Integer, Integer>> categoriesColors = new ArrayList<>();
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_HOME, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryHomeBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_FINANCE, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryFinanceBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_WORK, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryWorkBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_VACATION, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryVacationBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_SCHOOL, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategorySchoolBackground)));
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
        Log.d("AndroidNotes", "Saving: "+this.toString());
    }

    public void setStatusInProgress() {
        this._status = STATUS_IN_PROGRESS;
    }
    public void setStatusDone() {
        this._status = STATUS_DONE;
    }

    /**
     * Format date in standardized way.
     * @return String Formatted date string.
     */
    public String getFormattedDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("E, dd-MMM-YYYY");
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(this.getDueDateAsCalendar().getTime());
        } else {
            return this.getDueDateAsCalendar().get(Calendar.YEAR) + "-" + (this.getDueDateAsCalendar().get(Calendar.MONTH) + 1) + "-" + this.getDueDateAsCalendar().get(Calendar.DAY_OF_MONTH);
        }
    }

    /**
     * Format time in standardized way.
     * @return String Formatted time string.
     */
    public String getFormattedTime() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = null;
            dateFormat = new SimpleDateFormat("HH:mm");
            dateFormat.setTimeZone(TimeZone.getDefault());
            return dateFormat.format(this.getDueDateAsCalendar().getTime());
        } else {
            return this.getDueDateAsCalendar().get(Calendar.HOUR_OF_DAY) + ":" + this.getDueDateAsCalendar().get(Calendar.MINUTE);
        }
    }

    /**
     * Get Calendar type object representing due date.
     * If due date is not set for note returns current time.
     * @return Calendar object representing due date.
     */
    public Calendar getDueDateAsCalendar() {
        if (this._dueDateCalendar == null) {
            this._dueDateCalendar = Calendar.getInstance();
            if (this.hasDueDate()) {
             this._dueDateCalendar.setTimeInMillis(this.getDueDateAsLong()*1000);
            }
        }
        return this._dueDateCalendar;
    }

    /**
     * Delete note from existence (from database).
     */
    public void delete() {
        if (this._id != null) {
            MainActivity.db.removeNote(this);
        }
    }

    /**
     * Get pretty formatted all note fields.
     * @return String pretty formatted note
     */
    public String getSharableContent() {
        StringBuilder sharedContent = new StringBuilder();
        sharedContent.append("Title: ").append(this.getTitle()).append('\n');
        sharedContent.append("Status: ").append(this.getStatusName()).append('\n');
        //sharedContent.append("Priority: ").append(this.getPriorityName()).append('\n');
        sharedContent.append("Category: ").append(this.getCategoryName()).append('\n');
        if (hasDueDate()) {
            sharedContent.append("Due date: ").append(this.getFormattedDate()).append(" ").append(this.getFormattedTime()).append('\n');
        }
        sharedContent.append("Content: ").append(this.getContent()).append('\n');
        return sharedContent.toString();
    }

    /**
     * Get Category in human readable form.
     * @return String category name.
     */
    private String getCategoryName() {
        return getCategoryNameByInt(this.getCategoryAsInt());
    }

    /**
     * Get status in readable form.
     * @return String status name.
     */
    private String getStatusName() {
        if (this._status == STATUS_DONE) {
            return MainActivity.getAppContext().getString(R.string.note_status_done);
        }
        return MainActivity.getAppContext().getString(R.string.note_status_in_progress);
    }
}
