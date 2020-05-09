package pl.proenix.android.us2pum.lab6lang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    private SQLiteDatabase _db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    /**
     * Create tables for application.
     * @param db
     */
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

        // TODO: 09/05/2020 Write importer to import from file.
        this._db = db;
        this.addWord(new Word("car", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 1);
        this.addWord(new Word("samochód", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0), 2);
        this.addWord(new Word("auto", Word.WORD_LANGUAGE_POLISH, Word.WORD_NOT_LEARNABLE,0), 3);
        this.addWordsRelation(new WordRelation(1,2), 1);
        this.addWordsRelation(new WordRelation(1,3), 2);

        // Add second English Word.
        this.addWord(new Word("vehicle", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 4);
        this.addWord(new Word("pojazd", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0), 5);
        this.addWordsRelation(new WordRelation(4,5), 3);

        this.addWord(new Word("automobile", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 6);
        this.addWordsRelation(new WordRelation(6,2), 4);
        this.addWordsRelation(new WordRelation(6,3), 5);

        this.addWordsRelation(new WordRelation(6,1), 6);
        this.addWordsRelation(new WordRelation(1,6), 7);

        this.addWord(new Word("city", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 7);
        this.addWord(new Word("miasto", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0), 8);
        this.addWordsRelation(new WordRelation(7,8), 8);

        this.addWord(new Word("computer", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0),9);
        this.addWord(new Word("komputer", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0),10);
        this.addWord(new Word("PC", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0),11);
        this.addWord(new Word("PC", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0),12);
        this.addWordsRelation(new WordRelation(9,10), 9);
        this.addWordsRelation(new WordRelation(9,12), 10);

        this.addWordsRelation(new WordRelation(11,10), 11);
        this.addWordsRelation(new WordRelation(11,12), 12);

        this.addWordsRelation(new WordRelation(9,11), 13);
        this.addWordsRelation(new WordRelation(11,9), 14);
        this.addWordsRelation(new WordRelation(10,12), 15);
        this.addWordsRelation(new WordRelation(12,10), 16);

        this.addWord(new Word("parsley", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 13);
        this.addWord(new Word("pietruszka", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0),14);
        this.addWordsRelation(new WordRelation(13,14), 17);

        this.addWord(new Word("year", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 15);
        this.addWord(new Word("rok", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0),16);
        this.addWordsRelation(new WordRelation(15,16), 18);

        this.addWord(new Word("today", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 17);
        this.addWord(new Word("dziś", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0),18);
        this.addWordsRelation(new WordRelation(17,18), 19);

        this.addWord(new Word("tommorow", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 19);
        this.addWord(new Word("jutro", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0),20);
        this.addWordsRelation(new WordRelation(19,20), 20);

        this.addWord(new Word("yesterday", Word.WORD_LANGUAGE_ENGLISH, Word.WORD_LEARNABLE,0), 21);
        this.addWord(new Word("wczoraj", Word.WORD_LANGUAGE_POLISH, Word.WORD_LEARNABLE,0),22);
        this.addWordsRelation(new WordRelation(21,22), 21);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Future releases
            // upgradeToVersion2(db);
        }
        if (oldVersion < 3) {
            // Future releases
            // upgradeToVersion3(db);
        }
    }

    /**
     * Reset DB to original state for development purposes.
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
        db.close(); // Closing database connection

        return (int)id;
    }

    /**
     * Add new Word. For migrations only.
     * @param word Word object.
     * @param id Int ID of object.
     */
    private void addWord(Word word, int id) {
//        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // ID
        values.put(KEY_NAME, word.getName()); // Word
        values.put(KEY_LANG, word.getLanguage()); // Lang
        values.put(KEY_LEARNABLE, word.getLearnable()); // Learnable
        values.put(KEY_LEARN_STATE, word.getLearnState());

        // Inserting Row
        _db.insert(TABLE_WORDS, null, values);
//        db.close(); // Closing database connection
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
        db.close();

        return (int)id;
    }

    /**
     * Add new Word relation. For migrations only.
     * @param wordsRelation WordRelation object.
     * @param id int ID of inserted row.
     */
    void addWordsRelation(WordRelation wordsRelation, int id) {
//        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_ID_WORD_1, wordsRelation.getWord1ID());
        values.put(KEY_ID_WORD_2, wordsRelation.getWord2ID());

        // Inserting Row
        _db.insert(TABLE_WORDS_RELATION, null, values);
//        db.close();
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
        db.close();
    }

    /**
     * Get Word by ID.
     * @param id ID of looked word.
     * @return Word Object.
     */
    Word getWordById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(
                TABLE_WORDS,
                new String[]{KEY_ID, KEY_NAME, KEY_LANG, KEY_LEARNABLE, KEY_LEARN_STATE},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null)) {
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
        }
        return new Word();
    }

    /**
     * Get Words total count.
     * @return Integer number of words in all languages.
     */
    int getWordsCount() {
        String countQuery = "SELECT * FROM " + TABLE_WORDS;
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
        String countQuery = "SELECT * FROM " + TABLE_WORDS + " WHERE " + KEY_LANG + " = " + language;
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
                words.add(word);
            }
            cursor.close();
        }
        return words;
    }

    List<Word> getWordsByLanguageAndLearnableAndLearnState(int language, int learnable, int learnState, @Nullable String learnStateOperator, @Nullable Integer limit) {
        if (learnStateOperator == null) {
            learnStateOperator = "=";
        }

        List<Word> words = new ArrayList<Word>();
        String wordsQuery = "SELECT * FROM " + TABLE_WORDS
                + " WHERE " + KEY_LANG + " = " + language
                + " AND " + KEY_LEARNABLE + " = " + learnable
                + " AND " + KEY_LEARN_STATE + " " + learnStateOperator + " " + learnState
                + " ORDER BY " + KEY_LEARN_STATE + " ASC "; // get least learned words first
        if (limit != null) {
            wordsQuery += " LIMIT " + limit;
        }
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
                words.add(word);
            };
            cursor.close();
        }
        return words;
    }

    public void updateWord(Word word) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, word.getName()); // Word
        values.put(KEY_LANG, word.getLanguage()); // Lang
        values.put(KEY_LEARNABLE, word.getLearnable()); // Learnable
        values.put(KEY_LEARN_STATE, word.getLearnState());

        // updating row
        db.update(TABLE_WORDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(word.getID()) });
    }

    public List<Word> getRelatedWordsByIdAndLanguage(int id, int language) {
        List<Word> wordList = new ArrayList<Word>();
        String wordRelationQuery = "SELECT  * FROM " + TABLE_WORDS_RELATION
                + " WHERE " + KEY_ID_WORD_1 + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(wordRelationQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Word w = this.getWordByIdAndLanguage(Integer.parseInt(cursor.getString(2)), language);
                if (w != null ) {
                    wordList.add(w);
                }
            };
        }
        return wordList;
    }

    @Nullable
    private Word getWordByIdAndLanguage(int id, int language) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_ID + ", " + KEY_NAME + ", " + KEY_LANG + ", "
                + KEY_LEARNABLE + ", " + KEY_LEARN_STATE + " FROM " + TABLE_WORDS
                + " WHERE " + KEY_ID + " = " + id
                + " AND " + KEY_LANG + " = " + language;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                return new Word(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        Integer.parseInt(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3)),
                        Integer.parseInt(cursor.getString(4))
                );
            }
        }
        return null;
    }
}