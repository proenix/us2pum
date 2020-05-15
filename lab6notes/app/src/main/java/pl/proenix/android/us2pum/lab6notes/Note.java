package pl.proenix.android.us2pum.lab6notes;

import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

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
    private static final int CATEGORY_HOME = 2001;
    private static final int CATEGORY_FINANCE = 2002;
    private static final int CATEGORY_WORK = 2003;
    private static final int CATEGORY_VACATION = 2004;
    private static final int CATEGORY_SCHOOL = 2005;
    /**
     * Status representation as integer.
     */
    private Integer _status;
    private static final int STATUS_IN_PROGRESS = 3001;
    private static final int STATUS_DONE = 3002;

    /**
     * Priority of notes.
     */
    private Integer _priority;
    /**
     * Default Priority set for Notes without explicitly declared priority.
     */
    private static final int PRIORITY_DEFAULT = 1050;
    private static final int PRIORITY_LOW = 1010;
    private static final int PRIORITY_HIGH = 1060;
    private static final int PRIORITY_CRITICAL = 1080;

    /**
     * Due Date representation as seconds from epoch.
     */
    private Long _dueDate;
    private Calendar _dueDateCalendar;

    private static final int NOTE_TITLE_MAX_PREVIEW_CHARS = 50;
    private static final int NOTE_CONTENT_MAX_PREVIEW_CHARS = 120;

    Note() {
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
    Note(long id, String name, String content, Integer category, Integer status, Integer priority, Long dueDate) {
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
     * @param noteID ID of note in database.
     * @return Note object or null.
     */
    static Note findById(long noteID) {
        return MainActivity.db.findNoteById(noteID);
    }

    public static List<Map.Entry<Integer, String>> getPriorities() {
        List<Map.Entry<Integer, String>> prioritiesList = new ArrayList<>();
        prioritiesList.add(new AbstractMap.SimpleEntry<Integer, String>(PRIORITY_DEFAULT, MainActivity.getAppContext().getString(R.string.note_priority_default)));
        prioritiesList.add(new AbstractMap.SimpleEntry<Integer, String>(PRIORITY_HIGH, MainActivity.getAppContext().getString(R.string.note_priority_high)));
        prioritiesList.add(new AbstractMap.SimpleEntry<Integer, String>(PRIORITY_CRITICAL, MainActivity.getAppContext().getString(R.string.note_priority_critical)));
        prioritiesList.add(new AbstractMap.SimpleEntry<Integer, String>(PRIORITY_LOW, MainActivity.getAppContext().getString(R.string.note_priority_low)));
        return prioritiesList;
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
        return this.getDueDateAsCalendar().compareTo(Calendar.getInstance()) < 1;
    }

    /**
     * Get text color resource id for note text.
     * @return Color ID resource.
     */
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

    /**
     * Get text color resource id for note due date field.
     * @return Color ID resource.
     */
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
    /**
     * Get text color resource id for note background.
     * @return Color ID resource.
     */
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

    /**
     * Get text color resource id for note category.
     * @return List of pairs of integers. First integer represents category ID, second background color of that category.
     */
    public static List<Map.Entry<Integer, Integer>> getCategoriesColors() {
        List<Map.Entry<Integer, Integer>> categoriesColors = new ArrayList<>();
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_HOME, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryHomeBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_FINANCE, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryFinanceBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_WORK, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryWorkBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_VACATION, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategoryVacationBackground)));
        categoriesColors.add(new AbstractMap.SimpleEntry<Integer, Integer>(CATEGORY_SCHOOL, ContextCompat.getColor(MainActivity.getAppContext(), R.color.colorCategorySchoolBackground)));
        return categoriesColors;
    }

    /**
     * Get name of category in readable format by category integer representation.
     * @param categoryId Category as integer.
     * @return String category name in readable format.
     */
    public static String getCategoryNameByInt(int categoryId) {
        switch (categoryId) {
            case CATEGORY_HOME:
                return MainActivity.getAppContext().getString(R.string.category_name_home);
            case CATEGORY_FINANCE:
                return MainActivity.getAppContext().getString(R.string.category_name_finance);
            case CATEGORY_WORK:
                return MainActivity.getAppContext().getString(R.string.category_name_work);
            case CATEGORY_VACATION:
                return MainActivity.getAppContext().getString(R.string.category_name_vacation);
            case CATEGORY_SCHOOL:
                return MainActivity.getAppContext().getString(R.string.category_name_school);
            default:
                return MainActivity.getAppContext().getString(R.string.category_name_other);
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

    /**
     * Format date in standardized way.
     * @return String Formatted date string.
     */
    public String getFormattedDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd-MMM-YYYY");
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
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
        sharedContent.append(MainActivity.getAppContext().getString(R.string.note_sharable_title)).append(this.getTitle()).append('\n');
        sharedContent.append(MainActivity.getAppContext().getString(R.string.note_sharable_status)).append(this.getStatusName()).append('\n');
        //sharedContent.append("Priority: ").append(this.getPriorityName()).append('\n');
        sharedContent.append(MainActivity.getAppContext().getString(R.string.note_sharable_category)).append(this.getCategoryName()).append('\n');
        if (hasDueDate()) {
            sharedContent.append(MainActivity.getAppContext().getString(R.string.note_sharable_due_date)).append(this.getFormattedDate()).append(" ").append(this.getFormattedTime()).append('\n');
        }
        sharedContent.append(MainActivity.getAppContext().getString(R.string.note_sharable_content)).append(this.getContent()).append('\n');
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

    /**
     * Get shortened version of note title. Limited to NOTE_TITLE_MAX_PREVIEW_CHARS characters.
     * @return String shortened title.
     */
    public String getTitleShort() {
        if (this._name.length() >= NOTE_TITLE_MAX_PREVIEW_CHARS) {
            return this._name.substring(0, NOTE_TITLE_MAX_PREVIEW_CHARS) + "...";
        }
        return this._name;
    }

    /**
     * Get shortened version of note content. Limited to NOTE_CONTENT_MAX_PREVIEW_CHARS characters.
     * @return String shortened content.
     */
    public String getContentShort() {
        if (this._content.length() >= NOTE_CONTENT_MAX_PREVIEW_CHARS) {
            return this._content.substring(0, NOTE_CONTENT_MAX_PREVIEW_CHARS) + "...";
        }
        return this._content;
    }

    public String getPriorityName() {
        switch (this._priority) {
            case PRIORITY_DEFAULT: // Normal priority
                return MainActivity.getAppContext().getString(R.string.note_priority_default);
            case PRIORITY_CRITICAL:
                return MainActivity.getAppContext().getString(R.string.note_priority_critical);
            case PRIORITY_HIGH:
                return MainActivity.getAppContext().getString(R.string.note_priority_high);
            case PRIORITY_LOW:
                return MainActivity.getAppContext().getString(R.string.note_priority_low);
        }
        return "";
    }

    public void setPriority(int priorityInt) {
        this._priority = priorityInt;
    }
}
