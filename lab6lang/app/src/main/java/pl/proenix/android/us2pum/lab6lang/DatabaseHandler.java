package pl.proenix.android.us2pum.lab6lang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "languages";

    private static final String TABLE_WORDS = "words";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LEARNABLE = "learnable";
    private static final String KEY_LANG = "language";
    private static final String KEY_LEARN_STATE = "learn_state";

    private static final String TABLE_WORDS_RELATION = "words_relation";
    private static final String KEY_ID_WORD_1 = "id_word_1";
    private static final String KEY_ID_WORD_2 = "id_word_2";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORD_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_LANG + " INTEGER, "
                + KEY_LEARNABLE + " INTEGER,"
                + KEY_LEARN_STATE + " INTEGER "
                + ")";
        db.execSQL(CREATE_WORD_TABLE);

        String CREATE_WORD_RELATION_TABLE = "CREATE TABLE " + TABLE_WORDS_RELATION + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ID_WORD_1 + " INTEGER,"
                + KEY_ID_WORD_2 + " INTEGER, "
                + "FOREIGN KEY(" + KEY_ID_WORD_1 + ") REFERENCES " + TABLE_WORDS + "(" + KEY_ID + "),"
                + "FOREIGN KEY(" + KEY_ID_WORD_2 + ") REFERENCES " + TABLE_WORDS + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_WORD_RELATION_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS_RELATION);

        // Create tables again
        onCreate(db);
    }

    /**
     * Reset DB to orginal state for development purposes.
     */
    void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS_RELATION);

        onCreate(db);
    }

    /**
     * Add new Word.
     * @param word Word object.
     * @return int ID of inserted row.
     */
    int addWord(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, word.getName()); // Word
        values.put(KEY_LANG, word.getLanguage()); // Lang
        values.put(KEY_LEARNABLE, word.getLearnable()); // Learnable
        values.put(KEY_LEARN_STATE, word.getLearnState());

        // Inserting Row
        long id = db.insert(TABLE_WORDS, null, values);

        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

        return (int)id;
    }

    /**
     * Add new Word relation.
     * @param wordsRelation WordRelation object.
     * @return int ID of inserted row.
     */
    int addWordsRelation(WordRelation wordsRelation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_WORD_1, wordsRelation.getWord1ID());
        values.put(KEY_ID_WORD_2, wordsRelation.getWord2ID());

        // Inserting Row
        long id = db.insert(TABLE_WORDS_RELATION, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

        return (int)id;
    }

    /**
     * Add new Word relation.
     * @param id_word_1 ID of word 1.
     * @param id_word_2 ID of word 2.
     */
    void addWordsRelation(int id_word_1, int id_word_2) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_WORD_1, id_word_1);
        values.put(KEY_ID_WORD_2, id_word_2);

        // Inserting Row
        long id = db.insert(TABLE_WORDS_RELATION, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    /**
     * Get Word by ID.
     * @param id ID of looked word.
     * @return Word Object.
     */
    Word getWordById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_WORDS,
                new String[] { KEY_ID, KEY_NAME, KEY_LANG, KEY_LEARNABLE, KEY_LEARN_STATE },
                KEY_ID + "=?",
                new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            return new Word(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    Integer.parseInt(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(4))
            );
        }
        return new Word();
    }

    /**
     * Get Words total count.
     * @return Integer number of words in all languages.
     */
    int getWordsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_WORDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            int number = cursor.getCount();
            cursor.close();
            return  number;
        }
        return 0;
    }

    int getWordsCountByLanguage(int language) {
        String countQuery = "SELECT  * FROM " + TABLE_WORDS + " WHERE " + KEY_LANG + " = " + language;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            int number = cursor.getCount();
            cursor.close();
            return  number;
        }
        return 0;
    }

    public int getWordsCountByLanguageAndLearnState(int language, int learnState) {
        String countQuery = "SELECT  * FROM " + TABLE_WORDS
                + " WHERE " + KEY_LANG + " = " + language
                + " AND " + KEY_LEARN_STATE + " = " + learnState;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            int number = cursor.getCount();
            cursor.close();
            return  number;
        }
        return 0;
    }

    public int getWordsCountByLanguageAndLearnState(int language, int learnState, @Nullable String learnStateOperator) {
        if (learnStateOperator == null) {
            learnStateOperator = "=";
        }
        String countQuery = "SELECT  * FROM " + TABLE_WORDS
                + " WHERE " + KEY_LANG + " = " + language
                + " AND " + KEY_LEARN_STATE + " " + learnStateOperator + " " + learnState;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            int number = cursor.getCount();
            cursor.close();
            return  number;
        }
        return 0;
    }

    List<Word> getWordsByLanguageAndLearnable(int language, int learnable) {
        List<Word> words = new ArrayList<Word>();
        String wordsQuery = "SELECT * FROM " + TABLE_WORDS
                + " WHERE " + KEY_LANG + " = " + language
                + " AND " + KEY_LEARNABLE + " = " + learnable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(wordsQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Word word = new Word(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    Integer.parseInt(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(4)));
                Log.d("AndroidLang",
                        "ID: " + String.valueOf(word.getID()) + "\n"+
                                "Name: " + word.getName() +"\n"+
                                "Lang: " + String.valueOf(word.getLanguage()) +"\n"+
                                "Learnable: " + String.valueOf(word.getLearnable()) +"\n"+
                                "LearnState: " + String.valueOf(word.getLearnState())
                );
                words.add(word);
            }
        }
        return words;
    }

    List<Word> getWordsByLanguageAndLearnableAndLearnState(int language, int learnable, int learnState, @Nullable String learnStateOperator) {
        if (learnStateOperator == null) {
            learnStateOperator = "=";
        }
        List<Word> words = new ArrayList<Word>();
        String wordsQuery = "SELECT * FROM " + TABLE_WORDS
                + " WHERE " + KEY_LANG + " = " + language
                + " AND " + KEY_LEARNABLE + " = " + learnable
                + " AND " + KEY_LEARN_STATE + " " + learnStateOperator + " " + learnState;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(wordsQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Word word = new Word(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4)));
//                Log.d("AndroidLang",
//                        "ID: " + String.valueOf(word.getID()) + "\n"+
//                                "Name: " + word.getName() +"\n"+
//                                "Lang: " + String.valueOf(word.getLanguage()) +"\n"+
//                                "Learnable: " + String.valueOf(word.getLearnable()) +"\n"+
//                                "LearnState: " + String.valueOf(word.getLearnState())
//                );
                words.add(word);
            };
        }
        return words;
    }

    public int updateWord(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, word.getName()); // Word
        values.put(KEY_LANG, word.getLanguage()); // Lang
        values.put(KEY_LEARNABLE, word.getLearnable()); // Learnable
        values.put(KEY_LEARN_STATE, word.getLearnState());

        // updating row
        db.update(TABLE_WORDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(word.getID()) });
//        Log.d("AndroidLang",
//                "ID: " + String.valueOf(word.getID()) + "\n"+
//                        "Name: " + word.getName() +"\n"+
//                        "Lang: " + String.valueOf(word.getLanguage()) +"\n"+
//                        "Learnable: " + String.valueOf(word.getLearnable()) +"\n"+
//                        "LearnState: " + String.valueOf(word.getLearnState())
//        );
        return 0;
    }

    public List<Word> getRelatedWordsByIdAndLanguage(int id, int oppositeLanguage) {
        List<Word> wordList = new ArrayList<Word>();
        String wordRelationQuery = "SELECT  * FROM " + TABLE_WORDS_RELATION
                + " WHERE " + KEY_ID_WORD_1 + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(wordRelationQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                wordList.add(this.getWordById(Integer.parseInt(cursor.getString(2))));
            };
        }
        return wordList;
    }
}
