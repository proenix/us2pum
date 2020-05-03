package pl.proenix.android.us2pum.lab6lang;

/**
 * Class wrap Word objects.
 */
public class Word {
    public static final int WORD_LANGUAGE_ENGLISH = 0;
    public static final int WORD_LANGUAGE_POLISH = 1;

    public static final int WORD_NOT_LEARNABLE = 0;
    public static final int WORD_LEARNABLE = 1;


    private int _id;
    private String _name;
    private int _language; // 0 - English; 1 - Polish
    private int _learnable; // Is displayed in learning process or helper word.
    private int _learnState;

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
}
