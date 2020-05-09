package pl.proenix.android.us2pum.lab6lang;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class wrap Word objects.
 */
public class Word implements Parcelable {
    public static final int WORD_LANGUAGE_ENGLISH = 0;
    public static final int WORD_LANGUAGE_POLISH = 1;

    public static final int WORD_NOT_LEARNABLE = 0;
    public static final int WORD_LEARNABLE = 1;

    public static final int WORD_TO_LEARN = 0;

    private int _id;
    private String _name;
    private int _language; // 0 - English; 1 - Polish
    private int _learnable; // Is displayed in learning process or helper word.
    private int _learnState;

    private List<Word> _relatedSameLanguage;
    private List<Word> _relatedOppositeLanguage;

    public Word() {}

    public Word(int id, String name, int lang, int learnable, int learnState) {
        this._id = id;
        this._name = name;
        this._language = lang;
        this._learnable = learnable;
        this._learnState = learnState;
    }

    public Word(String name, int lang, int learnable, int learnState) {
        this._name = name;
        this._language = lang;
        this._learnable = learnable;
        this._learnState = learnState;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public int getLanguage() {
        return this._language;
    }

    public void setLanguage(Short lang) {
        this._language = lang;
    }

    public boolean isEnglish() {
        return (this._language == WORD_LANGUAGE_ENGLISH);
    }

    public boolean isPolish() {
        return (this._language == WORD_LANGUAGE_POLISH);
    }
    
    public boolean isLearnable() {
        return (this._learnable == WORD_LEARNABLE);
    }

    public int getLearnable() {
        return this._learnable;
    }
    
    public void setLearnable(int learnable) {
        this._learnable = learnable;
    }

    public int getLearnState() {
        return this._learnState;
    }

    public void setLearnState(int learnState) {
        this._learnState = learnState;
    }

    public void setAsLearned() {
        this._learnState += 1;
        this.save();
    }
    public void setAsNotLearned() {
        this._learnState = WORD_TO_LEARN;
        this.save();
    }

    /**
     * Return Opposite Language value.
     * @return WORD_LANGUAGE_POLISH|WORD_LANGUAGE_ENGLISH value.
     */
    public int getOppositeLanguage() {
        if (this._language == WORD_LANGUAGE_ENGLISH) {
            return WORD_LANGUAGE_POLISH;
        } else {
            return WORD_LANGUAGE_ENGLISH;
        }
    }

    /**
     * Save or Update in database.
     */
    public void save() {
        MainActivity.db.updateWord(this);
    };

    /**
     * Get related Words for this Word. Only opposite language.
     * @return List of Word objects.
     */
    public List<Word> getRelatedWordsOtherLanguage() {
        if (this._relatedOppositeLanguage == null) {
            this._relatedOppositeLanguage = MainActivity.db.getRelatedWordsByIdAndLanguage(this._id, this.getOppositeLanguage());
        }
        return this._relatedOppositeLanguage;
    }

    /**
     * Get related Words for this Word. Only same language.
     * @return List of Word objects.
     */
    public List<Word> getRelatedWordsSameLanguage() {
//        List<Word> relatedSameLanguage = new ArrayList<Word>();
//        relatedSameLanguage.add(this);
//        return relatedSameLanguage;
//        // TODO: 08/05/2020 Add support to synonyms in same la
        if (this._relatedSameLanguage == null) {
            this._relatedSameLanguage = MainActivity.db.getRelatedWordsByIdAndLanguage(this._id, this._language);
        }
        return this._relatedSameLanguage;
    }

    protected Word(Parcel in) {
        _id = in.readInt();
        _name = in.readString();
        _language = in.readInt();
        _learnable = in.readInt();
        _learnState = in.readInt();
        if (in.readByte() == 0x01) {
            _relatedSameLanguage = new ArrayList<Word>();
            in.readList(_relatedSameLanguage, Word.class.getClassLoader());
        } else {
            _relatedSameLanguage = null;
        }
        if (in.readByte() == 0x01) {
            _relatedOppositeLanguage = new ArrayList<Word>();
            in.readList(_relatedOppositeLanguage, Word.class.getClassLoader());
        } else {
            _relatedOppositeLanguage = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_name);
        dest.writeInt(_language);
        dest.writeInt(_learnable);
        dest.writeInt(_learnState);
        if (_relatedSameLanguage == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(_relatedSameLanguage);
        }
        if (_relatedOppositeLanguage == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(_relatedOppositeLanguage);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Word> CREATOR = new Parcelable.Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    /**
     * Add progress to word learning.
     */
    public void levelUp() {
        this._learnState += 1;
        this.save();
    }

    /**
     * Remove progress from word learning.
     */
    public void levelDown() {
        this._learnState -= 1;
        this.save();
    }
}
